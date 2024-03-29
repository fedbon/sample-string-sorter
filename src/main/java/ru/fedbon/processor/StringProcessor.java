package ru.fedbon.processor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.fedbon.exception.AppException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class StringProcessor implements Processor {

    @Override
    public void process(BufferedReader reader, List<Set<String>> groupedLines,
                        List<Map<String, Integer>> lineParts) {
        try {
            log.info("Starting to process the input file...");
            String line = reader.readLine();
            while (line != null) {
                processLine(line, groupedLines, lineParts);
                line = reader.readLine();
            }
            log.info("Finished processing the input file.");
        } catch (IOException e) {
            throw new AppException("Error occurred while processing input file");
        }
    }

    private void processLine(String line, List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts) {
        String[] columns = getColumnValues(line);
        Integer groupNumber = findGroupNumber(lineParts, columns);

        if (groupNumber == null) {
            if (hasNonEmptyColumns(columns)) {
                addToNewGroup(line, groupedLines, lineParts);
            }
        } else {
            addToExistingGroup(line, groupedLines, lineParts, groupNumber);
        }
    }

    private Integer findGroupNumber(List<Map<String, Integer>> lineParts, String[] columns) {
        for (int i = 0; i < Math.min(lineParts.size(), columns.length); i++) {
            Integer lineGroupNumber = lineParts.get(i).get(columns[i]);
            if (lineGroupNumber != null) {
                return lineGroupNumber;
            }
        }
        return null;
    }

    private boolean hasNonEmptyColumns(String[] columns) {
        return Arrays.stream(columns).anyMatch(s -> !s.isEmpty());
    }

    private void addToNewGroup(String line, List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts) {
        groupedLines.add(new HashSet<>(List.of(line)));
        updateLineParts(getColumnValues(line), lineParts, groupedLines.size() - 1);
    }

    private void addToExistingGroup(String line, List<Set<String>> groupedLines,
                                    List<Map<String, Integer>> lineParts, int groupNumber) {
        groupedLines.get(groupNumber).add(line);
        updateLineParts(getColumnValues(line), lineParts, groupNumber);
    }

    private String[] getColumnValues(String line) {
        for (int i = 1; i < line.length() - 1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i - 1) != ';' && line.charAt(i + 1) != ';') {
                return new String[0];
            }
        }
        return line.replace("\"", "").split(";");
    }

    private void updateLineParts(String[] newValues, List<Map<String, Integer>> lineParts, int groupNumber) {
        for (int i = 0; i < newValues.length; i++) {
            if (!newValues[i].isEmpty()) {
                Map<String, Integer> linePartsMap;
                if (i < lineParts.size()) {
                    linePartsMap = lineParts.get(i);
                } else {
                    linePartsMap = new HashMap<>();
                    lineParts.add(linePartsMap);
                }
                linePartsMap.put(newValues[i], groupNumber);
            }
        }
    }
}
