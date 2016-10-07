package org.camunda.bpm.extension.jbehave.example.simple.unit;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.withVariables;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

import java.util.UUID;

import javax.inject.Inject;

import org.camunda.bpm.bdd.Slf4jLoggerRule;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessAdapter;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants;
import org.camunda.bpm.extension.needle.ProcessEngineNeedleRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit test of simple process.
 * 
 * @author Simon Zambrovski, Holisticon AG.
 */
public class SimpleUnitTest {

  static {
    Slf4jLoggerRule.DEFAULT.before();
  }

  @Rule
  public ProcessEngineNeedleRule processEngine = ProcessEngineNeedleRule.fluentNeedleRule(this).build();

  @Inject
  private SimpleProcessAdapter simpleProcessAdapter;

  private ProcessInstance processInstance;

  public static void mockLoadContract(final SimpleProcessAdapter mock, final boolean isAutomatically) {
    doAnswer(new Answer<Boolean>() {

      @Override
      public Boolean answer(final InvocationOnMock invocation) throws Throwable {
        final DelegateExecution execution = (DelegateExecution) invocation.getArguments()[0];

        // set contract id to random uuid
        execution.setVariable(SimpleProcessConstants.Variables.CONTRACT_ID, UUID.randomUUID().toString());

        return isAutomatically;
      }
    }).when(mock).loadContractData(any(DelegateExecution.class));

  }

  /**
   * Glue code in order to separate all application control code from test
   * assertions.
   */
  class Glue {

    public void loadContractData(final boolean isAutomatically) {
      mockLoadContract(simpleProcessAdapter, isAutomatically);
    }

    public void startSimpleProcess() {
      processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(SimpleProcessConstants.PROCESS);
      assertThat(processInstance).isNotNull();
    }

    public void processAutomatically(final boolean withErrors) {
      if (withErrors) {
        doThrow(new BpmnError(SimpleProcessConstants.Events.ERROR_PROCESS_AUTOMATICALLY_FAILED)).when(simpleProcessAdapter).processContract();
      }
    }

    /**
     * Assert process end event.
     * 
     * @param name
     *          name of the end event.
     */
    private void assertEndEvent(final String name) {
      assertThat(processInstance).hasPassed(name).isEnded();
    }

    public void waitsInManualProcessing() {
      assertThat(processInstance).isWaitingAt(SimpleProcessConstants.Elements.TASK_PROCESS_MANUALLY);
    }

    /**
     * Process manually.
     * 
     * @param withErrors
     */
    public void processManually(boolean withErrors) {
      final Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
      complete(task, withVariables("processingErrorsPresent", Boolean.valueOf(withErrors)));
    }

  }

  private final Glue glue = new Glue();

  @Before
  public void initMocks() {
    Mocks.register(SimpleProcessAdapter.NAME, simpleProcessAdapter);
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldDeploy() {
    // nothing to do.
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldStartAndWaitForManual() {

    // given
    glue.loadContractData(false);

    // when
    glue.startSimpleProcess();

    // then
    glue.waitsInManualProcessing();
  }

  @Test
  @Ignore
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldStartProcessAutomaticallyAndWaitForManual() {

    // given
    glue.loadContractData(true);
    glue.processAutomatically(true);

    // when
    glue.startSimpleProcess();

    // then
    glue.waitsInManualProcessing();
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldStartAndRunAutomatically() {

    // given
    glue.loadContractData(true);
    glue.processAutomatically(false);

    // when
    glue.startSimpleProcess();

    // then
    glue.assertEndEvent(SimpleProcessConstants.Events.EVENT_CONTRACT_PROCESSED);
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldProcessContractManuallySuccessfully() {
    // given
    glue.loadContractData(false);
    glue.startSimpleProcess();
    glue.waitsInManualProcessing();

    // when
    glue.processManually(false);

    // then
    glue.assertEndEvent(SimpleProcessConstants.Events.EVENT_CONTRACT_PROCESSED);
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldProcessContractManuallyAndCancel() {
    // given
    glue.loadContractData(false);
    glue.startSimpleProcess();
    glue.waitsInManualProcessing();

    // when
    glue.processManually(true);

    // then
    glue.assertEndEvent(SimpleProcessConstants.Events.EVENT_CONTRACT_PROCESSING_CANCELLED);
  }

}
