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

    @Test
    void insertAtEnd() {
        GapBuffer b = new GapBuffer();
        b.insert(0, "Hello");

        assertEquals(5, b.length());
        assertEquals("Hello", b.toString());
    }
    @Test
    void insertInMiddle() {
        GapBuffer b = new GapBuffer();
        b.insert(0, "Hllo");
        b.insert(1, "e");
        assertEquals("Hello", b.toString());
    }

    @Test
    void insertLongerThanGap() {
        GapBuffer b = new GapBuffer(2);
        b.insert(0, "Hello World");
        assertEquals("Hello World", b.toString());
    }

    @Test
    void insertAtStartAfterEnd() {
        GapBuffer b = new GapBuffer(4);
        b.insert(0, "World");
        b.insert(0, "Hello ");
        assertEquals("Hello World", b.toString());
    }
    @Test
    void deleteMiddle() {
        GapBuffer b = new GapBuffer();
        b.insert(0, "Hello");
        b.delete(1, 2);
        assertEquals("Hlo", b.toString());
    }

    @Test
    void deleteAcrossGapMove() {
        GapBuffer b = new GapBuffer();
        b.insert(0, "Hello World");
        b.insert(5, ",");
        b.delete(0, 3);
        assertEquals("lo, World", b.toString());
    }

    @Test
    void invalidIndexThrows() {
        GapBuffer b = new GapBuffer();
        assertThrows(IndexOutOfBoundsException.class, () -> b.insert(1, "x"));
    }

    @Test
    void randomOperationsMatchStringBuilder() {
        for (long seed = 1; seed <= 20; seed++) {
            java.util.Random rnd = new java.util.Random(seed);
            GapBuffer gb = new GapBuffer(4);
            StringBuilder sb = new StringBuilder();

            for (int step = 0; step < 10_000; step++) {
                if (sb.length() == 0 || rnd.nextBoolean()) {
                    // Insert
                    int index = rnd.nextInt(sb.length() + 1);
                    int len = rnd.nextInt(6);

                    StringBuilder text = new StringBuilder();
                    for (int i = 0; i < len; i++) {
                        text.append((char) ('a' + rnd.nextInt(26)));
                    }

                    gb.insert(index, text.toString());
                    sb.insert(index, text.toString());

                } else {
                    // Delete
                    int index = rnd.nextInt(sb.length());
                    int len = rnd.nextInt(sb.length() - index + 1);

                    gb.delete(index, len);
                    sb.delete(index, index + len);
                }

                assertEquals(
                        sb.toString(),
                        gb.toString(),
                        "seed=" + seed + " step=" + step
                );
            }
        }
    }
}