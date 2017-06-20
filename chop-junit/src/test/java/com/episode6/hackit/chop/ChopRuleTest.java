package com.episode6.hackit.chop;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

/**
 * Tests {@link ChopRule}
 */
public class ChopRuleTest {

  private final Chop.Tree mTree = mock(Chop.Tree.class, treeAnswer());
  @Rule public final ChopRule mChopRule = new ChopRule(mTree);

  @Test
  public void testTreePlanted() {
    for (Chop.Level level : Chop.Level.values()) {
      verify(mTree).supportsLevel(level);
    }
  }

  @Test
  public void testSimpleLog() {
    Chop.d("hello");

    verify(mTree).chopLog(eq(Chop.Level.D), anyString(), eq("hello"));
  }

  @Test
  public void testTreeRemoved() throws Throwable {
    Chop.Tree tree = mock(Chop.Tree.class, treeAnswer());
    Statement statement = mock(Statement.class);
    Description description = mock(Description.class);
    ChopRule rule = new ChopRule(tree);
    Statement newStatement = rule.apply(statement, description);

    // creating the statement results in zero interactions
    verifyNoMoreInteractions(tree, statement);

    // evaluate the statement
    newStatement.evaluate();

    // verify tree was added and statement was evaluated
    for (Chop.Level level : Chop.Level.values()) {
      verify(tree).supportsLevel(level);
    }
    verify(statement).evaluate();
    verifyNoMoreInteractions(tree, statement);

    // try to chop a log, we should see no interactions on the tree
    Chop.d("hello");

    verifyNoMoreInteractions(tree);
  }

  private static Answer treeAnswer() {
    return new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        if (invocation.getMethod().getReturnType() == boolean.class) {
          return true;
        }
        return null;
      }
    };
  }
}
