
package com.hossam.notepad.buffer;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.undo.UndoableEdit;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GapBufferContent implements AbstractDocument.Content {

    private final GapBuffer buffer = new GapBuffer();
    private final List<WeakReference<TrackedPosition>> positions = new ArrayList<>();

    public GapBufferContent() {
        buffer.insert(0, "\n");
    }

    @Override
    public int length() {
        return buffer.length();
    }

    @Override
    public UndoableEdit insertString(int where, String str)
            throws BadLocationException {

        if (str == null) {
            throw new NullPointerException("str");
        }

        if (where < 0 || where > length() - 1) {
            throw new BadLocationException(
                    "Invalid insert position", where);
        }

        if (str.isEmpty()) {
            return new ContentEdit(this, where, str, true);
        }

        buffer.insert(where, str);
        updatePositionsForInsert(where, str.length());

        return new ContentEdit(this, where, str, true);
    }

    @Override
    public UndoableEdit remove(int where, int nitems)
            throws BadLocationException {

        if (where < 0 || nitems < 0 ||
                where > length() - 1 ||
                nitems > length() - 1 - where) {

            throw new BadLocationException(
                    "Invalid remove range", where);
        }

        String removed = getString(where, nitems);

        if (nitems > 0) {
            buffer.delete(where, nitems);
            updatePositionsForRemove(where, nitems);
        }

        return new ContentEdit(this, where, removed, false);
    }

    @Override
    public String getString(int where, int len)
            throws BadLocationException {

        if (where < 0 || len < 0 ||
                where > length() ||
                len > length() - where) {

            throw new BadLocationException(
                    "Invalid text range", where);
        }

        return buffer.subSequence(where, where + len).toString();
    }

    @Override
    public void getChars(int where, int len, Segment txt)
            throws BadLocationException {

        if (where < 0 || len < 0 ||
                where > length() ||
                len > length() - where) {

            throw new BadLocationException(
                    "Invalid text range", where);
        }

        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = buffer.charAt(where + i);
        }
        txt.array = chars;
        txt.offset = 0;
        txt.count = len;
    }

    @Override
    public Position createPosition(int offset)
            throws BadLocationException {

        if (offset < 0 || offset > length()) {
            throw new BadLocationException(
                    "Invalid position", offset);
        }

        TrackedPosition position = new TrackedPosition(offset);
        positions.add(new WeakReference<>(position));

        return position;
    }

    void rawInsert(int where, String str) {
        if (str.isEmpty()) {
            return;
        }

        buffer.insert(where, str);
        updatePositionsForInsert(where, str.length());
    }

    void rawRemove(int where, int count) {
        if (count == 0) {
            return;
        }

        buffer.delete(where, count);
        updatePositionsForRemove(where, count);
    }

    List<UndoPosRef> getPositionsInRange(int offset, int length) {
        int end = offset + length;
        List<UndoPosRef> refs = new ArrayList<>();
        for (int i = positions.size() - 1; i >= 0; i--) {
            TrackedPosition pos = positions.get(i).get();
            if (pos == null) {
                positions.remove(i);
            } else if (pos.offset >= offset && pos.offset <= end) {
                refs.add(new UndoPosRef(pos));
            }
        }
        return refs;
    }

    void updateUndoPositions(List<UndoPosRef> refs) {
        if (refs != null) {
            for (UndoPosRef ref : refs) {
                ref.reset();
            }
        }
    }

    private void updatePositionsForInsert(int where, int count) {
        int effectiveWhere = (where == 0) ? 1 : where;
        for (int i = positions.size() - 1; i >= 0; i--) {
            TrackedPosition position = positions.get(i).get();
            if (position == null) {
                positions.remove(i);
            } else if (position.offset >= effectiveWhere) {
                position.offset += count;
            }
        }
    }

    private void updatePositionsForRemove(int where, int count) {
        int end = where + count;

        for (int i = positions.size() - 1; i >= 0; i--) {
            TrackedPosition position = positions.get(i).get();
            if (position == null) {
                positions.remove(i);
            } else if (position.offset >= end) {
                position.offset -= count;
            } else if (position.offset >= where) {
                position.offset = where;
            }
        }
    }

    static class UndoPosRef {
        final TrackedPosition pos;
        final int undoLocation;

        UndoPosRef(TrackedPosition pos) {
            this.pos = pos;
            this.undoLocation = pos.offset;
        }

        void reset() {
            pos.offset = undoLocation;
        }
    }

    static class TrackedPosition implements Position {
        private int offset;

        private TrackedPosition(int offset) {
            this.offset = offset;
        }

        @Override
        public int getOffset() {
            return offset;
        }
    }
}
