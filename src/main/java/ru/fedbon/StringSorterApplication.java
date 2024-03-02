package ru.fedbon;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;



public class StringSorterApplication {

    private static final Logger LOGGER = Logger.getLogger(StringSorterApplication.class.getName());

    public static void main(String[] args) {
        if (args.length != 2) {
            LOGGER.info("Usage: java -jar sample-string-sorter-1.0.jar <input-file> <output-file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        long startTime = System.currentTimeMillis();

        List<Set<String>> groupedLines = new ArrayList<>();
        List<Map<String, Integer>> lineParts = new ArrayList<>();

        processInput(inputFile, groupedLines, lineParts);
        writeResultToFile(outputFile, groupedLines);

        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime) / 1000;
        LOGGER.info(String.format("Elapsed time: %d seconds", elapsedTime));
    }

    private static void processInput(String inputFile, List<Set<String>> groupedLines,
                                     List<Map<String, Integer>> lineParts) {
        try (InputStream inputStream = StringSorterApplication.class.getClassLoader().getResourceAsStream(inputFile)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = reader.readLine();
                while (line != null) {
                    processLine(line, groupedLines, lineParts);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error processing input: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void processLine(String line, List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts) {
        String[] columns = getColumnValues(line);
        Integer groupNumber = null;
        for (int i = 0; i < Math.min(lineParts.size(), columns.length); i++) {
            Integer lineGroupNumber = lineParts.get(i).get(columns[i]);
            if (lineGroupNumber != null) {
                if (groupNumber == null) {
                    groupNumber = lineGroupNumber;
                } else if (!Objects.equals(groupNumber, lineGroupNumber)) {
                    mergeGroups(groupedLines, lineParts, groupNumber, lineGroupNumber);
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

    private static void mergeGroups(List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts,
                                    int groupNumber1, int groupNumber2) {
        for (String line : groupedLines.get(groupNumber2)) {
            groupedLines.get(groupNumber1).add(line);
            updateLineParts(getColumnValues(line), groupNumber1, lineParts);
        }
        groupedLines.set(groupNumber2, new HashSet<>());
    }

    private static String[] getColumnValues(String line) {
        for (int i = 1; i < line.length() - 1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i - 1) != ';' && line.charAt(i + 1) != ';') {
                return new String[0];
            }
        }
        return line.replace("\"", "").split(";");
    }

    private static void updateLineParts(String[] newValues, int groupNumber, List<Map<String, Integer>> lineParts) {
        for (int i = 0; i < newValues.length; i++) {
            if (newValues[i].isEmpty()) {
                continue;
            }
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

    private static List<Set<String>> sortGroupsBySize(List<Set<String>> groupedLines) {
        return groupedLines.stream()
                .filter(group -> group.size() > 1)
                .sorted(Comparator.comparingInt((Set<String> group) -> group.size()).reversed())
                .toList();
    }

    private static void writeGroupsToFile(PrintWriter writer, List<Set<String>> sortedGroups) {
        int matchedGroupsCount = sortedGroups.size();
        writer.println("Total groups count: " + matchedGroupsCount);

        AtomicInteger groupNumber = new AtomicInteger(0);
        for (Set<String> group : sortedGroups) {
            if (group.size() > 1) {
                int currentGroup = groupNumber.incrementAndGet();
                writer.println("\nGroup " + currentGroup);
                group.forEach(writer::println);
            }
        }
    }

    private static void writeResultToFile(String outputFile, List<Set<String>> groupedLines) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile))))) {
            List<Set<String>> sortedGroups = sortGroupsBySize(groupedLines);
            writeGroupsToFile(writer, sortedGroups);
        } catch (IOException e) {
            LOGGER.severe("Error writing result to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}