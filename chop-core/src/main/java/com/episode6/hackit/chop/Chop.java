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

  private static final ThreadLocal<ChoppingToolsAdapter> TOOLS_ADAPTER =
      new ThreadLocal<ChoppingToolsAdapter>() {

        @Override
        protected ChoppingToolsAdapter initialValue() {
          return new ChoppingToolsAdapter();
        }
      };

  private static final ThreadLocal<SettableTagger> STRING_TAGGER =
      new ThreadLocal<SettableTagger>() {

        @Override
        protected SettableTagger initialValue() {
          return new SettableTagger();
        }
      };

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

  public static boolean plantTree(Tree tree) {
    return TREE_FARM.plantTree(tree);
  }

  public static ChoppingToolsAdapter withTagger(Tagger tagger) {
    return TOOLS_ADAPTER.get().setFormatter(sDefaultFormatter).andTagger(tagger);
  }

  public static ChoppingToolsAdapter withFormatter(Formatter formatter) {
    return TOOLS_ADAPTER.get().setTagger(sDefaultTagger).andFormatter(formatter);
  }

  public static ChoppingToolsAdapter withTag(String tag) {
    return withTagger(STRING_TAGGER.get().setTag(tag));
  }

  public static void v(String message, Object... args) {
    chopLogs(Level.V, sDefaultTagger, sDefaultFormatter, null, message, args);
  }

  public static void v(Throwable throwable, String message, Object... args) {
    chopLogs(Level.V, sDefaultTagger, sDefaultFormatter, throwable, message, args);
  }

  public static void d(String message, Object... args) {
    chopLogs(Level.D, sDefaultTagger, sDefaultFormatter, null, message, args);
  }

  public static void d(Throwable throwable, String message, Object... args) {
    chopLogs(Level.D, sDefaultTagger, sDefaultFormatter, throwable, message, args);
  }

  public static void i(String message, Object... args) {
    chopLogs(Level.I, sDefaultTagger, sDefaultFormatter, null, message, args);
  }

  public static void i(Throwable throwable, String message, Object... args) {
    chopLogs(Level.I, sDefaultTagger, sDefaultFormatter, throwable, message, args);
  }

  public static void w(String message, Object... args) {
    chopLogs(Level.W, sDefaultTagger, sDefaultFormatter, null, message, args);
  }

  public static void w(Throwable throwable, String message, Object... args) {
    chopLogs(Level.W, sDefaultTagger, sDefaultFormatter, throwable, message, args);
  }

  public static void e(String message, Object... args) {
    chopLogs(Level.E, sDefaultTagger, sDefaultFormatter, null, message, args);
  }

  public static void e(Throwable throwable, String message, Object... args) {
    chopLogs(Level.E, sDefaultTagger, sDefaultFormatter, throwable, message, args);
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

  public static final class ChoppingToolsAdapter {
    private Tagger mTagger;
    private Formatter mFormatter;

    ChoppingToolsAdapter() {
      mTagger = sDefaultTagger;
      mFormatter = sDefaultFormatter;
    }

    public ChoppingToolsAdapter andTagger(Tagger tagger) {
      return setTagger(tagger);
    }

    public ChoppingToolsAdapter andFormatter(Formatter formatter) {
      return setFormatter(formatter);
    }

    public void byDefault() {

      // A ThreadLocal SettableTagger would make a pretty lousy default. Don't allow it.
      if (mTagger instanceof SettableTagger) {
        throw new IllegalArgumentException(
            "Cannot Chop.setTag(String).byDefault(). Use Chop.withTagger(Tagger).byDefault() " +
                "and roll your own Chop.Tagger instead.");
      }

      sDefaultTagger = mTagger;
      sDefaultFormatter = mFormatter;
    }

    public void v(String message, Object... args) {
      chopLogs(Level.V, mTagger, mFormatter, null, message, args);
    }

    public void v(Throwable throwable, String message, Object... args) {
      chopLogs(Level.V, mTagger, mFormatter, throwable, message, args);
    }

    public void d(String message, Object... args) {
      chopLogs(Level.D, mTagger, mFormatter, null, message, args);
    }

    public void d(Throwable throwable, String message, Object... args) {
      chopLogs(Level.D, mTagger, mFormatter, throwable, message, args);
    }

    public void i(String message, Object... args) {
      chopLogs(Level.I, mTagger, mFormatter, null, message, args);
    }

    public void i(Throwable throwable, String message, Object... args) {
      chopLogs(Level.I, mTagger, mFormatter, throwable, message, args);
    }

    public void w(String message, Object... args) {
      chopLogs(Level.W, mTagger, mFormatter, null, message, args);
    }

    public void w(Throwable throwable, String message, Object... args) {
      chopLogs(Level.W, mTagger, mFormatter, throwable, message, args);
    }

    public void e(String message, Object... args) {
      chopLogs(Level.E, mTagger, mFormatter, null, message, args);
    }

    public void e(Throwable throwable, String message, Object... args) {
      chopLogs(Level.E, mTagger, mFormatter, throwable, message, args);
    }

    ChoppingToolsAdapter setTagger(Tagger tagger) {
      mTagger = tagger;
      return this;
    }

    ChoppingToolsAdapter setFormatter(Formatter formatter) {
      mFormatter = formatter;
      return this;
    }
  }

  private static class SettableTagger implements Tagger {

    private String mTag;

    SettableTagger setTag(String tag) {
      mTag = tag;
      return this;
    }

    @Override
    public String createTag() {
      return mTag;
    }
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
