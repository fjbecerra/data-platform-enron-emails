package com.fjbecerra.services.streams;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



@Service
public class ZipperService implements IZipperService {

    /**
     * Extract from a zip file given a pattern
     * @param file
     * @param pattern
     * @return
     * @throws IOException
     */
    @Override
    public InputStream extractFrom(String file, String pattern) throws IOException {
        InputStream inputStream = null;
        try(ZipFile zipFile = new ZipFile(file)){
            Predicate<ZipEntry> match = ze -> ze.getName().matches(pattern);
            Predicate<ZipEntry> contain = ze -> ze.getName().contains(pattern);
            Optional<? extends ZipEntry> optional = zipFile.stream().filter(match.or(contain)).findFirst();
            if(optional.isPresent()){
                ZipFile zipFi = new ZipFile(file);
                inputStream = zipFi.getInputStream(optional.get());
            }
            return inputStream;

        }
    }


}
