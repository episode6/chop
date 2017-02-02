package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A tagger meant for use in groovy apps that use chop
 */
class GroovyDebugTagger implements Chop.Tagger {
  private final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile(/\$\d+$/);

  @Override
  public String createTag() {
    String tag = new Throwable().getStackTrace()[3].getClassName();
    Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);
    if (m.find()) {
      tag = m.replaceAll("");
    }
    return tag.substring(tag.lastIndexOf('.') + 1);
  }
}
