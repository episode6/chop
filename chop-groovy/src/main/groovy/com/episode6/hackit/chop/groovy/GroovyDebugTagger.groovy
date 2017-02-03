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
      "org.codehaus.groovy",
      "groovy.lang",
      "com.episode6.hackit.chop.groovy.GroovyDebugTagger",
      "com.episode6.hackit.chop.ChopInternals",
      "com.episode6.hackit.chop.Chop"
  ]

  private final String CLASS_LINE_FORMAT = "%s:%d";
  private final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile(/\$\d+$/);
  private final Pattern CLOSURE_PATTERN = Pattern.compile(/\$\_\w+\_closure\d+$/);

  @Override
  String createTag() {
    Throwable t = new Throwable()
    StackTraceElement element = new Throwable().stackTrace.find {!isSystemCallsite(it)}
    String tag = element.className
    tag = applyPattern(tag, ANONYMOUS_CLASS_PATTERN)
    tag = applyPattern(tag, CLOSURE_PATTERN)
    return String.format(
        CLASS_LINE_FORMAT,
        tag.substring(tag.lastIndexOf('.') + 1),
        element.lineNumber)
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
