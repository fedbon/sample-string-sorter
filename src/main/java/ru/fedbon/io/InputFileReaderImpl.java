package ru.fedbon.io;

import org.springframework.stereotype.Component;
import ru.fedbon.exception.AppException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


@Component
public class InputFileReaderImpl implements InputFileReader {

    @Override
    public BufferedReader readFile(String filePath) {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new AppException("File not found: " + filePath);
            }
            return new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            throw new AppException("Error reading file: " + filePath);
        }
    }
}
