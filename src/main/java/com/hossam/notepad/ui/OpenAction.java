package com.hossam.notepad.ui;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
public class OpenAction extends AbstractAction {
    private final MainFrame frame;

    public OpenAction(MainFrame frame) {
        super("Open...");
        this.frame = frame;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.openFile();
    }
}