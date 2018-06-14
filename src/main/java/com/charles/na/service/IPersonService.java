package com.charles.na.service;

/**
 * 在判断热点人物的时候会用到的服务，
 * 通过查询百度百科来判断该人名是不是热点人名
 *
 * @author huqj
 */
public interface IPersonService {

    /**
     * 判断一个人名是不是在百科中存在
     *
     * @param people
     * @return
     */
    boolean isFamousPerson(String people);

}
