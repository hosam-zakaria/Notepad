
package com.hossam.notepad.algo;

import java.util.ArrayList;
import java.util.List;

public class KmpMatcher implements PatternMatcher {

    private int[] buildLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];

        int len = 0;

        for (int i = 1; i < m; i++) {
            while (len > 0 && pattern.charAt(i) != pattern.charAt(len)) {
                len = lps[len - 1];
            }

            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
            }

            lps[i] = len;
        }

        return lps;
    }

    @Override
    public List<Integer> findAll(CharSequence text, String pattern) {
        List<Integer> ans = new ArrayList<>();

        if (text == null || pattern == null ||
                pattern.isEmpty() || pattern.length() > text.length()) {
            return ans;
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = buildLps(pattern);

        int j = 0;

        for (int i = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
                j = lps[j - 1];
            }

            if (text.charAt(i) == pattern.charAt(j)) {
                j++;
            }

            if (j == m) {
                ans.add(i - m + 1);
                j = lps[j - 1];
            }
        }

        return ans;
    }

    @Override
    public int findNext(CharSequence text, String pattern, int from) {
        if (text == null || pattern == null ||
                pattern.isEmpty() || pattern.length() > text.length()) {
            return -1;
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = buildLps(pattern);

        from = Math.max(0, from);

        int j = 0;

        for (int i = from; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
                j = lps[j - 1];
            }

            if (text.charAt(i) == pattern.charAt(j)) {
                j++;
            }

            if (j == m) {
                return i - m + 1;
            }
        }

        return -1;
    }
}
