Dialob backend services
=======================

# Running locally

## Create local database service

### Postgres
```
docker run --name dialob-postgres -p 5432:5432 -e POSTGRES_PASSWORD=password123 -e POSTGRES_DB=dialob -d postgres
```
 
### CouchDB

#### install CouchDB MacOSX
```
brew install couchdb
couchdb
```
Couchdb tool is now on [http://localhost:5984/_utils](http://localhost:5984/_utils)

#### Run CouchDB in container
```
docker run -p5984:5984 couchdb:3.1.0
```

### MongoDB

#### Run mongodb container
```
docker run -p 27017:27017 --name mongo mongo
```
Mongo admin database url is `mongodb://localhost:27017`. Dialob database is `mongodb://localhost:27017/dialob`

#### mongo shell

`docker exec -it mongo mongo --shell`

 - select database
```
> use dialob
switched to db dialob
```

 - select database
```
> use dialob
switched to db dialob
```

#### Run mongoclient
```
docker run -p 3000:3000 --link mongo:mongo -e MONGO_URL=mongodb://mongo:27017 mongoclient/mongoclient
```
Mongo client url [http://localhost:3000](http://localhost:3000)


### Redis

#### Install Redis with brew
```
brew install redis
redis-server
```

#### Run redis container

```
docker run -p6379:6379 redis
```
You can monitor redis server with command `redis-cli monitor`.

# Vagrant

#### Start vagrant virtual machine
```bash
vagrant up
```

#### Copy application jars to virtual machine
```
scp dialob-boot/target/dialob-boot-*.jar vagrant:dialob-boot.jar
scp dialob-session-boot/target/dialob-session-boot-*.jar vagrant:dialob-session-boot.jar
```

### Update license headers

```
mvn license:format
```

### Additional documentation

- [Using S3 data storage](docs/S3.md)
- [CSV data query api](docs/csv-api.md)



