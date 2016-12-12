package com.fjbecerra.services.streams;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


public interface IZipperService extends Serializable{

    InputStream extractFrom(String file, String pattern) throws IOException;

}
