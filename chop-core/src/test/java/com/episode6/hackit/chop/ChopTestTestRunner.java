package com.episode6.hackit.chop;

import com.episode6.hackit.chop.tree.StdOutTree;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

public class ChopTestTestRunner extends BlockJUnit4ClassRunner {

  public ChopTestTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
    Chop.plantTree(new StdOutTree());
  }

  @Override
  protected Object createTest() throws Exception {
    Object test = super.createTest();
    MockitoAnnotations.initMocks(test);
    return test;
  }
}
