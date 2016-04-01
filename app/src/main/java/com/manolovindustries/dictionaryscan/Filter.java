package com.manolovindustries.dictionaryscan;

/**
 * Created by Brian on 5/3/2015.
 *
 * FILTER
 *
 * Interface which describes the operation of a word filter.
 * Contains a method which flags whether or not a word can
 * pass through the filter.
 */
public interface Filter {

    /**
     * Determines whether or not a word may pass through this filter.
     * @param word test word
     * @return TRUE if word may pass, FALSE otherwise
     */
    public boolean pass(String word);

    /**
     * Generates a new filter based on the provided
     * definition.
     * @param x definition
     * @return reference to newly-generated filter
     */
    public Filter spawn(String x);
}
