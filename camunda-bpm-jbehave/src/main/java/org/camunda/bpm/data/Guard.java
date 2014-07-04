package org.camunda.bpm.data;

import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * Guard interface.
 * 
 * @author Simon Zambrovski, holisticon AG.
 */
public interface Guard {
  /**
   * Checks of pre-conditions.
   * 
   * @param delegateExecution
   *          current execution.
   * @throws IllegalStateException
   *           thrown if pre-condition is not met.
   */
  void checkPreconditions(final DelegateExecution delegateExecution) throws IllegalStateException;

  /**
   * Checks of post-conditions.
   * 
   * @param delegateExecution
   *          current execution.
   * @throws IllegalStateException
   *           thrown if post-condition is not met.
   */
  void checkPostconditions(final DelegateExecution delegateExecution) throws IllegalStateException;
}