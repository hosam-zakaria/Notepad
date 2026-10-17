
package com.hossam.notepad.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RedoAction extends AbstractAction {

    private final MainFrame frame;

    public RedoAction(MainFrame frame) {
        super("Redo");
        this.frame = frame;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control Y"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.redo();
    }
}
