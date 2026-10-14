package com.hossam.notepad.ui;

import com.hossam.notepad.service.DocumentSession;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final DocumentSession session = new DocumentSession();

    public MainFrame() {
        setTitle("Untitled - MiniNotepad");
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
}
