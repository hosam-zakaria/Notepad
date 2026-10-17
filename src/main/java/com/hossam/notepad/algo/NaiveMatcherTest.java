package com.hossam.notepad.algo;

import com.hossam.notepad.buffer.GapBuffer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NaiveMatcherTest {

    private final List<PatternMatcher> matchers = List.of(
            new NaiveMatcher(),
            new KmpMatcher()
    );

    @Test
    void findsOverlapping() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(
                    List.of(0, 1),
                    matcher.findAll("aaa", "aa")
            );
        }
    }

    @Test
    void noMatch() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(
                    List.of(),
                    matcher.findAll("abcdef", "xyz")
            );
        }
    }

    @Test
    void emptyPattern() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(
                    List.of(),
                    matcher.findAll("abc", "")
            );

            assertEquals(-1, matcher.findNext("abc", "", 0));
        }
    }

    @Test
    void patternLongerThanText() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(
                    List.of(),
                    matcher.findAll("ab", "abcd")
            );
        }
    }

    @Test
    void findsAllMatches() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(
                    List.of(0, 2, 4),
                    matcher.findAll("ababab", "ab")
            );
        }
    }

    @Test
    void findsNextFromPosition() {
        for (PatternMatcher matcher : matchers) {
            assertEquals(2, matcher.findNext("ababab", "ab", 1));
            assertEquals(-1, matcher.findNext("ababab", "ab", 6));
        }
    }

    @Test
    void supportsGapBufferAsCharSequence() {
        for (PatternMatcher matcher : matchers) {
            GapBuffer gb = new GapBuffer();
            gb.insert(0, "aaa");

            assertEquals(
                    List.of(0, 1),
                    matcher.findAll(gb, "aa")
            );
        }
    }

    @Test
    void kmpMatchesNaiveOnRandomInput() {
        NaiveMatcher naive = new NaiveMatcher();
        KmpMatcher kmp = new KmpMatcher();

        for (long seed = 1; seed <= 20; seed++) {
            java.util.Random rnd = new java.util.Random(seed);

            for (int step = 0; step < 5_000; step++) {
                // Random text: length 0..30
                int n = rnd.nextInt(31);
                StringBuilder textBuilder = new StringBuilder();

                for (int i = 0; i < n; i++) {
                    textBuilder.append(rnd.nextBoolean() ? 'a' : 'b');
                }

                String text = textBuilder.toString();

                // Random pattern: length 1..5
                int m = rnd.nextInt(5) + 1;
                StringBuilder patternBuilder = new StringBuilder();

                for (int i = 0; i < m; i++) {
                    patternBuilder.append(rnd.nextBoolean() ? 'a' : 'b');
                }

                String pattern = patternBuilder.toString();

                // Compare findAll
                org.junit.jupiter.api.Assertions.assertEquals(
                        naive.findAll(text, pattern),
                        kmp.findAll(text, pattern),
                        "findAll: seed=" + seed + " step=" + step
                                + " text=" + text + " pattern=" + pattern
                );

                // Random starting position: 0..text.length()
                int from = rnd.nextInt(text.length() + 1);

                // Compare findNext
                org.junit.jupiter.api.Assertions.assertEquals(
                        naive.findNext(text, pattern, from),
                        kmp.findNext(text, pattern, from),
                        "findNext: seed=" + seed + " step=" + step
                                + " text=" + text + " pattern=" + pattern
                                + " from=" + from
                );
            }
        }
    }

}