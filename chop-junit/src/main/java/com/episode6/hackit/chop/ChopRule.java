package com.episode6.hackit.chop;

import com.episode6.hackit.chop.tree.StdOutDebugTree;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A {@link TestRule} that plants a given {@link Chop.Tree} before executing
 * a test and digs it up afterwards.
 */
public class ChopRule implements TestRule {

  private final Chop.Tree mTree;

  /**
   * Create a new ChopRule using a {@link StdOutDebugTree}
   */
  public ChopRule() {
    this(new StdOutDebugTree());
  }

  /**
   * Create a new ChopRule that plants the given tree before executing
   * a test and digs it up afterwards.
   * @param tree The tree to plant.
   */
  public ChopRule(Chop.Tree tree) {
    mTree = tree;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        Chop.plantTree(mTree);
        base.evaluate();
        ChopInternals.TREE_FARM.digUpTree(mTree);
      }
    };
  }
}
