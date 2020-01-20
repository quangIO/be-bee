# Building

## Manual
The program uses jooq for *type-safe* SQL queries.
### Database
* Setup PostgreSQL
* Setup the schema (from 2 .sql files) or load the whole dump file
* Modify connection configuration in build.gradle file.
* Set the JDBC_DATABASE_URL env variable accordingly
### Run the server
```
./gradlew generateBeeJooqSchemaSource
./gradlew build
java -jar build/libs/*-all.jar
```
## Plug-n-Play DevBox
Install Vagrant (https://www.vagrantup.com/)
```
curl https://pastebin.com/raw/GVKmKVJU >Vagrantfile
vagrant up
```
Navigate to localhost:*8080* (for the frontend)

The *server* runs on port 8888 by default

# How thing works
* Basic REST API using VertX
    * The bees, in fact, do not need to use http protocol but
    just for easy testing and stuff -> http 1.1 (in-efficient but does
    not really matter)

* Uses websocket for notification
    * In case we have very frequent data updates -> we are doomed
    * Can be fix with polling and batching
    * http2 streaming can be more efficient (but some cloud providers have not supported it)

* The frontend just updates everything on receiving notification
    * -> inefficient
    * Can use delta patching instead
        * soundness problem as it needs to send and receive packets in correct order for both websocket
        and http protocol -> need message queue/broker...
        * time consuming
        * polling + batching is better but I just want it to be simple
        enough here

* SQL is slow for analytics? Using ClickHouse instead of PostgreSQL will
likely to work (need to change the dialect maybe?)
