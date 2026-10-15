package com.hossam.notepad.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewAction extends AbstractAction {
    private final MainFrame frame;
    public NewAction(MainFrame frame) {
        super("New");
        this.frame = frame;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.newFile();
    }
}
