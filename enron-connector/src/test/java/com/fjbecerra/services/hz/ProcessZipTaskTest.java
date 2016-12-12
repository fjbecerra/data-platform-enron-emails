package com.fjbecerra.services.hz;

import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.services.files.FileService;
import com.fjbecerra.services.streams.IXmlStreamService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.text.ParseException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ProcessZipTaskTest {

    @InjectMocks
    private ProcessZipTask processZipTask;

    @Mock
    private IXmlStreamService transformerService;

    @Mock
    private FileService fileService;

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenZipPathIsNullThenTaskIsNotExecuted() throws IOException, EventPublisherException, ParseException, XMLStreamException {
        processZipTask.run();
        verify(transformerService, times(0)).transformAndProduceEvent("");

    }

    @Test
    public void whenZipPathIsSetThenTaskIsExecuted() throws IOException, EventPublisherException, ParseException, XMLStreamException {
        processZipTask.setZipPath("any path");
        processZipTask.run();
        verify(transformerService, times(1)).transformAndProduceEvent("any path");

    }
}
