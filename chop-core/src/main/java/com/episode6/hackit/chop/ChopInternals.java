package com.episode6.hackit.chop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

final class ChopInternals {

  static final ThreadLocal<Chop.ChoppingToolsAdapter> TOOLS_ADAPTER =
      new ThreadLocal<Chop.ChoppingToolsAdapter>() {

        @Override
        protected Chop.ChoppingToolsAdapter initialValue() {
          return new Chop.ChoppingToolsAdapter();
        }
      };

  static final ThreadLocal<Chop.DefaultableChoppingToolsAdapter> DEFAULTABLE_TOOLS_ADAPTER =
      new ThreadLocal<Chop.DefaultableChoppingToolsAdapter>() {

        @Override
        protected Chop.DefaultableChoppingToolsAdapter initialValue() {
          return new Chop.DefaultableChoppingToolsAdapter();
        }
      };


  static final ThreadLocal<Chop.SettableTagger> STRING_TAGGER =
          new ThreadLocal<Chop.SettableTagger>() {

            @Override
            protected Chop.SettableTagger initialValue() {
              return new Chop.SettableTagger();
            }
          };

  static final TreeFarm TREE_FARM = new TreeFarm();

  static Chop.Tagger sDefaultTagger = Chop.Defaults.TAGGER;
  static Chop.Formatter sDefaultFormatter = Chop.Defaults.FORMATTER;

  static void chopLogs(
      Chop.Level level,
      Chop.Tagger tagger,
      Chop.Formatter formatter,
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

  /**
   * The TreeFarm is where the {@link Chop.Tree}s are planted. It also keeps track of which
   * {@link Chop.Level}s are supported by the planted trees
   */
  static final class TreeFarm {

    private final Set<Chop.Tree> mTrees;
    private final Map<Chop.Level, Boolean> mSupportedLevelMap;

    TreeFarm() {
      mTrees = new HashSet<Chop.Tree>();
      mSupportedLevelMap = new HashMap<Chop.Level, Boolean>();

      resetLevelMap();
    }

    boolean plantTree(Chop.Tree tree) {
      if (mTrees.add(tree)) {
        for (Chop.Level level : Chop.Level.values()) {
          mSupportedLevelMap.put(level, tree.supportsLevel(level) || isLogLevelSupported(level));
        }
        return true;
      }
      return false;
    }

    boolean isLogLevelSupported(Chop.Level level) {
      return mSupportedLevelMap.get(level);
    }

    void chopLogs(Chop.Level level, String tag, String message) {
      for (Chop.Tree tree : mTrees) {
        if (tree.supportsLevel(level)) {
          tree.chopLog(level, tag, message);
        }
      }
    }

    /**
     * Visible for testing
     */
    boolean isTreePlanted(Chop.Tree tree) {
      return mTrees.contains(tree);
    }

    /**
     * Visible for testing
     */
    void digUpTree(Chop.Tree tree) {
      if (mTrees.remove(tree)) {
        resetLevelMap();
      }
    }

    private void resetLevelMap() {
      for (Chop.Level level : Chop.Level.values()) {
        mSupportedLevelMap.put(level, askAllTreesIsLevelSupported(level));
      }
    }

    private boolean askAllTreesIsLevelSupported(Chop.Level level) {
      for (Chop.Tree tree : mTrees) {
        if (tree.supportsLevel(level)) {
          return true;
        }
      }
      return false;
    }
  }
}
