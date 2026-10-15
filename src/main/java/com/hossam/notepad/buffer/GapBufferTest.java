package com.hossam.notepad.buffer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GapBufferTest {

    @Test
    void newBufferIsEmpty() {
        GapBuffer b = new GapBuffer();
        assertEquals(0, b.length());
        assertEquals("", b.toString());
    }
}