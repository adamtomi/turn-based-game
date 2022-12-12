package com.vamk.tbg.util;

import java.util.List;

/**
 * Utility class to help with "endlessly"
 * iterating through a list.
 */
public class Cursor<T> {
    private final List<T> elements;
    private int cursor;

    public Cursor(List<T> elements, int cursor) {
        if (elements.isEmpty()) throw new IllegalArgumentException("This list is empty");
        this.elements = elements;
        this.cursor = cursor;
    }

    public Cursor(List<T> elements) {
        this(elements, 0);
    }

    /**
     * Moves the cursor forward, then returns the element at that index.
     * If it's at the end of the list, the cursor gets reset to 0,
     * so that it can start over again.
     *
     * @return The element at the currently selected index
     */
    public T advance() {
        if (this.cursor >= this.elements.size() - 1) {
            this.cursor = 0;
        } else {
            this.cursor++;
        }

        return this.elements.get(this.cursor);
    }

    /**
     * Returns the internal cursor.
     */
    public int getInternalCursor() {
        return this.cursor;
    }

    /**
     * Rollback the cursor to the previous position.
     * If it's at 0 right now, it's set to the index
     * of the last element in the list.
     */
    public void rollback() {
        if (this.cursor == 0) {
            this.cursor = this.elements.size() - 1;
        } else {
            this.cursor--;
        }
    }
}
