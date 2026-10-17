package com.hossam.notepad.buffer;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.List;

public class ContentEdit extends AbstractUndoableEdit {

    private final GapBufferContent content;
    private final int where;
    private final String text;
    private final boolean insert;
    private List<GapBufferContent.UndoPosRef> posRefs;

    public ContentEdit(
            GapBufferContent content,
            int where,
            String text,
            boolean insert) {

        this.content = content;
        this.where = where;
        this.text = text;
        this.insert = insert;
        if (!insert && content != null && text != null) {
            this.posRefs = content.getPositionsInRange(where, text.length());
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        if (insert) {
            posRefs = content.getPositionsInRange(where, text.length());
            content.rawRemove(where, text.length());
        } else {
            content.rawInsert(where, text);
            content.updateUndoPositions(posRefs);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        if (insert) {
            content.rawInsert(where, text);
            content.updateUndoPositions(posRefs);
            posRefs = null;
        } else {
            posRefs = content.getPositionsInRange(where, text.length());
            content.rawRemove(where, text.length());
        }
    }
}