package com.fjbecerra.initialize

import com.datastax.spark.connector.cql.CassandraConnector
import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object EnronInizializer {

  val prop = ConfigFactory.load
  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("com.datastax").setLevel(Level.WARN)


    val conf = new SparkConf()
      .setAppName("Initialize Cassandra tables")
      .setMaster("local")
      .set("spark.cassandra.connection.host", sys.env(prop.getString("cassandra.host")))
    val sc = new SparkContext(conf)

    val cassandraConnector = CassandraConnector.apply(conf)


    cassandraConnector.withSessionDo(session => {
      session.execute("CREATE KEYSPACE IF NOT EXISTS enron WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} ")
      session.execute("CREATE TABLE IF NOT EXISTS enron.mail (mail_id text, subject Text, sent_date bigint , email_type text, words_within_body bigint, PRIMARY KEY (mail_id));")
      session.execute("CREATE TABLE IF NOT EXISTS enron.recipients_state (recipient_id text, relevant double,PRIMARY KEY (recipient_id))")
    })


    System.out.println("==================================================================================")
    System.out.println("Created Keyspace 'enron' with tables")
    System.out.println("==================================================================================")

    sc.stop()
  }
}
