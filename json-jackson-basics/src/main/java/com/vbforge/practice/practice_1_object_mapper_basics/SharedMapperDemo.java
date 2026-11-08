package com.vbforge.practice.practice_1_object_mapper_basics;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** One configured ObjectMapper is thread-safe: build it once, share it (here: 100 jobs on 4 threads). */
public class SharedMapperDemo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        List<Future<String>> results = new ArrayList<>();

        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 100; i++) {
                final int n = i;
                results.add(pool.submit(() -> MAPPER.writeValueAsString(Map.of("n", n))));
            }
        }

        boolean allCorrect = true;
        for (int i = 0; i < results.size(); i++) {
            allCorrect &= results.get(i).get().equals("{\"n\":" + i + "}");
        }
        System.out.println("100 results from one shared mapper, all correct: " + allCorrect);
    }
}
