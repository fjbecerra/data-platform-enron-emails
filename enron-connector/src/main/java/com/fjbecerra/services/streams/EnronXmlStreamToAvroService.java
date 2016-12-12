package com.fjbecerra.services.streams;

import com.fjbecerra.services.files.FileService;
import com.fjbecerra.mailrecord.MailRecord;
import com.fjbecerra.services.kafka.BrokerService;
import com.fjbecerra.services.kafka.KafkaService;
import com.fjbecerra.services.publishers.EventPublisherAvro;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;


@Service
public class EnronXmlStreamToAvroService implements IXmlStreamService {

    private static final Logger LOGGER = Logger.getLogger( EventPublisherAvro.class.getName());

    @Value("${topics}")
    private String topic;

    @Autowired
    private ZipperService zipperService;

    @Autowired
    private FileService fileService;


    private BrokerService brokerService = new KafkaService();

    private final static String XML_PATTERN = ".xml";
    private final static String SOURCE_BODY_PATTERN = "((?:[a-z][a-z]+))(_)(\\d+)(\\/)";

    /**
     * Transform XML in streaming to AVRO format and send them to kafka
     * @param path
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParseException
     */
    public void transformAndProduceEvent(String path) throws XMLStreamException, IOException, ParseException {
        LOGGER.info("XML file to process" + path);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try(InputStream xmlInputStream = zipperService.extractFrom(path, XML_PATTERN)) {
            reader = inputFactory.createXMLStreamReader(xmlInputStream);
            readDocuments(reader, path);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read Read documents from xml and get rid of the remaining xml
     * @param reader
     * @param path
     * @throws XMLStreamException
     * @throws ParseException
     * @throws IOException
     */
    private void readDocuments(XMLStreamReader reader, String path) throws XMLStreamException, ParseException, IOException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Documents")) {
                        readMails(reader, path);
                    }else if(elementName.equals("Relationships")){

                        return;
                    }
                    break;

                case XMLStreamReader.END_ELEMENT:
                    break;
            }
        }
        throw new XMLStreamException("Premature end of file");
    }


    /**
     * Retrive the Document transform to Avro and sends it to Kafka
     * getting rid of txt attachments
     * @param reader
     * @param path
     * @throws XMLStreamException
     * @throws ParseException
     * @throws IOException
     */
    private void readMails(XMLStreamReader reader, String path) throws XMLStreamException, ParseException, IOException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Document")) {
                        MailRecord mailRecord = readDocument(reader);
                        if(!isTxTattachment(mailRecord)) {
                            mailRecord.setBody(retrieveBodyTxtContent(path, mailRecord.getUuid() + ".txt"));
                            try {
                                brokerService.sendToKafka(mailRecord, topic);
                                LOGGER.info("Message successfully sent with id: " + mailRecord.getUuid());
                            } catch (ExecutionException | InterruptedException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                   return;
            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    /**
     * Read Tags, File and Location
     * @param reader
     * @return
     * @throws XMLStreamException
     * @throws ParseException
     */
    private MailRecord readDocument(XMLStreamReader reader) throws XMLStreamException, ParseException {
        MailRecord mail = new MailRecord();
        mail.setUuid(reader.getAttributeValue(null, "DocID"));

        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Tags")) {
                        readTag(reader, mail);
                    }else if ((elementName.equals("Files"))){
                        readFiles(reader,mail);
                    }else if(elementName.equals("Locations")){
                        readLocations(reader,mail);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;
            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    private MailRecord readLocations(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Location")) {
                        readLocation(reader, mail);//no return
                    }
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    private MailRecord readLocation(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Custodian")) {
                        readCustodian(reader, mail);//no return
                    }
                    if (elementName.equals("LocationURI")) {
                        readLocationURI(reader, mail);//no return
                    }
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    private MailRecord readCustodian(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    private MailRecord readLocationURI(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                        reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }

    private MailRecord readFiles(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("File")) {
                        readFile(reader, mail);
                    }
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }
    private MailRecord readFile(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("ExternalFile")) {
                        readExternalFile(reader, mail);//no return
                    }
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }
    private MailRecord readExternalFile(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }





     private MailRecord readTag(XMLStreamReader reader, MailRecord mail) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String attributeValue = reader.getAttributeValue(null, "TagName");
                    if (attributeValue.equals("#From"))
                        mail.setFrom(readCharacters(reader));
                    else if (attributeValue.equals("#To"))
                        mail.setTo(readCharactersList(reader,"[(,)]"));
                    else if (attributeValue.equals("#Subject"))
                        mail.setSubject(readCharacters(reader));
                    else if (attributeValue.equals("#DateSent"))
                        mail.setDateUtcEpoch(readDate(reader).getTime());
                    else if (attributeValue.equals("#CC"))
                        mail.setCc(readCharactersList(reader,"[(>)(,)]"));
                    else if (attributeValue.equals("#BCC"))
                        mail.setBcc(readCharactersList(reader,"[(>)(,)]"));
                    reader.next();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return mail;

            }
        }
        throw new XMLStreamException("Premature end of file");
    }





    private String readCharacters(XMLStreamReader reader) throws XMLStreamException {
        StringBuilder result = new StringBuilder();
        String dataType = reader.getAttributeValue(null, "TagDataType");
        if (dataType.equals("Text") || dataType.equals("DateTime")) {
            result.append(reader.getAttributeValue(null, "TagValue").trim());
        }
        return result.toString();
    }

    private List<String> readCharactersList(XMLStreamReader reader, String pattern) throws XMLStreamException {
        String characters = readCharacters(reader);
        Stream<String> stringStream = Arrays.stream(characters.split(pattern));
        List<String> list = new ArrayList<>();
        stringStream.forEach(i -> list.add(i.trim()));
        return list;
    }



    private Date readDate(XMLStreamReader reader) throws XMLStreamException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        String characters = readCharacters(reader);
        return  dateFormat.parse(characters);

    }

    private String retrieveBodyTxtContent(String zipFilaPath,  String fileName) throws IOException {
        String pattern = SOURCE_BODY_PATTERN + "(" + fileName + ")" ;
        InputStream txtInputStream = zipperService.extractFrom(zipFilaPath, pattern);
        return fileService.pullContentFromInputStream(txtInputStream);

    }

    private boolean isTxTattachment(MailRecord mailRecord){
        return (mailRecord.getFrom() == null || mailRecord.getFrom().isEmpty())&&
                (mailRecord.getTo() == null || mailRecord.getTo().isEmpty());
    }


}
