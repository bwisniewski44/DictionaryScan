package com.manolovindustries.dictionaryscan;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JUMBLE
 *
 * Models a multiset of letters.
 *
 * DISTINCTION is the measure of the number of distinct
 * elements in a set. Jumbles containing wildcards have
 * an ambiguous distinction. Therefore, distinction is
 * measured here
 */
public class Jumble {

    private Map<Character, Integer> jumble;     // multiset of letters
    private int total;                          // size of multiset of letters

    public static final char WILDCARD = '.';

    /**
     * CONSTRUCTOR
     *
     * Initializes an empty jumble.
     */
    public Jumble() {
        jumble = new HashMap<>();
        total = 0;
    }

    /**
     * CONSTRUCTOR
     *
     * Initializes a jumble to contain the
     * multiset of letters within the provided word.
     *
     * @param x string containing letters
     */
    public Jumble(String x) {
        jumble = new HashMap<>();
        total = 0;
        for (int i=0; i<x.length(); i++) {
            this.add(x.charAt(i));
            //total++;
        }
    }

    /**
     * CONSTRUCTOR
     *
     * Initializes this jumble to be a
     * copy of that provided.
     *
     * @param j jumble to be copied
     */
    public Jumble(Jumble j) {
        Set<Character> letters = j.distincts();
        for (char ch : letters) {
            //this.jumble.put(ch, j.jumble.get(ch));
            this.putBatch(ch, j.count(ch));
        }
        //this.total = j.total;
    }

    /**
     * Adds an instance of a character to the jumble.
     * @param ch letter to be added
     */
    public void add(char ch) {

        int count = 1;
        if (jumble.containsKey(ch)) {
            count = jumble.get(ch) + 1;
        }

        jumble.put(ch, count);
        total++;
    }

    /**
     * Adds instances of a letter.
     * @param ch letter to be added
     * @param volume number of instances of letter
     */
    public void putBatch(char ch, int volume) {
        int instances = volume;
        if (jumble.containsKey(ch)) {
            instances += jumble.get(ch);
        }

        jumble.put(ch, instances);
        total += volume;
    }

    /**
     * Removes an instance of a character from the jumble.
     * @param ch character to remove
     * @return number of remaining instances (-1 ==> char was not found)
     */
    public int pull(char ch) {
        int remainder = -1;

        // If the letter exists...
        if (jumble.containsKey(ch)) {
            remainder = jumble.get(ch) - 1; // get the number of instances remaining
            if (remainder > 0) {            // if anything remains, redefine the number of instances represented
                jumble.put(ch, remainder);
            }
            else {                          // otherwise, remove altogether
                jumble.remove(ch);
            }

            total--;    // decrement total number of instances of letters
        }

        return remainder;
    }

    /**
     * Removes all instances of a specific character
     * contained within the jumble.
     *
     * @param ch character to remove
     * @return number of instances removed (-1 ==> never in there in the first place)
     */
    public int pullBatch(char ch) {
        int removed = -1;

        if (jumble.containsKey(ch)) {
            removed = jumble.get(ch);
            jumble.remove(ch);
            total -= removed;
        }

        return removed;
    }

    /**
     * Returns the number of instances of the letter in the jumble.
     * @param ch letter to be counted
     * @return instance count of letter
     */
    public int count(char ch) {
        int count = 0;
        if (jumble.containsKey(ch)) {
            count = jumble.get(ch);
        }
        return count;
    }

    /**
     * Returns the total number of letters and wildcards
     * contained within the jumble.
     *
     * @return total number of instances of characters contained
     */
    public int total() {
        return this.total;
    }

    /**
     * Return the set of distinct letters contained in the jumble.
     *
     * @return set of distinct letters
     */
    public Set<Character> distincts() {
        return jumble.keySet();
    }

    /**
     * Gives the minimum possible distinct elements.
     * This varies by how wildcards resolve.
     *
     * @return minimum number of distinct elements
     */
    public int minDistinction() {

        // If the jumble could contain nothing, min distinction is 0
        if (this.total == 0) {
            return 0;
        }

        return this.distincts().size();
    }

    /**
     * Returns the maximum number of distinct elements.
     *
     * @return maximum number of distinct elements in jumble
     */
    public int maxDistinction() {

        int distinctTiles = this.distincts().size();    // # of unique Scrabble tiles
        int maxDistinction = distinctTiles;             // maximum # of distinct possibilities

        // If there are wildcards...
        int wilds = this.count(WILDCARD);
        if (wilds > 0) {
            int uniqueHardLetters = distinctTiles - 1;      // # of unique letters (w/o wildcards)
            maxDistinction = uniqueHardLetters + wilds;     // REVISED MAXIMUM DISTINCTION
        }

        return maxDistinction;
    }

}
