package com.episode6.hackit.chop;

public class ChopTestUtil {

  /**
   * Helper method for the {@link com.episode6.hackit.choppingtest.DifferentPackageTest}
   * @param tree
   */
  public static void digUpTree(Chop.Tree tree) {
    ChopInternals.TREE_FARM.digUpTree(tree);
  }
}
