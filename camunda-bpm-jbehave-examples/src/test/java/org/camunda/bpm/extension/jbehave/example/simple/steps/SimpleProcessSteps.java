package org.camunda.bpm.extension.jbehave.example.simple.steps;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.withVariables;
import static org.camunda.bpm.test.CamundaSupport.parseStatement;
import static org.mockito.Mockito.doThrow;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessAdapter;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants.Elements;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants.Events;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants.Variables;
import org.camunda.bpm.extension.jbehave.example.simple.unit.SimpleUnitTest;
import org.camunda.bpm.test.CamundaSupport;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.mockito.Mockito;



/**
 * Specific process steps.
 * 
 * @author Simon Zambrovski, Holisticon AG.
 */
public class SimpleProcessSteps {

  @Inject
  private SimpleProcessAdapter simpleProcessAdapter;

  @Inject
  private CamundaSupport support;

  @BeforeScenario
  public void initMocks() {
    Mocks.register(SimpleProcessAdapter.NAME, simpleProcessAdapter);
  }

  @AfterScenario
  public void resetMocks() {
    Mockito.reset(simpleProcessAdapter);
  }

  @Given("the contract $verb automatically processible")
  public void loadContractDataAutomatically(final String verb) {
    final boolean processingPossible = parseStatement("not", verb, false);

    SimpleUnitTest.mockLoadContract(simpleProcessAdapter, processingPossible);
  }

  @Given("the contract processing $verb")
  public void processingAutomatically(final String verb) {
    final boolean withErrors = parseStatement("succeeds", verb, false);
    if (withErrors) {
      doThrow(new BpmnError(Events.ERROR_PROCESS_AUTOMATICALLY_FAILED)).when(simpleProcessAdapter).processContract();
    }
  }

  @Then("the contract is loaded")
  public void contractIsLoaded() {
    assertThat(support.getProcessInstance()).hasPassed(Elements.SERVICE_LOAD_CONTRACT_DATA);
  }

  @Then("the contract is processed automatically")
  public void contractIsProcessed() {
    assertThat(support.getProcessInstance()).hasPassed(Elements.SERVICE_PROCESS_CONTRACT_AUTOMATICALLY);
  }

  @Then("the contract processing is cancelled")
  public void cancelledProcessing() {
    assertThat(support.getProcessInstance()).hasPassed(Elements.SERVICE_CANCEL_PROCESSING);
  }

  @When("the contract is processed $withoutErrors")
  public void processManually(final String withoutErrors) {
    final boolean hasErrors = !parseStatement("with errors", withoutErrors, false);

    complete(task(), withVariables(Variables.ARE_PROCESSING_ERRORS_PRESENT, Boolean.valueOf(hasErrors)));
  }
}
