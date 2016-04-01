package com.manolovindustries.dictionaryscan;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REGEX FILTER
 *
 * This class filters words based on how they
 * match a regular expression.
 */
public class RegexFilter implements Filter {

    private Matcher m;

    public boolean pass(String word) {
        m.reset(word);
        boolean result = false;
        try {
            result = m.matches();
        }
        catch (IllegalArgumentException e) {
            Log.d("Matcher", e.getMessage());
        }
        return result;
    }

    public Filter spawn(String regex) {
        RegexFilter filter = new RegexFilter();
        Pattern p = Pattern.compile(regex);
        filter.m = p.matcher("");

        return filter;
    }
}
