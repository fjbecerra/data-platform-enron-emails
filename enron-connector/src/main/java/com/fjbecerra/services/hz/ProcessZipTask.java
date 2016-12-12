package com.fjbecerra.services.hz;



import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.services.files.FileService;
import com.fjbecerra.services.streams.IXmlStreamService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;


@Service
public class ProcessZipTask implements Runnable, Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProcessZipTask.class.getName() );

    @Autowired
    private IXmlStreamService transformerService;

    @Autowired
    private FileService fileService;


    private String zipPath;


    @Override
    public void run(){
        try {
            if(zipPath == null || zipPath.isEmpty())
            {
                throw new EventPublisherException("Zip path is empty");
            }
            LOGGER.info("Processing zip file: " + this.zipPath);
            transformerService.transformAndProduceEvent(this.zipPath);
        } catch (EventPublisherException | ParseException | XMLStreamException | IOException e) {
            LOGGER.error("Error processing zip with path: " + this.zipPath);
            LOGGER.error(e.getMessage());
        }
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }
}
