# CodeTest-Crosskey
## Java

Build instructions
```
git clone https://github.com/moggesmith10/CodeTest-Crosskey/
cd CodeTest-Crosskey
mvn package
```
Run (classpath)
```
java -Dfile.encoding=UTF-8 -classpath target/classes Mortage_Plan prospects.txt
```
or (jar)
```
java -Dfile.encoding=UTF-8 -jar target/CodeTest-Crosskey-1.0-SNAPSHOT.jar prospects.txt
```

## Docker
Build image (Run in CodeTest-Crosskey folder)
```
docker build .
```
Create container
```
docker create {imageID}
```
Run container
```
docker start -a {containerID}
```
