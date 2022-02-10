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
Create container (get imageID from last command)
```
docker create {imageID}
```
Run container (get containerID from last command)
```
docker start -a {containerID}
```
## AWS
Since "Public application [to AWS]" is very up to interpretation and I didn't have time to learn glassfish or similar servlet runner, i've simply made an EC2 instance running Apache serving a zip file of this appliation.
```
http://ec2-16-170-248-12.eu-north-1.compute.amazonaws.com/
```
