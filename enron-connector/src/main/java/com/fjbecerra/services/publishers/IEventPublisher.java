package com.fjbecerra.services.publishers;


import com.fjbecerra.exceptions.EventPublisherException;

import java.io.Serializable;
import java.net.URISyntaxException;


/**
 * Created by FBecer01 on 27/11/2016.
 */
public interface IEventPublisher  extends Serializable {

     boolean startProduceEvent(String file) throws URISyntaxException, EventPublisherException;
}
