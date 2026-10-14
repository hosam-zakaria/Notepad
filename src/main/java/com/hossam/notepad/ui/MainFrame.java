package com.hossam.notepad.ui;

import com.hossam.notepad.service.DocumentSession;
import com.hossam.notepad.service.FileService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainFrame extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final DocumentSession session = new DocumentSession();
    private final FileService fileService = new FileService();

    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            try {
                String content = fileService.read(path);
                textArea.setText(content);
                session.markSaved(path);
                setTitle(path.getFileName() + " - MiniNotepad");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error opening file: " + e.getMessage(),
                        "Open Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public MainFrame() {
        setTitle("Untitled - MiniNotepad");
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem(new OpenAction(this));
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
}
