package com.fjbecerra.sql

import java.util.{Calendar, Date}

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql.CassandraConnector
import com.fjbecerra.mail.Recipient
import org.apache.spark.sql.Row


object AggregationJob {

  val prop = ConfigFactory.load

  final case class TopRecipient(execution_timestamp:Date, recipient_id: String, relevant:Double)

  def main(args: Array[String]){
    val conf = new SparkConf().setMaster("local[2]").setAppName("Q1. Average of words and Q2. Top 100 relevant recipient.")
      .set("spark.cassandra.connection.host", sys.env(prop.getString("cassandra.host")))

    val sc = new SparkContext(conf)
    val csc=new CassandraSQLContext(sc)

    val cassandraConnector = CassandraConnector.apply(conf)


    cassandraConnector.withSessionDo(session => {
      session.execute("CREATE TABLE IF NOT EXISTS enron.recipients_total (execution_timestamp timestamp,recipient_id text, relevant double,PRIMARY KEY (execution_timestamp,relevant, recipient_id))WITH CLUSTERING ORDER BY (relevant DESC);")
      session.execute("CREATE TABLE IF NOT EXISTS enron.mail_word_avg (company text, words_within_body bigint, PRIMARY KEY (company));")

    })


    val dfWords=csc.sql("SELECT avg(words_within_body) from enron.mail")
    dfWords.map{ case Row(words) => ("Enron Corporation", words)}.saveToCassandra("enron", "mail_word_avg")


    val timestamp = Calendar.getInstance.getTime
    val dfRelevant=csc.sql("SELECT recipient_id, relevant from enron.recipients_state")

    dfRelevant.map{ case Row(recipient, relevant) => TopRecipient(timestamp,recipient.toString, relevant.asInstanceOf[Double])}.saveToCassandra("enron", "recipients_total")



  }
}
