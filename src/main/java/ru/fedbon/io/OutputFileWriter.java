package ru.fedbon.io;

import java.util.List;
import java.util.Set;

public interface OutputFileWriter {

    void writeGroupsToFile(String outputFile, List<Set<String>> sortedGroups);

    void writeResultToFile(String outputFile, List<Set<String>> groupedLines);
}
