package com.charles.na.spider.ds;

import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 使用zk实现优先消费队列
 *
 * @author huqj
 */
public class PriorityQueue implements Watcher, Comparator {

    /**
     * 写入zk的根目录名称
     */
    @Setter
    @Getter
    private String queuePath = "/queue";

    private int timeout;

    private ZooKeeper zooKeeper;

    /**
     * 当有数据可供消费的时候，调用的回调函数列表，按照调用consume函数的先后顺序调用
     */
    private List<Consumer<byte[]>> consumerList = new ArrayList<>();

    public PriorityQueue(String connectString, int timeout, String queuePath) {
        this.queuePath = queuePath;
        this.timeout = timeout;
        init(connectString);
    }


    /**
     * 生产数据
     *
     * @param content  生产的内容
     * @param priority 消息的优先级
     */
    public synchronized boolean produce(byte[] content, int priority) {
        try {
            //创建生产者节点生产数据，节点名称为: data-优先级-递增顺序
            zooKeeper.create(queuePath + "/data-" + priority + "-", content,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 消费数据，调用者传入回调函数，每次有数据可消费就回调
     *
     * @return 消费内容
     */
    public void consume(Consumer<byte[]> consumer) {
        consumerList.add(consumer);

    }

    private void init(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString, timeout, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //若队列znode根目录不存在则创建
        try {
            if (zooKeeper.exists(queuePath, false) == null) {
                createZNodeWithParent(queuePath);
            }
            //开始监听生产数据
            zooKeeper.getChildren(queuePath, true);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听到有新的数据可以消费，就调用consumer
     *
     * @param watchedEvent
     */
    @Override
    public synchronized void process(WatchedEvent watchedEvent) {
        try {
            List<String> children = zooKeeper.getChildren(queuePath, false);
            //没有数据或者没有消费者订阅数据
            if (children.size() == 0 || consumerList.size() == 0) {
                //这里不能直接return，在finally中还要再注册
                return;
            }
            Collections.sort(children, this);
            for (String child : children) {
                String path = queuePath + "/" + child;
                byte[] data = zooKeeper.getData(path, false, null);
                zooKeeper.delete(path, zooKeeper.exists(path, false).getVersion());
                for (Consumer c : consumerList) {
                    c.accept(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //删除之后再监听子节点变化事件,否则删除也会触发该函数，导致重复消费
            try {
                zooKeeper.getChildren(queuePath, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 比较两个数据哪个应该被先消费
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Object o1, Object o2) {
        try {
            String[] s1 = ((String) o1).split("-");
            String[] s2 = ((String) o2).split("-");
            if (s1.length != 3 && s2.length != 3) {
                return 0;
            }
            //先看优先级
            Integer p1 = Integer.parseInt(s1[1]);
            Integer p2 = Integer.parseInt(s2[1]);
            if (p1 > p2) {
                return -1;
            } else if (p1 < p2) {
                return 1;
            }
            //同一优先级下看生产顺序
            Integer seq1 = Integer.parseInt(s1[2]);
            Integer seq2 = Integer.parseInt(s2[2]);
            return seq1 - seq2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 和父节点一起递归创建新znode节点
     */
    private void createZNodeWithParent(String path) throws KeeperException, InterruptedException {
        if (path == null || !path.startsWith("/")) {
            return;
        }
        //先判断父节点是否存在
        boolean canCreate = false;
        String parent = null;
        if (path.lastIndexOf("/") == 0) {
            canCreate = true;
        } else {
            parent = path.substring(0, path.lastIndexOf("/"));
            if (zooKeeper.exists(parent, false) != null) {
                canCreate = true;
            }
        }
        //父节点存在则直接创建，否则先创建父节点
        if (!canCreate) {
            createZNodeWithParent(parent);
        }
        zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
