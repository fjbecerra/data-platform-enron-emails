package com.fjbecerra.services.kafka;


import com.fjbecerra.mailrecord.MailRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;



public interface BrokerService  extends Serializable {
     RecordMetadata sendToKafka(MailRecord message, String topic) throws ExecutionException, InterruptedException;
}
