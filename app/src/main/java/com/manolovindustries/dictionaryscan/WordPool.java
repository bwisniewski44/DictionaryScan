package com.manolovindustries.dictionaryscan;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Wrapper class around the data structure
 * containing the pool of match possibilities.
 *
 * This wrapper is optimized for speed in
 * listing/de-listing words.
 */
public class WordPool {

    private int MAX_LENGTH;
    private Queue<String> pool;

    public WordPool() {
        pool = new ArrayDeque<>();
        MAX_LENGTH = 0;
    }

    public void add(String word) {
        pool.add(word);
        if (word.length() > MAX_LENGTH) {
            MAX_LENGTH = word.length();
        }
    }

    public String remove() {
        return pool.remove();
        // add some max length reassessment here
    }

    public int size() {
        return pool.size();
    }

    public void clear() {
        pool.clear();
    }

    public int maxLength() {
        return MAX_LENGTH;
    }
}
