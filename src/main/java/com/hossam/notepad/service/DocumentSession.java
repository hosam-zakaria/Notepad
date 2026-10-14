package com.hossam.notepad.service;
import java.nio.file.Path;

public class DocumentSession {
    private Path currentFile;
    private boolean dirty;

    public Path getCurrentFile() {
        return currentFile;
    }
    public boolean isDirty() {
        return dirty;
    }
    public boolean hasFile() {
        return (currentFile != null);
    }
    public void markDirty() {
        dirty = true;
    }
    public void markSaved(Path path) {
        currentFile = path;
        dirty = false;
    }
    public void reset() {
        currentFile = null;
        dirty = false;
    }
}
