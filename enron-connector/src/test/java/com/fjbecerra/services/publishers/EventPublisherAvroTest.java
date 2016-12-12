package com.fjbecerra.services.publishers;

import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.services.files.FileService;
import com.fjbecerra.services.hz.ProcessZipTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.executor.impl.ExecutorServiceProxy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class EventPublisherAvroTest {

    @InjectMocks
    private EventPublisherAvro eventPublisherAvro = new EventPublisherAvro();

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private FileService fileService;

    @Mock
    private ProcessZipTask processZipTask;

    @Mock
    private ExecutorServiceProxy executorServiceProxy;

    @Before
    public void setUp() throws IOException, XMLStreamException, ParseException, EventPublisherException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(eventPublisherAvro, "executorName" ,"exec");

    }

    @Test
    public void givenMountFolderWhenFindFilesThenJobsHasFinishedSuccessfully() throws URISyntaxException, EventPublisherException {
        when(fileService.listFilesInAFolder(anyString(), anyString())).thenReturn(Arrays.asList("/test/test/any.zip"));
        Map<String, List<String>> map = new HashMap<>();
        map.put("any", Arrays.asList("/test/test/any.zip"));
        when(hazelcastInstance.getExecutorService(anyString())).thenReturn(executorServiceProxy);
        assertTrue(eventPublisherAvro.startProduceEvent("folder"));
    }


    @Test(expected = EventPublisherException.class)
    public void givenMountFolderThenTargetFolderIsNotSetUp() throws EventPublisherException {
        when(fileService.listFilesInAFolder(anyString(), anyString())).thenReturn(null);
        eventPublisherAvro.startProduceEvent("folder");
    }


}
