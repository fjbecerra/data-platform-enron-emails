package com.fjbecerra.event

import com.typesafe.config.ConfigFactory
import io.confluent.kafka.serializers.KafkaAvroDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils



object ConfigStream {

  val prop = ConfigFactory.load

  def kafkasStreaming(ssc:StreamingContext) = {

    val topicsSet = sys.env(prop.getString("topics")).split(",").toSet
    val kafkaParams = Map[String, String](
      "auto.offset.reset" -> sys.env(prop.getString("offset.reset")),
      "metadata.broker.list" -> sys.env(prop.getString("brokers")),
     "schema.registry.url" -> sys.env(prop.getString("schemaRegistry")),
     "max.partition.fetch.bytes" -> sys.env(prop.getString("max.partition.fetch.bytes")))


      KafkaUtils.createDirectStream[Object, Object, KafkaAvroDecoder, KafkaAvroDecoder](
      ssc, kafkaParams, topicsSet)
  }

}
