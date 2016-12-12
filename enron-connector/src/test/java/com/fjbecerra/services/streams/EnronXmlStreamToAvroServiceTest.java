package com.fjbecerra.services.streams;

import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.mailrecord.MailRecord;
import com.fjbecerra.services.files.FileService;
import com.fjbecerra.services.kafka.BrokerService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class EnronXmlStreamToAvroServiceTest {

    @InjectMocks
    private IXmlStreamService mailReader = new EnronXmlStreamToAvroService();

    @Mock
    private ZipperService zipperService;

    @Mock
    private BrokerService brokerService;

    @Mock
    private FileService fileService;

    private String xml = "src/test/resources/zl_townsend-j_875_MSNA_000.xml";

    @Before
    public void init() throws ParseException, XMLStreamException, IOException, EventPublisherException, ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mailReader, "topic" ,"test4");
        when(fileService.pullContentFromInputStream(any(InputStream.class))).thenReturn("hi");
        TopicPartition topicPartition = new TopicPartition("test4",0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition,0,0);
        when(brokerService.sendToKafka(any(MailRecord.class),anyString())).thenReturn(recordMetadata);

        when(zipperService.extractFrom(anyString(), anyString())).thenReturn(new FileInputStream(xml));

    }

    private MailRecord buildMailRecord(){
        MailRecord mailRecord = new MailRecord();
        mailRecord.setUuid("3.856680.JUXNPBRMAWZNOCA0LIVNR30NSFBJZSXTB");
        mailRecord.setFrom("Suzanne Calcagno");
        mailRecord.setTo(Arrays.asList("Mark Breese", "John Hodge", "Chris Germany", "Judy Townsend", "Ruth Concannon", "Robert   Superty", "Rebecca W Cantrell", "Victor Lamadrid", "Maureen Smith", "Gil Muhl"));
        mailRecord.setSubject("Transco MarketLink Phase III Rejected");
        mailRecord.setDateUtcEpoch(Long.valueOf(988622400000l));
        mailRecord.setBody("hi");
        return mailRecord;
    }


    @Test(expected = FileNotFoundException.class)
    public void givenAFileNotExistThenThrowEx() throws IOException, EventPublisherException, ParseException, XMLStreamException {
        String name = "rc/test/resources/zl_townsend-j_875_MSNA_000.xml";
        when(zipperService.extractFrom(anyString(), anyString())).thenReturn(new FileInputStream(name));
        mailReader.transformAndProduceEvent(name);
    }

    @Test
    public void givenADateWhenProcessEmeailThenVerifyEmailReadIsCorrect() throws IOException, XMLStreamException, ParseException, EventPublisherException, ExecutionException, InterruptedException {
        MailRecord mailRecord = buildMailRecord();
        mailReader.transformAndProduceEvent(xml);
        verify(brokerService, times(1)).sendToKafka(mailRecord, "test4");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mailRecord.getDateUtcEpoch());
        assertEquals(calendar.get(Calendar.YEAR), 2001);
        assertEquals(calendar.get(Calendar.MONTH), 3);
        assertEquals(calendar.get(Calendar.DATE), 30);
    }


}
