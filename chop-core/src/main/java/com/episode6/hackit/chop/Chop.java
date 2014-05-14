package com.episode6.hackit.chop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Chop {

  public enum Level {
    V, D, I, W, E
  }

  /**
   * A Tree is a logging "source." That is to say a tree outputs logs somewhere.
   * A tree can support some, all or no Levels of log (although planting a tree that supports no
   * logs seems pointless. {@link #chopLog(Chop.Level, String, String)} will only be called if the Tree
   * returns true in {@link #supportsLevel(Chop.Level)} for the given level.
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
   * be called if a Tree (supporting the requested {@link Chop.Level}) is planted
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
    return ChopInternals.TREE_FARM.plantTree(tree);
  }

  public static DefaultableChoppingToolsAdapter withTagger(Tagger tagger) {
    return ChopInternals.DEFAULTABLE_TOOLS_ADAPTER.get()
        .withDefaultFormatter()
        .andTagger(tagger)
        .cast(DefaultableChoppingToolsAdapter.class);
  }

  public static DefaultableChoppingToolsAdapter withFormatter(Formatter formatter) {
    return ChopInternals.DEFAULTABLE_TOOLS_ADAPTER.get()
        .withDefaultTagger()
        .andFormatter(formatter)
        .cast(DefaultableChoppingToolsAdapter.class);
  }

  public static ChoppingToolsAdapter withTag(String tag) {
    return ChopInternals.TOOLS_ADAPTER.get()
        .withDefaultFormatter()
        .andTagger(ChopInternals.STRING_TAGGER.get().withTag(tag));
  }

  public static void v(String message, Object... args) {
    ChopInternals.chopLogs(Level.V, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, null, message, args);
  }

  public static void v(Throwable throwable, String message, Object... args) {
    ChopInternals.chopLogs(Level.V, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, throwable, message, args);
  }

  public static void d(String message, Object... args) {
    ChopInternals.chopLogs(Level.D, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, null, message, args);
  }

  public static void d(Throwable throwable, String message, Object... args) {
    ChopInternals.chopLogs(Level.D, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, throwable, message, args);
  }

  public static void i(String message, Object... args) {
    ChopInternals.chopLogs(Level.I, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, null, message, args);
  }

  public static void i(Throwable throwable, String message, Object... args) {
    ChopInternals.chopLogs(Level.I, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, throwable, message, args);
  }

  public static void w(String message, Object... args) {
    ChopInternals.chopLogs(Level.W, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, null, message, args);
  }

  public static void w(Throwable throwable, String message, Object... args) {
    ChopInternals.chopLogs(Level.W, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, throwable, message, args);
  }

  public static void e(String message, Object... args) {
    ChopInternals.chopLogs(Level.E, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, null, message, args);
  }

  public static void e(Throwable throwable, String message, Object... args) {
    ChopInternals.chopLogs(Level.E, ChopInternals.sDefaultTagger, ChopInternals.sDefaultFormatter, throwable, message, args);
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
   * An implementation of {@link Chop.Tagger} that is settable/mutable
   */
  public static class SettableTagger implements Tagger {

    private String mTag;

    SettableTagger withTag(String tag) {
      mTag = tag;
      return this;
    }

    @Override
    public String createTag() {
      return mTag;
    }
  }

  public static class ChoppingToolsAdapter {
    protected Tagger mTagger;
    protected Formatter mFormatter;

    ChoppingToolsAdapter() {
      mTagger = ChopInternals.sDefaultTagger;
      mFormatter = ChopInternals.sDefaultFormatter;
    }

    public ChoppingToolsAdapter andTagger(Tagger tagger) {
      mTagger = tagger;
      return this;
    }

    public ChoppingToolsAdapter andFormatter(Formatter formatter) {
      mFormatter = formatter;
      return this;
    }

    public final void v(String message, Object... args) {
      ChopInternals.chopLogs(Level.V, mTagger, mFormatter, null, message, args);
    }

    public final void v(Throwable throwable, String message, Object... args) {
      ChopInternals.chopLogs(Level.V, mTagger, mFormatter, throwable, message, args);
    }

    public final void d(String message, Object... args) {
      ChopInternals.chopLogs(Level.D, mTagger, mFormatter, null, message, args);
    }

    public final void d(Throwable throwable, String message, Object... args) {
      ChopInternals.chopLogs(Level.D, mTagger, mFormatter, throwable, message, args);
    }

    public final void i(String message, Object... args) {
      ChopInternals.chopLogs(Level.I, mTagger, mFormatter, null, message, args);
    }

    public final void i(Throwable throwable, String message, Object... args) {
      ChopInternals.chopLogs(Level.I, mTagger, mFormatter, throwable, message, args);
    }

    public final void w(String message, Object... args) {
      ChopInternals.chopLogs(Level.W, mTagger, mFormatter, null, message, args);
    }

    public final void w(Throwable throwable, String message, Object... args) {
      ChopInternals.chopLogs(Level.W, mTagger, mFormatter, throwable, message, args);
    }

    public final void e(String message, Object... args) {
      ChopInternals.chopLogs(Level.E, mTagger, mFormatter, null, message, args);
    }

    public final void e(Throwable throwable, String message, Object... args) {
      ChopInternals.chopLogs(Level.E, mTagger, mFormatter, throwable, message, args);
    }

    ChoppingToolsAdapter withDefaultTagger() {
      mTagger = ChopInternals.sDefaultTagger;
      return this;
    }

    ChoppingToolsAdapter withDefaultFormatter() {
      mFormatter = ChopInternals.sDefaultFormatter;
      return this;
    }

    /**
     * Internal helper class to cast a this ChoppingToolsAdapter
     * as one of its subclasses
     * @return casted instance of this adapter
     */
    <T extends ChoppingToolsAdapter> T cast(Class<T> clazz) {
      return (T)this;
    }
  }

  public static class DefaultableChoppingToolsAdapter extends ChoppingToolsAdapter {

    public DefaultableChoppingToolsAdapter andTagger(Tagger tagger) {
      mTagger = tagger;
      return this;
    }

    public DefaultableChoppingToolsAdapter andFormatter(Formatter formatter) {
      mFormatter = formatter;
      return this;
    }

    public final void byDefault() {
      ChopInternals.sDefaultTagger = mTagger;
      ChopInternals.sDefaultFormatter = mFormatter;
    }
  }
}
