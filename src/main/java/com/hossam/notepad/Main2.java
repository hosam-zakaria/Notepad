
package com.hossam.notepad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main2 {

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();

        for(int i = 1; i <= 200_000 ;i++){
            sb.append("Line ").append(i).append(" hello مرحبا the quick brown fox\n");
        }

        Files.writeString(Path.of("big.txt"),sb.toString(),StandardCharsets.UTF_8);
        System.out.println("big.txt created successfully!");
    }
}
