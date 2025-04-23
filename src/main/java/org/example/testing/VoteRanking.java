package org.example.testing;

import java.util.*;

public class VoteRanking {

    public static List<String> rankCandidates(List<List<String>> votes) {
        Map<String, Integer> scoreMap = new HashMap<>();

        for (List<String> vote : votes) {
            int weight = vote.size();
            for (int i = 0; i < vote.size(); i++) {
                String candidate = vote.get(i);
                scoreMap.put(candidate, scoreMap.getOrDefault(candidate, 0) + (weight - i));
            }
        }

        // Sort by score descending, then lexicographically (optional tie-breaker)
        List<String> result = new ArrayList<>(scoreMap.keySet());
        result.sort((a, b) -> {
            int cmp = scoreMap.get(b) - scoreMap.get(a);
            return (cmp != 0) ? cmp : a.compareTo(b); // tie-breaker by name
        });

        return result;
    }

    public static void main(String[] args) {
        List<List<String>> votes = List.of(
                List.of("A", "B", "C"),
                List.of("A", "C", "D"),
                List.of("D", "A", "C")
        );

        List<String> ranked = rankCandidates(votes);
        System.out.println("Ranked Candidates: " + ranked);
    }
}
