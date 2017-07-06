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

    private int length;
    private Map<Character, Set<Integer>> definedPositions;
    private Set<SubstitutionData> substitutions;
    private boolean exclusiveMode;

    private class SubstitutionData {
        int leadingPosition;  // leading position of a variable
        Set<Integer> neighborPositions;   // set of indices at which a word must have the same letter

        SubstitutionData() {
            neighborPositions = new HashSet<Integer>();
        }
    }

    public SubstitutionFilter() {
        this.definedPositions = new HashMap<Character, Set<Integer>>();
        this.substitutions = new HashSet<SubstitutionData>();
    }

    public boolean pass(String w) {
        if (w.length() == this.length) {

            // Ensure that those defined characters are present
            for (Character ch : definedPositions.keySet()) {
                for (Integer index : definedPositions.get(ch)) {
                    if (w.charAt(index) != ch) {
                        return false;
                    }
                }
            }

            // Ensure that those substituted characters match
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

    public Filter spawn(String definition) {

        SubstitutionFilter filter = new SubstitutionFilter();
        filter.length = definition.length();

        Set<Integer> skipPositions = new HashSet<>();
        for (int i=0; i<definition.length(); i++) {

            // If this position is not already accounted for...
            if (!skipPositions.contains(i)) {

                Character letter = definition.charAt(i);
                if (Character.isUpperCase(letter) || Character.isLowerCase(letter)) {
                    boolean isVariable = Character.isUpperCase(letter);
                    letter = Character.toUpperCase(letter);

                    if (isVariable) {
                        SubstitutionData data = new SubstitutionData();
                        data.leadingPosition = i;

                        for (int j = i + 1; j < definition.length(); j++) {
                            if (definition.charAt(j) == definition.charAt(i)) {
                                data.neighborPositions.add(j);
                                skipPositions.add(j);
                            }
                        }

                        filter.substitutions.add(data);
                    } else {
                        char desensitizedKey = Character.toUpperCase(letter);
                        if (!filter.definedPositions.containsKey(desensitizedKey)) {
                            Set<Integer> positions = new HashSet<>();
                            filter.definedPositions.put(desensitizedKey, positions);
                        }
                        filter.definedPositions.get(desensitizedKey).add(i);
                    }
                }
            }
        }

        return filter;
    }
}

