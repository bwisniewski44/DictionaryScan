package com.manolovindustries.dictionaryscan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.lang.System.in;

/**
 * Created by faith on 7/4/2017.
 *
 * The Substitution Filter allows for the substitution
 * of letters into specific positions of a word,
 * a constraint not easily achieved through a true
 * regular expression.
 */

public class SubstitutionFilter implements Filter {

    protected int length;
    protected Map<Character, Set<Integer>> knowns;
    protected Set<SubstitutionData> substitutions;

    protected class SubstitutionData {
        int leadingPosition;  // leading position of a variable
        Set<Integer> neighborPositions;   // set of indices at which a word must have the same letter

        SubstitutionData() {
            neighborPositions = new HashSet<Integer>();
        }
    }

    public SubstitutionFilter() {
        this.knowns = new HashMap<Character, Set<Integer>>();
        this.substitutions = new HashSet<SubstitutionData>();
    }

    public boolean pass(String w) {
        if (w.length() == this.length) {

            // Ensure the known letters are found at each of their respective indices
            for (Character letter : this.knowns.keySet()) {
                Set<Integer> indices = this.knowns.get(letter);
                for (Integer index : indices) {
                    if (w.charAt(index) != letter) {
                        return false;
                    }
                }
            }

            // Ensure that a substitution can be supplied in each position for each variable
            for (SubstitutionData substitution : substitutions) {
                for (int comparisonIndex : substitution.neighborPositions) {
                    if (w.charAt(substitution.leadingPosition) != w.charAt(comparisonIndex)) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    /*
     *  Sets the state of this SubstitutionFilter.
     */
    protected void define(String definition) {
        this.length = definition.length();
        this.knowns.clear();

        // Proceed to define knowns and substitution variables
        Set<Integer> processedPositions = new HashSet<>();
        for (int i=0; i<definition.length(); i++) {
            if (!processedPositions.contains(i)) {    // if not already having accounted for this position
                // For any letter at this position...
                Character letter = definition.charAt(i);
                if (Character.isUpperCase(letter) || Character.isLowerCase(letter)) {
                    boolean isVariable = Character.isUpperCase(letter);     // determine whether it may have plain-text letters substituted into it
                    letter = Character.toUpperCase(letter);                 // de-sensitize case

                    if (isVariable) {
                        SubstitutionData data = new SubstitutionData();
                        data.leadingPosition = i;

                        // Search for the variable in the remainder of the definition
                        for (int j=i+1; j<definition.length(); j++) {
                            if (definition.charAt(i) == definition.charAt(j)) {
                                data.neighborPositions.add(j);
                                processedPositions.add(j);
                            }
                        }
                        this.substitutions.add(data);
                    }
                    else { // if encountering a known letter
                        // Ensure letter features in 'knowns'
                        if (!this.knowns.containsKey(letter)) {
                            Set<Integer> empty = new HashSet<Integer>();
                            this.knowns.put(letter, empty);
                        }
                        this.knowns.get(letter).add(i);
                    }
                }
            }
        }
    }

    public Filter spawn(String definition) {

        SubstitutionFilter filter = new SubstitutionFilter();
        filter.define(definition);

        return filter;
    }
}

