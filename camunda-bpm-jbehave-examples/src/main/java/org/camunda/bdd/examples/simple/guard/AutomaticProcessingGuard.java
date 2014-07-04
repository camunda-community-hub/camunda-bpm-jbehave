package org.camunda.bdd.examples.simple.guard;

import org.camunda.bpm.data.ActivityGuard;
import static org.camunda.bpm.data.Guards.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants.Variables;

/**
 * Guard of the isAutomatic variable.
 */
public class AutomaticProcessingGuard extends ActivityGuard {

  private static final long serialVersionUID = 1L;

  @Override
  public void checkPostconditions(final DelegateExecution execution) throws IllegalStateException {
    checkIsSet(execution, Variables.IS_AUTOMATIC);
    checkIsSet(execution, Variables.CONTRACT_ID);
  }
}
