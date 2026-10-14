package com.hossam.notepad;
import com.hossam.notepad.service.FileService;
import com.hossam.notepad.ui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
