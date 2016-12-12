package com.fjbecerra.services.kafka;


import com.fjbecerra.config.KafkaConfig;
import com.fjbecerra.mailrecord.MailRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;


public class KafkaService implements BrokerService {

    private static final Logger LOGGER = Logger.getLogger( KafkaService.class.getName() );


    @Override
    public RecordMetadata sendToKafka(MailRecord message, String topic) throws ExecutionException, InterruptedException {
        return KafkaConfig.INSTANCE.send(message,topic);

    }


}
