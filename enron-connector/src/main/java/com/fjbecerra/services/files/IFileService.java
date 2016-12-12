package com.fjbecerra.services.files;

import com.fjbecerra.exceptions.EventPublisherException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;


public interface IFileService  extends Serializable{

    List<String> listFilesInAFolder(String directory, String pattern) throws EventPublisherException;

    String pullContentFromInputStream(InputStream is) throws IOException;

}
