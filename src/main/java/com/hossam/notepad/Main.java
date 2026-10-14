package com.hossam.notepad;
import com.hossam.notepad.service.FileService;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        FileService fs = new FileService();
        Path path = Path.of("test.txt");
        fs.write(path, "Hello مرحبا");
        String content = fs.read(path);
        System.out.println(content);
    }
}
