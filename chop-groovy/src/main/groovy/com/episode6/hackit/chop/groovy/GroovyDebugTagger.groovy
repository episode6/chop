package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A tagger meant for use in groovy apps that use chop.
 * Groovy's stacktraces are quite a bit more dynamic than java's, so we can't just
 * 'know' which element in the stacktrace the log originated from. Instead we search
 * for the first non-system callsite.
 */
class GroovyDebugTagger implements Chop.Tagger {
  private static final List<String> SYSTEM_CALLSITES = [
      "sun.reflect",
      "java.lang.reflect",
      "org.codehaus.groovy.reflection",
      "org.codehaus.groovy.runtime.callsite",
      "com.episode6.hackit.chop.groovy.GroovyDebugTagger",
      "com.episode6.hackit.chop.ChopInternals",
      "com.episode6.hackit.chop.Chop",
      "groovy.lang"
  ]

  private final String CLASS_LINE_FORMAT = "%s:%d";
  private final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile(/\$\d+$/);
  private final Pattern CLOSURE_PATTERN = Pattern.compile(/\$\_\w+\_closure\d+$/);

  @Override
  String createTag() {
    StackTraceElement element = findFirstNonSystemElement(new Throwable())
    String tag = element.className
    tag = applyPattern(tag, ANONYMOUS_CLASS_PATTERN)
    tag = applyPattern(tag, CLOSURE_PATTERN)
    return String.format(
        CLASS_LINE_FORMAT,
        tag.substring(tag.lastIndexOf('.') + 1),
        element.lineNumber)
  }

  private static StackTraceElement findFirstNonSystemElement(Throwable t) {
    // All the groovy stack traces I've seen have the appropriate line at least 19 entries down (and have ~90 entries)
    // I'm too much of a wuss to start that high up, so this fuzzy logic is meant to ensure we don't accidentally
    // skip the important line in smaller stack traces but hopefully cut down on the big-O of this method for larger
    // stack traces that we're more likely to get
    int startIndex = t.stackTrace.length < 50 ? 3 : 15; // java traces have the important line at entry 3,
                                                        // so start there for smaller traces
    for (int i = startIndex; i < t.stackTrace.length; i++) {
      if (!isSystemCallsite(t.stackTrace[i])) {
        return t.stackTrace[i]
      }
    }
  }

  private static boolean isSystemCallsite(StackTraceElement element) {
    return SYSTEM_CALLSITES.find {element.className.startsWith(it)} != null
  }

  private static String applyPattern(String tag, Pattern pattern) {
    Matcher m = pattern.matcher(tag);
    if (m.find()) {
      return m.replaceAll("");
    }
    return tag
  }
}
