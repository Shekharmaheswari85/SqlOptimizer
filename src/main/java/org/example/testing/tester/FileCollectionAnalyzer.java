package org.example.testing.tester;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FileCollectionAnalyzer {

    public static Map<String, Object> generateReport(List<FileInfo> files, int topN) throws InterruptedException {
        AtomicLong totalSize = new AtomicLong(0);
        ConcurrentHashMap<String, AtomicLong> collectionSizeMap = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (FileInfo file : files) {
            executor.submit(() -> {
                totalSize.addAndGet(file.size);
                for (String collectionId : file.collectionIds) {
                    collectionSizeMap
                            .computeIfAbsent(collectionId, k -> new AtomicLong(0))
                            .addAndGet(file.size);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Find top N collections by size
        PriorityQueue<Map.Entry<String, AtomicLong>> topNHeap = new PriorityQueue<>(
                (a, b) -> {
                    int cmp = Long.compare(a.getValue().get(), b.getValue().get());
                    return (cmp != 0) ? cmp : b.getKey().compareTo(a.getKey()); // reverse lex for minHeap
                }
        );

        for (Map.Entry<String, AtomicLong> entry : collectionSizeMap.entrySet()) {
            topNHeap.offer(entry);
            if (topNHeap.size() > topN) topNHeap.poll();
        }

        List<Map.Entry<String, Long>> topCollections = new ArrayList<>();
        while (!topNHeap.isEmpty()) {
            Map.Entry<String, AtomicLong> entry = topNHeap.poll();
            topCollections.add(Map.entry(entry.getKey(), entry.getValue().get()));
        }

        Collections.reverse(topCollections); // highest first

        Map<String, Object> report = new HashMap<>();
        report.put("totalSize", totalSize.get());
        report.put("topCollections", topCollections);

        return report;
    }

    public static void main(String[] args) throws InterruptedException {
        List<FileInfo> files = List.of(
                new FileInfo("file1.txt", 100, List.of("c1")),
                new FileInfo("file2.txt", 200, List.of("c1")),
                new FileInfo("file3.txt", 200, List.of("c1")),
                new FileInfo("file4.txt", 300, List.of("c2")),
                new FileInfo("file5.txt", 100, List.of()),
                new FileInfo("file6.txt", 150, List.of("c2", "c3")),
                new FileInfo("file7.txt", 250, List.of("c3"))
        );

        Map<String, Object> report = generateReport(files, 2);
        System.out.println("Total size of files: " + report.get("totalSize"));

        System.out.println("Top Collections:");
        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Long>> top = (List<Map.Entry<String, Long>>) report.get("topCollections");
        for (Map.Entry<String, Long> entry : top) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
