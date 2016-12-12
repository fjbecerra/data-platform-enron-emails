package com.fjbecerra;

import com.fjbecerra.config.KafkaConfig;
import com.fjbecerra.services.publishers.EventPublisherAvro;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnronConnectorApplication implements CommandLineRunner{

	private static final Logger LOGGER = Logger.getLogger( EnronConnectorApplication.class.getName());

	@Autowired
	private EventPublisherAvro eventPublisherAvro;

	@Value("${file.path}")
	private String path;

	@Value("${brokers}")
	private String brokers;

	@Value("${schemaRegistry}")
	private String shemaRegistry;

	@Value("${batch.size}")
	private Integer batchSize;

	@Value("${max.request.size}")
	private Integer requestSize;

	@Value("${message.max.bytes}")
	private Integer messageMaxSize;

	public static void main(String[] args) {
		SpringApplication.run(EnronConnectorApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		KafkaConfig.INSTANCE.init(brokers, shemaRegistry, batchSize, requestSize, messageMaxSize);
		eventPublisherAvro.startProduceEvent(path);
		LOGGER.info("Completed. All the data loaded");
	}
}
