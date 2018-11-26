# news-analyse-1.0
version 1.0 of news analyse system, version 2.0 will be a distributed system

- main function
  - catch news data from web periodlly
  - cluster analyse
  - topic extract and track
  - visualize impression
  
- design
  - data collection subsystem(spider)
  - pre-processing subsystem
  - cluster and topic subsystem
  - impression web subsystem
  - configuration subsystem

- start instruction
```
mvn package -DskipTests
bin/start.sh <option> [<date>]
```
  
- project structure
```
.
├── README.md
├── pom.xml
├── bin
    ├── start.sh
    ├── stop.sh
├── document
├── logs
├── python
├── src
    ├── main
        ├── java
            ├── common
                ├── ...
            ├── controller
                ├── ...
            ├── mapper
                ├── ...
            ├── model
                ├── ...
            ├── service
                ├── ...
            ├── soa
                ├── ...
            ├── spider
                ├── ...
            ├── utils
                ├── ...
            ├── web
                ├── ...
            ├── AnalyseApplication.java
        ├── resources
            ├── mybatis.xml
            ├── applicationContext.xml
            ├── druid.xml
            ├── jdbc.properties
            ├── jdbc.properties.online
            ├── jdbc.properties.test
            ├── log4j.properties
            ├── mongo.properties
            ├── mybatis-config.xml
            ├── redis.properties
            ├── spring-mvc.xml
            ├── zookeeper.properties
        ├── webapp
    ├── test

```
