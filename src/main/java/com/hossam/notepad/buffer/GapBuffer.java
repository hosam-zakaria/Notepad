package com.hossam.notepad.buffer;

public class GapBuffer implements CharSequence{
    private char[] data;
    private int gapStart, gapEnd;

    public GapBuffer() {
        this(16);
    }

    public GapBuffer(int initialCapacity){
        data = new char[initialCapacity];
        gapStart = 0;
        gapEnd = initialCapacity;
    }

    @Override
    public int length() {
        return data.length - (gapEnd - gapStart);
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= length()) {
            throw new IndexOutOfBoundsException();
        }
        if (index < gapStart) {
            return data[index];
        }
        return data[index + (gapEnd - gapStart)];
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length(); i++) {
            sb.append(charAt(i));
        }

        return sb.toString();
    }
    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }
}
