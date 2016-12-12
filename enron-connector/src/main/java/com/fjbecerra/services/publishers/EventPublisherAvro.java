package com.fjbecerra.services.publishers;

import com.fjbecerra.services.files.FileService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.fjbecerra.exceptions.EventPublisherException;

import com.fjbecerra.services.hz.ProcessZipTask;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;



@Service
public class EventPublisherAvro implements IEventPublisher {

    private static final Logger LOGGER = Logger.getLogger( EventPublisherAvro.class.getName());

    @Autowired
    private ProcessZipTask processZipTask;

    @Autowired
    private FileService fileService;

    @Autowired
    private  HazelcastInstance hazelcastInstance;

    @Value("${hazelcast.executor.name}")
    private String executorName;


    /**
     * Start generates event
     * @param file
     * @return
     * @throws EventPublisherException
     */
    @Override
    public boolean startProduceEvent(String file) throws EventPublisherException {
        List<String> zipFiles = fileService.listFilesInAFolder(file, "_xml.zip");
        if(zipFiles == null || zipFiles.size() == 0)
        {
                throw new EventPublisherException("Double check you setup the mount. ");
        }
        zipFiles.stream().forEach(zipFile -> {
            IExecutorService executor = hazelcastInstance.getExecutorService(executorName);
            processZipTask.setZipPath(zipFile);
            executor.execute(processZipTask);
        });

        return Boolean.TRUE;
    }


}
