package com.hossam.notepad.buffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.undo.UndoManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class GapBufferContentRegressionTest {

    @Test
    @DisplayName("1. Initial empty document has required newline and valid length")
    void testInitialEmptyDocument() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        assertEquals(1, content.length(), "Content must start with initial newline");
        assertEquals("\n", content.getString(0, 1), "Content must start with '\\n'");

        PlainDocument doc = new PlainDocument(content);
        assertEquals(0, doc.getLength(), "PlainDocument.getLength() must be 0 for empty doc");
        assertEquals("", doc.getText(0, doc.getLength()));

        Element root = doc.getDefaultRootElement();
        assertEquals(1, root.getElementCount(), "Root element must have 1 initial line");
        Element line0 = root.getElement(0);
        assertEquals(0, line0.getStartOffset());
        assertEquals(1, line0.getEndOffset());
    }

    @Test
    @DisplayName("2. Insertion at beginning, middle, and end")
    void testInsertions() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);

        // Insert at beginning
        doc.insertString(0, "World", null);
        assertEquals("World", doc.getText(0, doc.getLength()));

        // Insert at beginning again
        doc.insertString(0, "Hello ", null);
        assertEquals("Hello World", doc.getText(0, doc.getLength()));

        // Insert in middle
        doc.insertString(5, ",", null);
        assertEquals("Hello, World", doc.getText(0, doc.getLength()));

        // Insert at end
        doc.insertString(doc.getLength(), "!", null);
        assertEquals("Hello, World!", doc.getText(0, doc.getLength()));

        // Verify backing content still preserves trailing newline
        assertEquals("Hello, World!\n", content.getString(0, content.length()));
    }

    @Test
    @DisplayName("3. Deletion and invalid ranges")
    void testDeletionAndInvalidRanges() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);

        doc.insertString(0, "ABCDEF", null);
        doc.remove(2, 2); // remove "CD"
        assertEquals("ABEF", doc.getText(0, doc.getLength()));

        // Deleting beyond document length
        assertThrows(BadLocationException.class, () -> doc.remove(10, 1));
        assertThrows(BadLocationException.class, () -> doc.remove(-1, 1));

        // Content direct invalid delete ranges
        assertThrows(BadLocationException.class, () -> content.remove(-1, 1));
        assertThrows(BadLocationException.class, () -> content.remove(0, content.length())); // attempting to delete final newline
        assertThrows(BadLocationException.class, () -> content.remove(content.length() - 1, 1)); // deleting final newline
    }

    @Test
    @DisplayName("4. Zero-length reads and edits")
    void testZeroLengthReadsAndEdits() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);
        doc.insertString(0, "Test", null);

        // Zero length insert
        content.insertString(0, "");
        assertEquals("Test", doc.getText(0, doc.getLength()));

        // Zero length remove
        content.remove(1, 0);
        assertEquals("Test", doc.getText(0, doc.getLength()));

        // Zero length read
        assertEquals("", content.getString(2, 0));

        Segment seg = new Segment();
        content.getChars(2, 0, seg);
        assertEquals(0, seg.count);
    }

    @Test
    @DisplayName("5. Position behavior after insertion and deletion (regression for line 0 shift)")
    void testPositionTracking() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);

        Position p0 = doc.createPosition(0);
        assertEquals(0, p0.getOffset());

        // Insert at offset 0: p0 MUST remain 0!
        doc.insertString(0, "First", null);
        assertEquals(0, p0.getOffset(), "Position at offset 0 must not shift when inserting at 0");

        Position pMid = doc.createPosition(3);
        assertEquals(3, pMid.getOffset());

        Position pEnd = doc.createPosition(doc.getLength());
        assertEquals(5, pEnd.getOffset());

        // Insert before pMid
        doc.insertString(1, "XX", null); // text: "FXXirst"
        assertEquals(0, p0.getOffset());
        assertEquals(5, pMid.getOffset(), "pMid should have shifted from 3 to 5");
        assertEquals(7, pEnd.getOffset(), "pEnd should have shifted from 5 to 7");

        // Delete text across pMid
        doc.remove(4, 2); // remove chars at 4, 5
        assertEquals(0, p0.getOffset());
        assertEquals(4, pMid.getOffset(), "pMid was inside deleted range, collapses to delete start");
        assertEquals(5, pEnd.getOffset());
    }

    @Test
    @DisplayName("6. Multiple lines and newline handling without element inversion")
    void testMultipleLines() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);

        doc.insertString(0, "Line 1\nLine 2\nLine 3", null);

        Element root = doc.getDefaultRootElement();
        assertEquals(3, root.getElementCount(), "Should have 3 lines");

        for (int i = 0; i < root.getElementCount(); i++) {
            Element el = root.getElement(i);
            assertTrue(el.getStartOffset() <= el.getEndOffset(),
                    "Line " + i + " must have start <= end: [" + el.getStartOffset() + ", " + el.getEndOffset() + ")");
        }

        assertEquals(0, root.getElement(0).getStartOffset());
        assertEquals(7, root.getElement(0).getEndOffset()); // "Line 1\n"
        assertEquals(7, root.getElement(1).getStartOffset());
        assertEquals(14, root.getElement(1).getEndOffset()); // "Line 2\n"
        assertEquals(14, root.getElement(2).getStartOffset());
        assertEquals(21, root.getElement(2).getEndOffset()); // "Line 3\n"
    }

    @Test
    @DisplayName("7. Undo followed by redo preserves document elements and positions")
    void testUndoRedo() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);
        UndoManager undoManager = new UndoManager();
        doc.addUndoableEditListener(undoManager);

        doc.insertString(0, "First Line\nSecond Line", null);
        Position posSecond = doc.createPosition(12);
        assertEquals(12, posSecond.getOffset());

        // Undo insert
        assertTrue(undoManager.canUndo());
        undoManager.undo();
        assertEquals("", doc.getText(0, doc.getLength()));
        Element rootAfterUndo = doc.getDefaultRootElement();
        assertEquals(1, rootAfterUndo.getElementCount());
        assertEquals(0, rootAfterUndo.getElement(0).getStartOffset());
        assertEquals(1, rootAfterUndo.getElement(0).getEndOffset());

        // Redo insert
        assertTrue(undoManager.canRedo());
        undoManager.redo();
        assertEquals("First Line\nSecond Line", doc.getText(0, doc.getLength()));

        Element rootAfterRedo = doc.getDefaultRootElement();
        assertEquals(2, rootAfterRedo.getElementCount());
        assertEquals(0, rootAfterRedo.getElement(0).getStartOffset());
        assertEquals(11, rootAfterRedo.getElement(0).getEndOffset());
        assertEquals(11, rootAfterRedo.getElement(1).getStartOffset());
        assertEquals(23, rootAfterRedo.getElement(1).getEndOffset());
        assertEquals(12, posSecond.getOffset(), "Tracked position should be restored after redo");
    }

    @Test
    @DisplayName("8. Repeated typing and deletion in real JTextArea")
    void testRepeatedTypingAndDeletion() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);
        JTextArea textArea = new JTextArea(doc);

        textArea.setText("Initial text");
        assertEquals("Initial text", textArea.getText());

        textArea.append("\nAppended line 1");
        textArea.append("\nAppended line 2");
        assertTrue(textArea.getText().contains("Appended line 2"));

        doc.remove(0, doc.getLength());
        assertEquals("", textArea.getText());

        for (int i = 0; i < 50; i++) {
            textArea.append("Item " + i + "\n");
        }
        assertEquals(51, doc.getDefaultRootElement().getElementCount());
    }

    @Test
    @DisplayName("9. Repainting and resizing text area without StateInvariantError")
    void testRepaintAndResizeWithoutStateInvariantError() throws BadLocationException {
        GapBufferContent content = new GapBufferContent();
        PlainDocument doc = new PlainDocument(content);
        JTextArea textArea = new JTextArea(doc);
        textArea.setSize(400, 400);

        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Initial repaint
        assertDoesNotThrow(() -> textArea.paint(g));

        // 2. Type text at start then newline (the exact pattern that previously triggered the bug)
        doc.insertString(0, "Hello", null);
        assertDoesNotThrow(() -> textArea.paint(g));

        doc.insertString(5, "\nWorld\nAnother line", null);
        assertDoesNotThrow(() -> textArea.paint(g));

        // 3. Resizing text area
        textArea.setSize(800, 600);
        BufferedImage imgLarge = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gLarge = imgLarge.createGraphics();
        assertDoesNotThrow(() -> textArea.paint(gLarge));

        // 4. Repeated edits and paints
        for (int i = 0; i < 20; i++) {
            doc.insertString(0, "Prefix" + i + "\n", null);
            assertDoesNotThrow(() -> textArea.paint(gLarge));
        }

        g.dispose();
        gLarge.dispose();
    }
}
