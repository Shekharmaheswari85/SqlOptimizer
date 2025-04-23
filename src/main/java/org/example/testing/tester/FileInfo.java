package org.example.testing.tester;

import java.util.List;

class FileInfo {
    String name;
    int size;
    List<String> collectionIds;

    public FileInfo(String name, int size, List<String> collectionIds) {
        this.name = name;
        this.size = size;
        this.collectionIds = collectionIds;
    }
}
