package ru.fedbon;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.fedbon.io.InputFileReaderImpl;
import ru.fedbon.io.OutputFileWriterImpl;
import ru.fedbon.processor.StringProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@SpringBootApplication
public class StringSorterApplication {

    public static void main(String[] args) {
        if (args.length != 2) {
            log.info("Use command in target folder: " +
                    "java -jar sample-string-sorter-1.0.jar input-data.txt <output-file-name>");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        List<Set<String>> groupedLines = new ArrayList<>();
        List<Map<String, Integer>> lineParts = new ArrayList<>();

        var fileReader = new InputFileReaderImpl();
        var processor = new StringProcessor();
        var fileWriter = new OutputFileWriterImpl();

        long startTime = System.currentTimeMillis();

        processor.process(fileReader.readFile(inputFilePath), groupedLines, lineParts);
        fileWriter.writeResultToFile(outputFilePath, groupedLines);

        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime) / 1000;
        log.info(String.format("Elapsed time: %d seconds", elapsedTime));
    }
}