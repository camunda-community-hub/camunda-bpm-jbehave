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

  public void checkPostconditions(DelegateExecution execution) throws IllegalStateException {
  }

  public void checkPreconditions(DelegateExecution execution) throws IllegalStateException {
  }

  public void notify(DelegateTask delegateTask) {
    Guards.dispatch(this, delegateTask.getEventName(), delegateTask.getExecution());
  }
}
