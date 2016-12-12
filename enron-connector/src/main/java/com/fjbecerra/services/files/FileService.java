package com.fjbecerra.services.files;


import com.fjbecerra.exceptions.EventPublisherException;
import com.fjbecerra.services.streams.ZipperService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FileService implements IFileService {

    private static final Logger LOGGER = Logger.getLogger( FileService.class.getName() );

    @Autowired
    private ZipperService zipperService;

    /**
     * List files given a directory and pattern
     * @param directory
     * @param pattern
     * @return
     */
    @Override
    public List<String> listFilesInAFolder(String directory, final String pattern) throws EventPublisherException {
        List<String> fileNames = new ArrayList<>();
        try (
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory),
                        path -> {
                            return path.getFileName().toString().contains(pattern);
                        })
            )
            {
                directoryStream.forEach(path -> fileNames.add(path.toString()));

            } catch (IOException ex) {
                throw new EventPublisherException("Be sure you setup the mount targeting the right folder");
            }
        return fileNames;
    }


    /**
     * Extract content of an input stream by line
     * @param is
     * @return
     * @throws IOException
     */
    public String pullContentFromInputStream(InputStream is) throws IOException {
        if(is == null) return "";
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

}
