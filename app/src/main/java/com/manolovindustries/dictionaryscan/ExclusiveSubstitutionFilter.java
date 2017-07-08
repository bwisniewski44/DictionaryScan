package com.manolovindustries.dictionaryscan;

import android.util.Log;

import java.util.Set;

import static java.lang.System.in;

/**
 * Created by faith on 7/7/2017.
 */

public class ExclusiveSubstitutionFilter extends SubstitutionFilter {



    public boolean pass(String w) {
        Log.d("Exclusive filter: pass", "Entered the override 'pass' function");
        if (w.length() == this.length) {
            Set<Character> specifiedLetters = this.knowns.keySet();

            for (char ch : specifiedLetters) {
                Set<Integer> demandedPositions = this.knowns.get(ch);
                for (int i : demandedPositions) {
                    if (w.charAt(i) != ch) {
                        Log.d("ExcSubFilt pass[26]", "Char '" + ch + "' does not feature at position " + Integer.toString(i) + " in \"" + w + "\"");
                        return false;
                    }
                }
            }

            for (SubstitutionData substitution : this.substitutions) {
                Character ch = w.charAt(substitution.leadingPosition);
                if (specifiedLetters.contains(ch)) {
                    Log.d("ExcSubFilt pass[35]", '\'' + ch + "' violates exclusivity (has already been placed in word \"" + w + '"');
                    return false;
                } else {
                    specifiedLetters.add(ch);
                    for (int i : substitution.neighborPositions) {
                        if (w.charAt(i) != ch) {
                            Log.d("ExcSubFilt pass[41]", "Expecting '" + ch + " at pos[" + Integer.toString(i) + "] in \"" + w + '"');
                            return false;
                        }
                    }
                }
            }

            Log.d("ExbSubFilt pass[48]", "Word '" + w + "' passes through the filter");
            return true;
        }

        return false;
    }
    

    public Filter spawn(String definition) {
        ExclusiveSubstitutionFilter filter = new ExclusiveSubstitutionFilter();
        filter.define(definition);

        return filter;
    }
}
