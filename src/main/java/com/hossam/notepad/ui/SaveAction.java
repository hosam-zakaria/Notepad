package com.hossam.notepad.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveAction extends AbstractAction {
    private final MainFrame frame;
    public SaveAction(MainFrame frame) {
        super("Save");
        this.frame = frame;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.saveFile();
    }
}
