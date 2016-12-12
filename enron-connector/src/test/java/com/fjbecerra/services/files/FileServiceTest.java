package com.fjbecerra.services.files;


import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.services.streams.ZipperService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.assertEquals;



public class FileServiceTest {

    @InjectMocks
    private IFileService fileService = new FileService();

    @Mock
    private ZipperService zipperService;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenADirectoryWhenFilterThenReturnOnlyOneName() throws URISyntaxException, MalformedURLException, EventPublisherException {
        List<String> files = fileService.listFilesInAFolder("src/test/resources/", "_xml.zip");
        assertEquals(files.size(), 1);
    }

    @Test
    public void givenADirectoryWithSplitZipFilesWhenFilterThenReturnOnlyOneName() throws URISyntaxException, MalformedURLException, EventPublisherException {
        List<String> files = fileService.listFilesInAFolder("src/test/resources/split", "_xml.zip");
        assertEquals(files.size(), 1);
    }

    @Test
    public void givenAdDirectoryWhenWrongFilterThenReturnEmptyListOfFiles() throws EventPublisherException {
        List<String> files = fileService.listFilesInAFolder("src/test/resources/", "ddd.");
        assertEquals(files.size(), 0);
    }

    @Test(expected = EventPublisherException.class)
    public void givenDirectoryIfFileNotExistThenThrowException() throws EventPublisherException {
        List<String> files =fileService.listFilesInAFolder("wrong path", "_xml.");
        assertEquals(files.size(), 0);
    }


    @Test
    public void givenAFileWhenReadingItThenRetrieveContent() throws IOException {
        assertEquals("hello world",fileService.pullContentFromInputStream(new FileInputStream("src/test/resources/3.856680.JUXNPBRMAWZNOCA0LIVNR30NSFBJZSXTB.txt")));
    }

    @Test(expected = FileNotFoundException.class)
    public void givenAFileWhenReadingItThenNotFound() throws IOException {
        fileService.pullContentFromInputStream(new FileInputStream("3.856680.JUXNPBRMAWZNOCA0LIVNR30NSFBJZSXTB.txt"));

    }

}



