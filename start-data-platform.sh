#!/bin/bash
echo "starting kafka and cassandra"
docker-compose up -d kafka cassandra
echo "kafka and cassandra running"

echo "starting enron-connector"
sleep 3m && docker-compose up -d enron-connector
echo "enron-connector running"

echo "starting spark and zeppelin"
sleep 1m && docker-compose up -d spark-master zeppelin
echo "spark and zeppelin running"
