package com.episode6.hackit.chop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public final class Chop {

  private static final TreeFarm TREE_FARM = new TreeFarm();

  private static Tagger sDefaultTagger = Defaults.TAGGER;
  private static Formatter sDefaultFormatter = Defaults.FORMATTER;

  public enum Level {
    V, D, I, W, E
  }

  /**
   * A Tree is a logging "source." That is to say a tree outputs logs somewhere.
   * A tree can support some, all or no Levels of log (although planting a tree that supports no
   * logs seems pointless. {@link #chopLog(Level, String, String)} will only be called if the Tree
   * returns true in {@link #supportsLevel(Level)} for the given level.
   *
   * The name Tree comes from Timber's analogy. I'm not sure it fits anymore and might change it
   * (since the responsibility of Trees in this case is a bit different).
   */
  public interface Tree {
    boolean supportsLevel(Level level);
    void chopLog(Level level, String tag, String message);
  }

  /**
   * The Tagger is responsible for creating tags for chopped logs. {@link #createTag()} will only
   * be called if a Tree (supporting the requested {@link Level}) is planted
   */
  public interface Tagger {
    String createTag();
  }

  /**
   * The Formatter is responsible for converting message/object combos & Throwables into Strings
   * than can be printed by the {@link Tree}s
   */
  public interface Formatter {
    String formatLog(String message, Object... args);
    String formatThrowable(Throwable throwable);
  }

  private static void chopLogs(
      Level level,
      Tagger tagger,
      Formatter formatter,
      @Nullable Throwable throwable,
      String message,
      Object... args) {

    if (!TREE_FARM.isLogLevelSupported(level)) {
      return;
    }

    String tag = tagger.createTag();
    String formattedMessage = formatter.formatLog(message, args);
    TREE_FARM.chopLogs(level, tag, formattedMessage);

    if (throwable != null) {
      String formattedThrowable = formatter.formatThrowable(throwable);
      TREE_FARM.chopLogs(level, tag, formattedThrowable);
    }
  }

  public static final class Defaults {
    public static final Tagger TAGGER = new Tagger() {

      private final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile("\\$\\d+$");

      @Override
      public String createTag() {
        String tag = new Throwable().getStackTrace()[3].getClassName();
        Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);
        if (m.find()) {
          tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
      }
    };

    public static final Formatter FORMATTER = new Formatter() {

      @Override
      public String formatLog(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
      }

      @Override
      public String formatThrowable(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        return sw.toString();
      }
    };
  }

  /**
   * The TreeFarm is where the {@link Tree}s are planted. It also keeps track of which
   * {@link Level}s are supported by the planted trees
   */
  private static final class TreeFarm {

    private final Set<Tree> mTrees;
    private final Map<Level, Boolean> mSupportedLevelMap;

    TreeFarm() {
      mTrees = new HashSet<Tree>();
      mSupportedLevelMap = new HashMap<Level, Boolean>();

      for (Level level : Level.values()) {
        mSupportedLevelMap.put(level, false);
      }
    }

    boolean plantTree(Tree tree) {
      if (mTrees.add(tree)) {
        for (Level level : Level.values()) {
          mSupportedLevelMap.put(level, tree.supportsLevel(level) || isLogLevelSupported(level));
        }
        return true;
      }
      return false;
    }

    boolean isLogLevelSupported(Level level) {
      return mSupportedLevelMap.get(level);
    }

    void chopLogs(Level level, String tag, String message) {
      for (Tree tree : mTrees) {
        if (tree.supportsLevel(level)) {
          tree.chopLog(level, tag, message);
        }
      }
    }
  }
}
