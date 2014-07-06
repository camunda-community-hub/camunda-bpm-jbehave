package org.camunda.bpm.data;

import java.io.Serializable;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * Task guard base. <br />
 * Is intended to be used as a base class for pre- and post-condition guard on
 * user tasks. This class is intended to be subclassed and the subclass should
 * be registered as a task listener for start and end events on the user task.
 * 
 * @author Simon Zambrovski, holisticon AG.
 * 
 */
public class TaskGuard implements TaskListener, Guard, Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Empty implementation of pre-condition checks. <br />
   * Override this method to implement your own. Please throw
   * {@link IllegalStateException} on contract violations.
   */
  public void checkPostconditions(DelegateExecution execution) throws IllegalStateException {
  }

  /**
   * Empty implementation of post-condition checks. <br />
   * Override this method to implement your own. Please throw
   * {@link IllegalStateException} on contract violations.
   */
  public void checkPreconditions(DelegateExecution execution) throws IllegalStateException {
  }

  /**
   * Implementation of the event dispatching.
   * 
   * @param delegateExecution
   *          process execution
   */
  public final void notify(DelegateTask delegateTask) {
    Guards.dispatch(this, delegateTask.getEventName(), delegateTask.getExecution());
  }
}
