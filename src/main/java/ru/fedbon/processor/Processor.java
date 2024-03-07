package ru.fedbon.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Processor {

    void process(BufferedReader reader, List<Set<String>> groupedLines, List<Map<String, Integer>> lineParts);
}
