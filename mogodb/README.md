# Elastos.ORG.DID.Explorer.Api
## Summary

This repo provide HTTP Restful API for DID explorer.

## Build with maven

In project directory, use maven command:
```shell
$uname mvn clean compile package
```
If there is build success, Then the package did.backend.service-0.0.6.jar will be in target directory.

## Configure project properties
In project directory, open ./src/resources/application.properties

### Configure database
This project use database create by project:[Elastos.ORG.API.Misc](https://github.com/elastos/Elastos.ORG.API.Misc)
Change spring.datasource to your database.like:
```yaml
spring.datasource.url=jdbc:mariadb://localhost:3306/chain?useUnicode=true&characterEncoding=UTF-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=12345678
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```

### Configure did side chain address
Change "node.didPrefix" to your did side chain node url.

## Run

Copy did.backend.service-0.0.6.jar to your deploy directory.
then use jar command to run this spring boot application.
```shell
$uname java -jar did.backend.service-0.0.6.jar
```

## Web Service APIs

### Get all properties of a did
```yaml
HTTP: GET
URL: /api/1/didexplorer/did/{did}[/status/{all/normal/deprecated}][?page={page_number}&size={size_number}&detailed={true/false}]
HEADERS:
    Content-Type: application/json
return:
    成功: {
            "status":200,
            "result": [{\"key\":\"del_in_middle\",\"value\":\"test_1543817023\"},{\"key\":\"123\",\"value\":\"test_1543817020\"},{\"key\":\"del_in_end\",\"value\":\"test_1543817024\"},{\"key\":\"wid\",\"value\":\"100000380\"},{\"key\":\"publicKey\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}
         };
    失败: {"status":400, "result":"Err msg"}
```

We can get all properties of a did from this api. But if the did is deprecated, there will be return a empty string in result.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt
```
then you can get all normal properties of the did("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt").

or you can plus status in url to get properties in some special status.
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/status/all
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/status/normal
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/status/deprecated
```

If Success, we will get response like:
```json
{
    "result": "[{\"key\":\"del_in_middle\",\"value\":\"test_1543817023\"},{\"key\":\"123\",\"value\":\"test_1543817020\"},{\"key\":\"del_in_end\",\"value\":\"test_1543817024\"},{\"key\":\"wid\",\"value\":\"100000380\"},{\"key\":\"publicKey\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}]",
    "status": 200
}
```

### Get a property of a did 
```yaml
HTTP: GET
URL: /api/1/didexplorer/did/{did}/property?key={key_name}
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status": 200,
        "result": [{\"key\":\"property_key_1\",\"value\":\"test_1543817023\"}]
        }
    失败: {"status":400, "result":"Err msg"}
```

We can get a property of a did from this api.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/property?key=property_key_name
```
If Success, we will get property value of property "property_key_name" in did "ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt"

It response like:
```json
{
    "result": "[{\"key\":\"property_key_name\",\"value\":\"test_1543817023\"}]",
    "status": 200
}
```

### Get a property history of a did 
```yaml
HTTP: GET
URL: /api/1/didexplorer/did/{did}/property_history?key={key_name}[&page={page_number}&size={size_number}]
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status":200,
        "result":  [{\"key\":\"property_key_1\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"},{\"key\":\"property_key_1\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}]
    }
    失败: {"status":400, "result":"Err msg"}
```

We can get history of a property from this api.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/property_history?key=property_key_name&page=3&size=2
```
If Success, we will get history of property key which is "property_key_name" in did "ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", in page 3.

It response like:
```json
{
    "result": "[{\"key\":\"property_key_name\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"},{\"key\":\"property_key_name\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}]",
    "status": 200
}
```

### Get properties which key contains some string of a did 
```yaml
HTTP: GET
URL: /api/1/didexplorer/did/{did}/property_like?key={key_name}[&blockheightmin={height}]
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status": 200,
        "result": [{\"key\":\"property_key_1\",\"value\":\"test_1543817023\"}]
        }
    失败: {"status":400, "result":"Err msg"}
```

We can get properties which property_key contains key_name of a did from this api.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/property_like?key=name
```
If Success, we will get all properties which property_key contains "name" in did "ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt"

It response like:
```json
{
    "result": "[{\"key\":\"property_key_name\",\"value\":\"test_1543817023\"}]",
    "status": 200
}
```

### Get properties history which key contains some string of a did 
```yaml
HTTP: GET
URL: /api/1/didexplorer/did/{did}/property_history_like?key={key_name}[&blockheightmin={height}&page={page_number}&size={size_number}]
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status":200,
        "result":  [{\"key\":\"property_key_1\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"},{\"key\":\"property_key_1\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}]
    }
    失败: {"status":400, "result":"Err msg"}
```

We can get some properties history of a did from this api.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/property_history_like?key=name&page=3&size=2
```
If Success, we will get history of properties which key contains "name" in did "ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", in page 3.

It response like:
```json
{
    "result": "[{\"key\":\"property_key_name\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"},{\"key\":\"property_key_name\",\"value\":\"02EB87E5147DB8CA09B3A65FC2EA2E650982726A25ADB0856C73AAA23395D69CD9\"}]",
    "status": 200
}
```

### Get properties which has the same property_key 
```yaml
HTTP: GET
URL: /api/1/didexplorer/property?key={key_name}[&page={page_number}&size={size_number}]
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status": 200,
        "result": [{\"did\":\"ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt\",\"key\":\"property_key_1\",\"value\":\"test_1543817023\"}]
        }
    失败: {"status":400, "result":"Err msg"}
```

We can get all properties which has the same property_key.

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did/ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt/property?key=property_key_name
```
If Success, we will get property list which has the same property key which is "property_key_name".

It response like:
```json
{
    "result": "[{\"key\":\"property_key_name\",\"value\":\"test_1543817023\"}]",
    "status": 200
}
```

### Get properties of a did app
```yaml
HTTP: GET
URL: /api/1/didexplorer/did_app?appid={app_id}
HEADERS:
    Content-Type: application/json
return:
    成功: {
        "status": 200,
        "result": {\"did\":\"ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt\",\"public_key\":\"0245E44ACDF97CD8B676C064B373B4FDD246456F3B391FC21BAF515A4E49FD5E24\",\"app_id\":\"E9AC59878569C187DF42B602A8FCBC2F439CB8769C71D7A4ABE913ECCBE8FEA26DF7457BFCC478CC81A92780584990DEEF7776E2E33B604F1DE3FF62308E2121\"}
        }
    失败: {"status":400, "result":"Err msg"}
```

We can get did and public key of a did app by appid

For example we get from our local service like this:
```url
http://localhost:8093/api/1/didexplorer/did_app?appid=E9AC59878569C187DF42B602A8FCBC2F439CB8769C71D7A4ABE913ECCBE8FEA26DF7457BFCC478CC81A92780584990DEEF7776E2E33B604F1DE3FF62308E2121
```
If Success, we will get property list which has the same property key which is "property_key_name".

It response like:
```json
{
    "result": "{\"did\":\"ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt\",\"public_key\":\"0245E44ACDF97CD8B676C064B373B4FDD246456F3B391FC21BAF515A4E49FD5E24\",\"app_id\":\"E9AC59878569C187DF42B602A8FCBC2F439CB8769C71D7A4ABE913ECCBE8FEA26DF7457BFCC478CC81A92780584990DEEF7776E2E33B604F1DE3FF62308E2121\"}",
    "status": 200
}
```

