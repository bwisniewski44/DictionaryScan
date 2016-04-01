package com.manolovindustries.dictionaryscan;

/**
 * Filters words based upon their length.
 * Defined bounds are inclusive.
 */
public class LengthFilter implements Filter {

    public int min, max;
    public boolean HAS_MIN, HAS_MAX;

    private static final char SEPARATOR = ','; // separator must be non-numeric

    /**
     * CONSTRUCTOR.
     * Used for a filter where the bounds are each used.
     * @param min minimum length of passing word (inclusive)
     * @param max maximum length of passing word (inclusive)
     */
    public LengthFilter(int min, int max) {
        this.min = min;
        this.max = max;

        HAS_MIN = (this.min > 0);
        HAS_MAX = (this.max > 0);
    }

    public boolean pass(String word) {

        // I'd think that this is likely to be violated first
        if (this.HAS_MAX) {
            if (word.length() > this.max) {
                return false;
            }
        }

        //
        if (this.HAS_MIN) {
            if (word.length() < this.min) {
                return false;
            }
        }

        return true;
    }

    public Filter spawn(String x) {
        //return LengthFilter.fromString(x);
        return new LengthFilter(this.min, this.max);
    }

    /**
     * Builds and returns a string-encoded filter state
     * definition.
     *
     * @return string definition of this filter
     */
    public String toString() {
        return Integer.toString(min) + ',' + Integer.toString(max);
    }

    /**
     * Builds an instance of LengthFilter from a string-
     * encoded filter state definition and returns a
     * reference to that instance.
     *
     * @param x string-encoded filter state definition
     * @return reference to new filer instance
     */
    public static LengthFilter fromString(String x) {
        int divIndex = x.lastIndexOf(SEPARATOR);
        int min = Integer.parseInt(x.substring(0, divIndex));
        int max = Integer.parseInt(x.substring(divIndex+1, x.length()));
        return new LengthFilter(min, max);
    }
}
