
package com.hossam.notepad.service;

import com.hossam.notepad.algo.PatternMatcher;

import java.util.List;
import java.util.Objects;

public class SearchService {

    private final PatternMatcher matcher;

    public SearchService(PatternMatcher matcher) {
        this.matcher = Objects.requireNonNull(matcher);
    }

    public List<Integer> findAll(CharSequence text, String pattern) {
        return matcher.findAll(text, pattern);
    }

    public int findNext(CharSequence text, String pattern, int from) {
        return matcher.findNext(text, pattern, from);
    }
}
