package com.manolovindustries.dictionaryscan;

import android.util.Log;

/**
 * JUMBLE FILTER
 *
 * Filters out words whose multiset of letters
 * is not a subset of the jumble.
 */
public class JumbleFilter implements Filter {

    private Jumble jumble;

    // Behavior constants
    public static final int SUBSET = 0;     // passing words are a subset of the jumble
    public static final int EXACT = 1;      // the jumble set is equal to that of passing words
    public static final int SUPERSET = 2;   // the jumble is a subset of passing words

    public static final int DEFAULT_MODE = SUBSET;
    private int mode = DEFAULT_MODE;

    /**
     * CONSTRUCTOR
     *
     * Initializes the filter to an operational mode.
     *
     * @param mode operational mode constant flag
     */
    public JumbleFilter(int mode) {

        switch (mode) {
            case (EXACT) :
                this.mode = EXACT;
                break;
            case (SUBSET) :
                this.mode = SUBSET;
                break;
            case (SUPERSET) :
                this.mode = SUPERSET;
                break;
            default :
                this.mode = DEFAULT_MODE;
                break;
        }
    }

    public boolean pass(String word) {

        // Perform length check before jumblizing the word



        // Define which jumble constitutes what's needed and which is what's had
        //Jumble temp = new Jumble(word);
        Jumble need, have;
        switch (this.mode) {
            case (SUBSET) : // 'word' is subset of jumble
                if (word.length() > jumble.total()) {
                    return false;
                }
                Log.d('"' + word + '"', "Can \"" + word + "\" be formed from jumble?");
                need = new Jumble(word);        // "need" to form the 'word'
                have = jumble;      // "have" letters from 'jumble' to do it
                break;
            case (SUPERSET) : // 'word' is superset of jumble
                if (jumble.total() > word.length()) {
                    return false;
                }
                Log.d('"' + word + '"', "Can jumble be formed from \"" + word + "\"?");
                need = jumble;      // "need" to form the 'jumble'
                have = new Jumble(word);        // "have" the letters from 'word' to do it
                break;
            default : // 'word' must be formed from exactly what's within jumble
                if (jumble.total() != word.length()) {
                    return false;
                }
                need = jumble;
                have = new Jumble(word);
                break;
        }

        // Universally reject any word where the need is greater than the had
        //if (have.total() < need.total()) {
          //  Log.d('"' + word + '"', "FAILED 'TOTAL' TEST");
            //return false;
        //}/
        //if (this.mode == EXACT) {
          //  if (need.total() < have.total()) {
            //    return false;
            //}
        //}

        // Universally reject any situation where what could be needed couldn't be had
        if (need.minDistinction() > have.maxDistinction()) {
            Log.d('"' + word + '"', "FAILED 'DISTINCTION' TEST");
            return false;
        }
        if (this.mode == EXACT) {
            if (have.minDistinction() > need.maxDistinction()) {
                return false;
            }
        }

        if (!sufficient(need, have)) {
            return false;
        }
        if (this.mode == EXACT) {
            if (!sufficient(have, need)) {
                return false;
            }
        }

        Log.d('"' + word + '"', "Word passes the filter!");
        return true;
    }

    /**
     * Produce a JumbleFilter of the given
     * configuration.
     * @param x definition
     * @return reference to new jumble filter
     */
    public Filter spawn(String x) {
        JumbleFilter filter = new JumbleFilter(this.mode);
        filter.jumble = new Jumble(x);

        return filter;
    }

    /**
     * Checks for a sufficiency in the number of letters had in
     * order to produce those needed from the jumble.
     *
     * @param need goal word
     * @param have pool of letters had
     * @return TRUE ==> jumble can form what's needed, FALSE==> no deficiency
     */
    private boolean sufficient(Jumble need, Jumble have) {

        // Find any deficiency in what's needed
        int deficiency = 0;
        int wilds = have.count(Jumble.WILDCARD);
        for (Character ch : need.distincts()) {
            if (ch != Jumble.WILDCARD) {
                int net = have.count(ch) - need.count(ch);
                if (net < 0) {
                    deficiency += net;
                    if (deficiency + wilds < 0) {
                        //Log.d('"' + word + '"', "FAILED 'DEFICIENCY' TEST");
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
