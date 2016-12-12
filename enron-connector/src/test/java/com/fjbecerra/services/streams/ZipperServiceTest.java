package com.fjbecerra.services.streams;


import com.fjbecerra.services.files.FileService;
import com.fjbecerra.services.streams.IZipperService;
import com.fjbecerra.services.streams.ZipperService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.io.*;


import static org.junit.Assert.*;


public class ZipperServiceTest {
    private IZipperService zipperService;

    private String output = "src/test/resources/output/edrm-enron-v2_townsend-j_xml.zip";

    @Before
    public void init(){
        zipperService = new ZipperService();
    }



    @Test
    public void givenZipFileTXTWithFolderThenReadTextWhichIsIn() throws IOException {
        String pattern = "((?:[a-z][a-z]+))(_)(\\d+)(\\/)(3.856680.JUXNPBRMAWZNOCA0LIVNR30NSFBJZSXTB.txt)";
        InputStream zis = zipperService.extractFrom("src/test/resources/edrm-enron-v2_townsend-j_xml.zip", pattern);
        String content = pullContentFromInputStream(zis);
        assertFalse(content.isEmpty());
    }

    @Test
    public void givenZipFileTXTWhenIsNotFoundThenContentIsEmpty() throws IOException {
        String pattern = "((?:[a-z][a-z]+))(_)(\\d+)(\\/)(3.856680.JUXNPBRMAWZNO.txt)";
        InputStream zis = zipperService.extractFrom("src/test/resources/edrm-enron-v2_townsend-j_xml.zip", pattern);
        String content = pullContentFromInputStream(zis);
        assertTrue(content.isEmpty());
    }

    @Test
    public void givenAZipWhichIsSplitThenMergeItIntoFolderSpecified() throws IOException {
        String pattern = "((?:[a-z][a-z]+))(_)(\\d+)(\\/)(3.856680.JUXNPBRMAWZNOCA0LIVNR30NSFBJZSXTB.txt)";
        InputStream zis = zipperService.extractFrom("src/test/resources/edrm-enron-v2_townsend-j_xml.zip", pattern);
        String content = pullContentFromInputStream(zis);
        assertFalse(content.isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void givenAZipWhenReadThenNotFound() throws IOException {
        zipperService.extractFrom("edrm-enron-v2_townsend-j_xml.zip", "ss");

    }

    @Test
    public void givenZipXMLThenReadXMLWhichIsIn() throws IOException {
        String pattern = ".xml";
        InputStream zis = zipperService.extractFrom("src/test/resources/edrm-enron-v2_townsend-j_xml.zip", pattern);
        String content = pullContentFromInputStream(zis);
        assertFalse(content.isEmpty());
    }

    @Test
    public void givenZipXMLAndWrongPatternThenContentIsEmpty() throws IOException {
        String pattern = ".cc";
        InputStream zis = zipperService.extractFrom("src/test/resources/edrm-enron-v2_townsend-j_xml.zip", pattern);
        String content = pullContentFromInputStream(zis);
        assertTrue(content.isEmpty());
    }


    @After
    public void after(){
        new File(output).delete();
    }


    private String pullContentFromInputStream(InputStream zis) throws IOException {
       return new FileService().pullContentFromInputStream(zis);
    }


}
