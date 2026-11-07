package com.vbforge.practice.practice_10_download_to_file;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * BodyHandlers.ofFile(path) opens the file with CREATE + WRITE only: a shorter body written over a
 * longer existing file leaves stale bytes at the end. Pass TRUNCATE_EXISTING to overwrite properly.
 */
public class OfFileOpenOptionsDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Path dir = Files.createTempDirectory("http-download");
        Path target = dir.resolve("download.txt");

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build(); // body "hello", 5 bytes

            Files.writeString(target, "XXXXXXXXXXXXXXXXXXXX"); // 20 old bytes
            HttpResponse<Path> plain = client.send(request, BodyHandlers.ofFile(target));
            System.out.println("default options   -> \"" + Files.readString(plain.body()) + "\"");

            Files.writeString(target, "XXXXXXXXXXXXXXXXXXXX");
            HttpResponse<Path> truncating = client.send(request,
                    BodyHandlers.ofFile(target, CREATE, TRUNCATE_EXISTING, WRITE));
            System.out.println("with TRUNCATE     -> \"" + Files.readString(truncating.body()) + "\"");
        } finally {
            Files.deleteIfExists(target);
            Files.deleteIfExists(dir);
        }
    }
}
