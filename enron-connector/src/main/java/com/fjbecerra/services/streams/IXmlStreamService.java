package com.fjbecerra.services.streams;


import com.fjbecerra.exceptions.EventPublisherException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;


public interface IXmlStreamService extends Serializable {

    void transformAndProduceEvent(String path) throws EventPublisherException, XMLStreamException, IOException, ParseException;
}
