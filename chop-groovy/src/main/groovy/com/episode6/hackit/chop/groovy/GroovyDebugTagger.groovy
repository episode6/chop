package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A tagger meant for use in groovy apps that use chop
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

  private final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile(/\$\d+$/);
  private final Pattern CLOSURE_PATTERN = Pattern.compile(/\$\_\w+\_closure\d+$/);

  @Override
  String createTag() {
    String tag = new Throwable().stackTrace.find {!isSystemCallsite(it)}.className
    tag = applyPattern(tag, ANONYMOUS_CLASS_PATTERN)
    tag = applyPattern(tag, CLOSURE_PATTERN)
    return tag.substring(tag.lastIndexOf('.') + 1);
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
