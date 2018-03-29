package com.charles.na.model;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 文本中的关键词类
 * @since 1.0
 */
public class Words {
    /**
     * id
     */
    private String id;
    /**
     * 关键词
     */
    private String word;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Words{" +
                "id='" + id + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
