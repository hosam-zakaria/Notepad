package com.hossam.notepad.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveAsAction extends AbstractAction {
    private final MainFrame frame;
    public SaveAsAction(MainFrame frame) {
        super("Save As...");
        this.frame = frame;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.saveFileAs();
    }
}
