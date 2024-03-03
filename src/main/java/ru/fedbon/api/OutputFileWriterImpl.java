package ru.fedbon.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.fedbon.exception.AppException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;



@Slf4j
@Component
@RequiredArgsConstructor
public class OutputFileWriterImpl implements OutputFileWriter {

    @Override
    public void writeResultToFile(String outputFilePath, List<Set<String>> groupedLines) {
        List<Set<String>> sortedGroups = sortGroupsBySize(groupedLines);
        writeGroupsToFile(outputFilePath, sortedGroups);
    }

    @Override
    public void writeGroupsToFile(String outputFilePath, List<Set<String>> sortedGroups) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath)))) {
            int matchedGroupsCount = sortedGroups.size();
            writer.println("Total groups count: " + matchedGroupsCount);

            var groupNumber = new AtomicInteger(0);
            for (Set<String> group : sortedGroups) {
                if (group.size() > 1) {
                    int currentGroup = groupNumber.incrementAndGet();
                    writer.println("\nGroup " + currentGroup);
                    group.forEach(writer::println);
                }
            }
        } catch (IOException e) {
            throw new AppException("Error writing result to file: " + outputFilePath);
        }
    }

    private List<Set<String>> sortGroupsBySize(List<Set<String>> groupedLines) {
        return groupedLines.stream()
                .filter(group -> group.size() > 1)
                .sorted(Comparator.comparingInt((Set<String> group) -> group.size()).reversed())
                .toList();
    }
}
