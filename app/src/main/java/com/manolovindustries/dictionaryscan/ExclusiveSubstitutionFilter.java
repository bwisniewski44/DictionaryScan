package com.manolovindustries.dictionaryscan;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import static java.lang.System.in;

/**
 * Created by faith on 7/7/2017.
 */

public class ExclusiveSubstitutionFilter extends SubstitutionFilter {



    public boolean pass(String w) {
        // Log.d("Exclusive filter: pass", "Entered the override 'pass' function");
        if (w.length() == this.length) {
            Set<Character> specifiedLetters = new HashSet<>(this.knowns.keySet());  // get a copy of the knowns

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
                    Log.d("ExcSubFilt pass[35]", "ch['" + ch + "'] violates exclusivity (has already been placed in word \"" + w + '"');
                    return false;
                } else {
                    Log.d("ExcSubFilt pass[38]", "About to add letter '" + ch + "' to those which have been defined in word '" + w + "'");
                    try {
                    specifiedLetters.add(ch);}
                    catch (UnsupportedOperationException e) {
                        Log.d("ExcSubFilt pass[42]", "Caught error: " + e.getMessage());
                        if (ch == null) {
                            Log.d("ExcSubFilt pass[43]", "Indeed ch==null for pos[" + Integer.toString(substitution.leadingPosition) + "] in '" + w + "'");
                        }
                        else
                        {
                            Log.d("ExcSubFilt pass[48]", "Weird we're getting null for ch=[" + Character.toString(ch) + "] at pos[" + Integer.toString(substitution.leadingPosition) + " in '" + w + "'");
                        }
                    }
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
