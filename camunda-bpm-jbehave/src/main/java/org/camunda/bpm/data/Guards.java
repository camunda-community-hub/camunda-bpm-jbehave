package org.camunda.bpm.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public final class Guards {
  public static final String VARIABLE_SKIP_GUARDS = "runtimeSkipGuards";
  public static final Map<String, Object> MAP_SKIP_GUARDS = new HashMap<String, Object>();

  static final String VARIABLE_NAME_MUST_BE_NOT_NULL = "Variable name must be not null";
  static final String CONDITION_VIOLATED = "Condition of task '%s' is violated: ";
  private static final Predicate<String> isPrecondition = Predicates.or(Predicates.equalTo("start"), Predicates.equalTo("create"));
  private static final Predicate<String> isPostcondition = Predicates.or(Predicates.equalTo("end"), Predicates.equalTo("complete"));

  public static void dispatch(Guard guard, String eventName, DelegateExecution execution) {
    if (skipGuards(execution)) {
      return;
    }

    if (isPrecondition.apply(eventName))
      guard.checkPreconditions(execution);
    else if (isPostcondition.apply(eventName))
      guard.checkPostconditions(execution);
  }

  public static boolean skipGuards(final DelegateExecution execution) {
    return BooleanUtils.toBoolean((Boolean) execution.getVariable(VARIABLE_SKIP_GUARDS));
  }

  public static void checkIsSetLocal(final DelegateExecution execution, final String variableName) {
    Preconditions.checkArgument(variableName != null, VARIABLE_NAME_MUST_BE_NOT_NULL);

    final Object variableLocal = execution.getVariableLocal(variableName);
    Preconditions.checkState(variableLocal != null,
        String.format("Condition of task '%s' is violated: Local variable '%s' is not set.", new Object[] { execution.getCurrentActivityId(), variableName }));
  }

  public static void checkIsSet(final DelegateExecution execution, final String variableName) {
    Preconditions.checkArgument(variableName != null, "Variable name must be not null");

    final Object variableLocal = execution.getVariableLocal(variableName);
    final Object variable = execution.getVariable(variableName);

    Preconditions.checkState((variableLocal != null) || (variable != null),
        String.format("Condition of task '%s' is violated: Variable '%s' is not set.", new Object[] { execution.getCurrentActivityId(), variableName }));
  }

  public static void checkIsSetGlobal(final DelegateExecution execution, final String variableName) {
    Preconditions.checkArgument(variableName != null, VARIABLE_SKIP_GUARDS);

    final Object variable = execution.getVariable(variableName);
    Preconditions.checkState(variable != null,
        String.format("Condition of task '%s' is violated: Global variable '%s' is not set.", new Object[] { execution.getCurrentActivityId(), variableName }));
  }
}