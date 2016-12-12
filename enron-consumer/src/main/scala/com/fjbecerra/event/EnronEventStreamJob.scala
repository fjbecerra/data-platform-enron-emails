package com.fjbecerra.event


import java.util.Calendar

import com.fjbecerra.mail.Mail._
import com.fjbecerra.mailrecord.MailRecord
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificData
import com.fjbecerra.event.ConfigStream._
import org.apache.spark.streaming.{Seconds, StreamingContext, _}
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkConf
import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector._
import com.fjbecerra.mail.Recipient
import com.fjbecerra.mail.Recipient._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.streaming.dstream.{ConstantInputDStream, DStream}


/**
  * Reads upcoming messages and update recipient's relevancy state - regardless order -
  * and count word message within an
  * email
  */
object EnronEventStreamJob {

  final case class MailDetail(mail_id :String, subject :String, sent_date :Long ,email_type :String, words_within_body:Long)
  final case class RecipientWithState(recipient_id: String, relevant: Double)


  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("com.datastax").setLevel(Level.WARN)
    val sparkConf = new SparkConf().
      setAppName("Process in streaming events coming from kafka and persist them in cassandra")
      .setMaster("local[2]")
      .set("spark.cassandra.connection.host", sys.env(prop.getString("cassandra.host")))
      .set("spark.cassandra.connection.keep_alive_ms", "60000") // prevent cassandra connection from being closed after every write

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val sc = ssc.sparkContext

    val initialState = ssc
      .cassandraTable[(String,Double)]("enron", "recipients_state").select("recipient_id", "relevant")
      .map { case (mail,  countValue) => (mail, countValue) }


    val countByMail = StateSpec.function(updateState _).initialState(initialState)

    val messages = kafkasStreaming(ssc)
    val mails = messages.map(_._2.asInstanceOf[GenericRecord]) map (SpecificData.get().deepCopy(MailRecord.SCHEMA$, _).asInstanceOf[MailRecord])

    //mails with no from in are txt attachments.
    val filteredStream = mails.filter { mail => filterTxtAttachment(mail.getFrom) }

    saveEmailDetails(filteredStream)

    val toStream = filteredStream.filter(mail => Option(mail.getTo).nonEmpty).map(_.getTo).map(list => composeListOfEmails(list.toArray, 1.0))
    val ccStream = filteredStream.filter(mail => Option(mail.getCc).nonEmpty).map(_.getCc).map(list => composeListOfEmails(list.toArray, 0.5))
    val bccStream = filteredStream.filter(mail => Option(mail.getBcc).nonEmpty).map(_.getBcc).map(list => composeListOfEmails(list.toArray, 0.5))
    val totalStream = toStream union ccStream union bccStream


    val parsedStream = totalStream.map {
      case Vector((mail, relevant)) => (mail, relevant)
      case _ => ("", 0.0)
    }.filter(!_._1.isEmpty)
    val updatedStream = parsedStream.map {
      case (email, relevant) => (email, relevant)
    }.mapWithState(countByMail)
    updatedStream.cache() //


    // updatedStream contains all the entries emmited by trackStateFunc (one entry for each entry in the original dstream. might have multiple entries per key)
    // use this to update the state in Cassandra.
    // once sorted and printed
    updatedStream
      .transform(rdd => rdd.sortBy { case (_, count) => -count })
      .print(10)

    // once saved to Cassandra
    updatedStream map { case (mail, relevant) => RecipientWithState(mail, relevant) } saveToCassandra("enron", "recipients_state")

    updatedStream.cache



    ssc.checkpoint(System.getProperty("java.io.tmpdir"))
    ssc.start()
    ssc.awaitTermination()
  }




  /**
    * Words counts per message
    *
    * @param stream
    */
  def saveEmailDetails(stream: DStream[MailRecord]){

    val wordparsedStream = stream.map(mail => {
      val words =  exctractBodyMessage(mail.getBody)
      val union = concatLines(words)
      val nWords = countWords(union)
      val typeEmail = emailType(mail.getSubject).get
      MailDetail(mail.getUuid, mail.getSubject,mail.getDateUtcEpoch,typeEmail,  nWords)})
    // as the updatedStream is used for multiple actions (saveToCassandra, print) it should be cached to prevent recomputation.
    wordparsedStream.cache

    // save updated stream to cassandra
    wordparsedStream.saveToCassandra("enron", "mail")
  }

  def updateState(batchTime: Time, key: (String), value: Option[(Double)], state: State[Double]): Option[(String, Double)] = {
    val sum = value.getOrElse(0.0) + state.getOption.getOrElse(0.0)
    val output = (key, sum)
    state.update(sum)
    Some(output)
  }



}