
package com.hossam.notepad.algo;

import java.util.ArrayList;
import java.util.List;

public class NaiveMatcher implements PatternMatcher {

    @Override
    public List<Integer> findAll(CharSequence text, String pattern) {
        List<Integer> ans = new ArrayList<>();

        if (text == null || pattern == null ||
                pattern.isEmpty() || pattern.length() > text.length()) {
            return ans;
        }

        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;

            while (j < pattern.length() &&
                    text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == pattern.length()) {
                ans.add(i);
            }
        }

        return ans;
    }

    @Override
    public int findNext(CharSequence text, String pattern, int from) {
        if (text == null || pattern == null || pattern.isEmpty() ||
                pattern.length() > text.length()) {
            return -1;
        }

        from = Math.max(0, from);

        for (int i = from; i <= text.length() - pattern.length(); i++) {
            int j = 0;

            while (j < pattern.length() &&
                    text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == pattern.length()) {
                return i;
            }
        }

        return -1;
    }
}
