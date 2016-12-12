# Streaming Data Platform - Enron Emails V2 xml # 

![ScreenShot](https://github.com/fjbecerra/data-platform-enron-emails/blob/master/images/data-platform-stack.PNG)

***

- **ENRON-CONNECTOR**

**Generates events feeding .ZIP files (.XML format)** off a local folder linked to **AWS ESB remotely**. **Transforms XML's** document tag + the content of its .TXT file (regardless it's an txt attachment) into **AVRO format** in *streaming* and send them to **Kafka**.

Files are processed **asynchronously**, taking advantage of the Hazelcast's **ExecuteService destributed**. Allowing to scale it out as needed.

***

- **KAFKA & SCHEMA REGISTRY**

Kafka streams Avro files. Each Avro represents an single email details + body message.

Schema Registry, *tracks Avro schema versions*. UI. http://<ADVERTISER HOST>:3030

***

- **ENRON-CONSUMER on SPARK. (1 batch, 1 stream jobs)**

Batch job creates Cassandra table.

The streaming job running on Spark, feeds messages from Kafka. It uses the spark streaming api, gets rid of the .**TXT** attachments.

**Calculates the number of word within the body message per RDD**, cleaning static things, such as company signature, automatic words generated by the mail service, numbers, asteriks...

**Figures out the email of each recipient** and calculates the relevancy. 1 for TO and 0.5 for CC and BCC recipients.

Sinks the results in Cassandra, using the Cassandra connector for Spark.

***
 
- **CASSANDRA**

Contains analitics tables. 

Mail table. Contains the mail details + number of words within a message.
Recipient_order_by_relevant. Contains all the existing unique recipients + their total relevancy.

***

- **ZEPPELIN**

Data visualization, allowing business users, developers, analytics run their scripts.

***

### Requiremnts:

 - JAVA 8, 7 and SCALA 2.10
 
 - Docker 1.+
 
 - Docker Compose 1.+
 
 - Apache maven 3.+
 
 - sshfs - mount a remote system - 

***

### Configuration:

- **Mounting a romete system to a local folder. - Description for AWS -**

1.Mount an AWS ESB and restore the Enron Emails Snapshop. 
 
2.In order to read those files in ESB we mount a folder in our local host remotely using sshdf.
 
```
$ sudo apt-get install sshfs
```

SSHFS is a very powerful tool but its commands are a bit naughty. 

Login as **Root** user. Pay attention the **full path of your public/private key** and **no slash** at the end of your target local.
 
```
# sshfs -o identityfile=<full path of your private key>.pem <instance user>@<instance aws public dns>:/<aws esb mount path>/edrm-enron-v2/ /<target local folder>
```

---
- **Editing docker-compose.yml**.

1.Avro schema registry needs an __advertiser host__, so, we replace the value of all the environment variables called **KAFKA_SCHEMA_REGISTRY** to **http://<host machine ip >:8081**
 
2.For enron-connector to read files, in **enron-connector container -> volumes**, we replace the path prefix **<target local folder>:/enron/input** to the target local folder (you configured this at the sshfs step). 
 
---
- **Building the jars. We have to build it separately due to enron-connector should be built with JAVA 8 and enron-consumer with JAVA 7 since it is written in SCALA 2.10.**

(Assuming JAVA 8 is by default)

1.Enron connector:

```
$ mvn -f enron-connector\pom.xml clean install
```

2.Enron consumer:
 
```
$ export JAVA_HOME = "<JAVA 7 install folder location>"
```
```
$ mvn -f enron-consumer\pom.xml clean install
```

- **Running Everything**

Execute the bash file with name start-data-platform.sh

```
$ ./start-data-platform.sh
```  

---
###Results

Execute job to calculate the result once the data has landed to cassandra. **Update your local folder to be mounted if needed**


```
docker exec -i -t spark-master bash spark-submit --class com.fjbecerra.sql.AggregationJob --master local[*] --driver-memory 1G --executor-memory 1G /app/enron-consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
```

Quetion 1.

select avg(words_within_body) from enron.mail;

![ScreenShot](https://github.com/fjbecerra/data-platform-enron-emails/blob/master/images/avg.PNG)


Question 2.

select recipient_id, relevant from enron.recipients_order_by_relevant limit 100;

![ScreenShot](https://github.com/fjbecerra/data-platform-enron-emails/blob/master/images/topRelevant.png)


