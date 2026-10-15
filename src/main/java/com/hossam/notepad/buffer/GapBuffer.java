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

    private void moveGap(int pos) {
        if (pos < gapStart) {
            int distance = gapStart - pos;

            for (int i = 0; i < distance; i++) {
                data[gapEnd - 1 - i] = data[gapStart - 1 - i];
            }

            gapStart = pos;
            gapEnd -= distance;
        }else if (pos > gapStart) {
            int distance = pos - gapStart;

            for (int i = 0; i < distance; i++) {
                data[gapStart + i] = data[gapEnd + i];
            }

            gapStart += distance;
            gapEnd += distance;
        }
    }
    private void ensureCapacity(int required) {
        int gapSize = gapEnd - gapStart;
        if (gapSize >= required) {
            return;
        }

        int newCapacity = Math.max(data.length * 2,length() + required);
        char[] newData = new char[newCapacity];
        System.arraycopy(data, 0, newData, 0, gapStart);

        int suffixLength = data.length - gapEnd;
        int newGapEnd = newCapacity - suffixLength;

        System.arraycopy(data, gapEnd, newData,
                newGapEnd, suffixLength);

        data = newData;
        gapEnd = newGapEnd;
    }
    public void insert(int index, String text) {
        if (index < 0 || index > length()) {
            throw new IndexOutOfBoundsException("index=" + index + ", length=" + length());
        }
        ensureCapacity(text.length());
        moveGap(index);
        for (int i = 0; i < text.length(); i++) {
            data[gapStart] = text.charAt(i);
            gapStart++;
        }
    }
    public void delete(int index, int len) {
        if (index < 0 || len < 0 || index + len > length()) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + ", length=" + length()
            );
        }
        moveGap(index + len);
        gapStart -= len;
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
        if (start < 0 || end > length() || start > end) {
            throw new IndexOutOfBoundsException(
                    "start=" + start + ", end=" + end +
                            ", length=" + length()
            );
        }

        return toString().substring(start, end);
    }
}
