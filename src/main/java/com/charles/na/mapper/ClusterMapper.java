package com.charles.na.mapper;

import com.charles.na.model.Cluster;

import java.util.List;

/**
 * @author huqj
 */
public interface ClusterMapper {

    int insertOne(Cluster cluster);

    int insertMany(List<Cluster> clusters);

    List<Cluster> findByClusterId(String id);

    List<Cluster> findByDate(String date);
}
