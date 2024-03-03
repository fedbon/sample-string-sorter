package ru.fedbon.processor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Slf4j
@Component
public class StringProcessor implements Processor {


    @Override
    public void process(BufferedReader reader, List<Set<String>> groupedLines,
                        List<Map<String, Integer>> lineParts) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            processLine(line, groupedLines, lineParts);
            line = reader.readLine();
        }
    }

    private void processLine(String line, List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts) {
        String[] columns = getColumnValues(line);
        Integer groupNumber = null;
        for (int i = 0; i < Math.min(lineParts.size(), columns.length); i++) {
            Integer lineGroupNumber = lineParts.get(i).get(columns[i]);
            if (lineGroupNumber != null) {
                if (groupNumber == null) {
                    groupNumber = lineGroupNumber;
                } else if (!Objects.equals(groupNumber, lineGroupNumber)) {
                    mergeGroups(groupedLines,lineParts, groupNumber, lineGroupNumber);
                }
            }
        }
        if (groupNumber == null) {
            if (Arrays.stream(columns).anyMatch(s -> !s.isEmpty())) {
                groupedLines.add(new HashSet<>(List.of(line)));
                updateLineParts(columns, groupedLines.size() - 1, lineParts);
            }
        } else {
            groupedLines.get(groupNumber).add(line);
            updateLineParts(columns, groupNumber, lineParts);
        }
    }

    private void mergeGroups(List<Set<String>> groupedLines,
                             List<Map<String, Integer>> lineParts, int groupNumber1, int groupNumber2) {
        for (String line : new HashSet<>(groupedLines.get(groupNumber2))) {
            groupedLines.get(groupNumber1).add(line);
            updateLineParts(getColumnValues(line), groupNumber1, lineParts);
        }
        groupedLines.set(groupNumber2, new HashSet<>());
    }

    private String[] getColumnValues(String line) {
        for (int i = 1; i < line.length() - 1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i - 1) != ';' && line.charAt(i + 1) != ';') {
                return new String[0];
            }
        }
        return line.replace("\"", "").split(";");
    }

    private void updateLineParts(String[] newValues, int groupNumber, List<Map<String, Integer>> lineParts) {
        for (int i = 0; i < newValues.length; i++) {
            if (!newValues[i].isEmpty()) {
                Map<String, Integer> partMap;
                if (i < lineParts.size()) {
                    partMap = lineParts.get(i);
                } else {
                    partMap = new HashMap<>();
                    lineParts.add(partMap);
                }
                partMap.put(newValues[i], groupNumber);
            }
        }
    }
}
