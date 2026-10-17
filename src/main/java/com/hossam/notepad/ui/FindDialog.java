package com.hossam.notepad.ui;

import com.hossam.notepad.algo.KmpMatcher;
import com.hossam.notepad.service.SearchService;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class FindDialog extends JDialog {

    private final JTextComponent textComponent;
    private final UndoManager undoManager;
    private final SearchService searchService;

    private final JTextField findField = new JTextField(20);
    private final JTextField replaceField = new JTextField(20);

    public FindDialog(JFrame owner, JTextComponent textComponent, UndoManager undoManager) {
        super(owner, "Find / Replace", false);
        this.textComponent = textComponent;
        this.undoManager = undoManager;
        this.searchService = new SearchService(new KmpMatcher());

        buildUI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel fields = new JPanel(new GridLayout(2, 2, 5, 5));
        fields.add(new JLabel("Find:"));
        fields.add(findField);
        fields.add(new JLabel("Replace with:"));
        fields.add(replaceField);

        JButton findNextButton = new JButton("Find Next");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");

        findNextButton.addActionListener(e -> findNext());
        replaceButton.addActionListener(e -> replace());
        replaceAllButton.addActionListener(e -> replaceAll());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(findNextButton);
        buttons.add(replaceButton);
        buttons.add(replaceAllButton);

        setLayout(new BorderLayout(8, 8));
        add(fields, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void findNext() {
        String pattern = findField.getText();
        if (pattern.isEmpty()) return;

        String text = getText();
        int start = textComponent.getSelectionEnd();
        int index = searchService.findNext(text, pattern, start);

        if (index == -1 && start > 0) {          // wrap around
            index = searchService.findNext(text, pattern, 0);
        }
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "No match found.");
            return;
        }
        textComponent.requestFocusInWindow();
        textComponent.select(index, index + pattern.length());
    }

    private void replace() {
        String pattern = findField.getText();
        if (pattern.isEmpty()) return;

        String selected = textComponent.getSelectedText();
        if (selected != null && selected.equals(pattern)) {
            textComponent.replaceSelection(replaceField.getText());
        }
        findNext();
    }

    private void replaceAll() {
        String pattern = findField.getText();
        String replacement = replaceField.getText();
        if (pattern.isEmpty()) return;

        List<Integer> matches = searchService.findAll(getText(), pattern);
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matches found.");
            return;
        }

        AbstractDocument doc = (AbstractDocument) textComponent.getDocument();
        CompoundEdit compound = new CompoundEdit();
        UndoableEditListener collector = event -> compound.addEdit(event.getEdit());

        doc.removeUndoableEditListener(undoManager);
        doc.addUndoableEditListener(collector);
        try {
            // من الآخر للأول عشان الـ indexes الأولى ما تتغيرش
            for (int i = matches.size() - 1; i >= 0; i--) {
                doc.replace(matches.get(i), pattern.length(), replacement, null);
            }
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(this, "Replace error: " + e.getMessage(),
                    "Replace Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            doc.removeUndoableEditListener(collector);
            doc.addUndoableEditListener(undoManager);
            compound.end();
            undoManager.addEdit(compound);
        }

        textComponent.requestFocusInWindow();
        JOptionPane.showMessageDialog(this, matches.size() + " replacement(s) completed.");
    }

    private String getText() {
        try {
            Document d = textComponent.getDocument();
            return d.getText(0, d.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}