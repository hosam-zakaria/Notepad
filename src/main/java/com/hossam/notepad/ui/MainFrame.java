package com.hossam.notepad.ui;

import com.hossam.notepad.service.DocumentSession;
import com.hossam.notepad.service.FileService;
import javax.swing.text.PlainDocument;
import com.hossam.notepad.buffer.GapBufferContent;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainFrame extends JFrame {

    private final JTextArea textArea = new JTextArea(new PlainDocument(new GapBufferContent()));
    private final DocumentSession session = new DocumentSession();
    private final FileService fileService = new FileService();
    private final UndoManager undoManager = new UndoManager();

    private void updateTitle() {
        String name = session.hasFile()
                ? session.getCurrentFile().getFileName().toString()
                : "Untitled";

        setTitle(name + " - MiniNotepad");
    }

    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            try {
                String content = fileService.read(path);
                textArea.setText(content);
                session.markSaved(path);
                updateTitle();
                undoManager.discardAllEdits();
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
    public void saveFileAs() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);

        if(result == JFileChooser.APPROVE_OPTION){
            Path path = chooser.getSelectedFile().toPath();
            try {
                fileService.write(path, textArea.getText());
                session.markSaved(path);
                updateTitle();
            } catch (IOException e){
                JOptionPane.showMessageDialog(
                        this,
                        "Error saving file: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }else{

        }

    }
    public void saveFile() {
        if (session.hasFile()) {
            Path path = session.getCurrentFile();
            try {
                fileService.write(path, textArea.getText());
                session.markSaved(path);
                updateTitle();

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error saving file: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            saveFileAs();
        }
    }
    public void newFile() {
        textArea.setText("");
        session.reset();
        updateTitle();
        undoManager.discardAllEdits();
    }
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    public MainFrame() {
        setTitle("Untitled - MiniNotepad");
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        fileMenu.add(new JMenuItem(new NewAction(this)));
        fileMenu.add(new JMenuItem(new OpenAction(this)));
        fileMenu.add(new JMenuItem(new SaveAction(this)));
        fileMenu.add(new JMenuItem(new SaveAsAction(this)));
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(new UndoAction(this)));
        editMenu.add(new JMenuItem(new RedoAction(this)));
        menuBar.add(editMenu);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        textArea.getDocument().addUndoableEditListener(undoManager);
        undoManager.setLimit(200);
    }
}
