cb-proto
----
Sample Scala http service demonstring use of Scala
- [refined types](https://github.com/fthomas/refined)
- [value types with newtype wrapper](https://github.com/estatico/scala-newtype)
- [postgres skunk](https://github.com/tpolecat/skunk)
- [functional Configuration](https://cir.is/)

## Description
Prototype for a quick Scala [http4s](https://http4s.org/) with [skunk](https://github.com/tpolecat/skunk) service

## Prerequisite
- Java 11 or better
- [docker-compose](https://docs.docker.com/compose/)
- [sbt](https://www.scala-sbt.org/)

## Usage
- Code
- Database
- Running the app

### Code

``` sh
git clone git@github.com:kayvank/cb-proto.git
sbt compile
sbt \~test
```

### Database
- Start db
- Monitor logs
- Connect to DB
- Create the User table

#### Start database

We use Postgress for this project. 
*My dev machine is Archlinux. To avoid file permission issues:*

``` sh
cd db
mkdir pg_data ## required 
CURRENT_UID=$(id -u):$(id -g) docker-compose up
```
#### Database logs
Docker compose is set to log the queries.  To tail the queries:

``` sh
docker ps ## to get the docker PS id o 
docker logs -f PSID

```

#### Connect to DB
To connect to Postgress instance:

``` sh
docker inspect PSID ## extract the IP address 
psql -h IPADDRESS -U q2io 
```
#### Creat the User table

``` sh
psql -h IPADDRESS -U q2io  < ./tables.sql
```

### Running the app
Prerequisites for running the app:
- environment variables, as described in next section
- postgreSql instance, as described in previous sections

Next to execute the app:
``` sh
sbt clean compile bootstrap/run
```
#### Environment variables

``` sh
export APP_ENV=dev
export GOOGLE_API_KEY='some-key'
export PASSWORD_SALT='SAL123'
export POSTGRES_HOST='172.18.0.1'
export POSTGRES_USER_NAME=q2io
export POSTGRES_PASSWORD=password
export POSTGRES_DATABASE=q2io
```

## PostgreSql
For sample payload and tests see the [RestClient](https://github.com/pashky/restclient.el) script [Spec.http](./modules/scripts/Spec.http)

## Reference
tbd
