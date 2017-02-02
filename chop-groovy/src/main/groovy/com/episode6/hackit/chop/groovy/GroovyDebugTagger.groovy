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

  @Override
  public String createTag() {
    println "starting test\n\n"
    Throwable t = new Throwable()
    t.printStackTrace()

    StackTraceElement elm = t.stackTrace.find {
      boolean shoudlSkip = isSystemCallsite(it)
      return !shoudlSkip
    }
    println "found elm ${elm}"


    String tag = elm.className
    Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);
    if (m.find()) {
      tag = m.replaceAll("");
    }
    return tag.substring(tag.lastIndexOf('.') + 1);
  }

  private static boolean isSystemCallsite(StackTraceElement element) {
    for (String systemCallsite : SYSTEM_CALLSITES) {
      if (element.className.startsWith(systemCallsite)) {
        println "${element.className} starts with ${systemCallsite}"
        return true
      }
    }
    return false
  }
}
