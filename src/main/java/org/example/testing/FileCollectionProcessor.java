package org.example.testing;

import java.util.*;
import java.util.stream.Collectors;

public class FileCollectionProcessor {

    // Map of file name to size
    private final Map<String, Integer> fileSizeMap = new HashMap<>();

    // Map of collection name to set of files
    private final Map<String, Set<String>> collectionToFileMap = new HashMap<>();

    // Add a file with optional collections
    public void addFile(String fileName, int size, List<String> collections) {
        fileSizeMap.put(fileName, size);
        if (collections != null) {
            for (String collection : collections) {
                collectionToFileMap
                        .computeIfAbsent(collection, k -> new HashSet<>())
                        .add(fileName);
            }
        }
    }

    // Compute total size of files processed
    public int getTotalSizeProcessed() {
        return fileSizeMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Get Top K collections based on cumulative size
    public List<Map.Entry<String, Integer>> getTopKCollectionsBySize(int k) {
        Map<String, Integer> collectionSizeMap = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : collectionToFileMap.entrySet()) {
            String collection = entry.getKey();
            int totalSize = 0;
            for (String file : entry.getValue()) {
                totalSize += fileSizeMap.getOrDefault(file, 0);
            }
            collectionSizeMap.put(collection, totalSize);
        }

        return collectionSizeMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(k)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        FileCollectionProcessor processor = new FileCollectionProcessor();

        // Sample Input
        processor.addFile("file1.txt", 100, null);
        processor.addFile("file2.txt", 200, List.of("collection1"));
        processor.addFile("file3.txt", 200, List.of("collection1"));
        processor.addFile("file4.txt", 300, List.of("collection2"));
        processor.addFile("file5.txt", 100, null);

        // Output Total Size
        int totalSize = processor.getTotalSizeProcessed();
        System.out.println("Total size of files processed: " + totalSize);

        // Output Top K Collections
        int K = 2;
        System.out.println("Top " + K + " collections by size:");
        List<Map.Entry<String, Integer>> topCollections = processor.getTopKCollectionsBySize(K);
        for (Map.Entry<String, Integer> entry : topCollections) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
