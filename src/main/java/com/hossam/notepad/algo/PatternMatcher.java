
package com.hossam.notepad.algo;

import java.util.List;

public interface PatternMatcher {

    List<Integer> findAll(CharSequence text, String pattern);

    int findNext(CharSequence text, String pattern, int from);
}
