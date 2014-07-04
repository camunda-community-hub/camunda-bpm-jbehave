package org.camunda.bpm.extension.jbehave.example.simple.unit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.camunda.bpm.bdd.Slf4jLoggerRule;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.needle.ProcessEngineNeedleRule;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessAdapter;
import org.camunda.bpm.extension.jbehave.example.simple.SimpleProcessConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Deployment unit test of org.camunda.bpm.extension.jbehave.example.simple
 * process.
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

  private final DelegateExecution delegateExecution = mock(DelegateExecution.class);

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

  class Glue {

    private String processInstanceId;

    public void loadContractData(final boolean isAutomatically) {
      mockLoadContract(simpleProcessAdapter, isAutomatically);
    }

    public void startSimpleProcess() {
      processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(SimpleProcessConstants.PROCESS);
      assertNotNull(processInstance);
      processInstanceId = processInstance.getProcessInstanceId();
    }

    public void processAutomatically(final boolean withErrors) {
      if (withErrors) {
        doThrow(new BpmnError(SimpleProcessConstants.Events.ERROR_PROCESS_AUTOMATICALLY_FAILED)).when(simpleProcessAdapter).processContract();
      }
    }

    /**
     * Assert that process execution has run through the activity with given id.
     * 
     * @param name
     *          name of the activity.
     */
    private void assertActivityVisitedOnce(final String name) {

      final HistoricActivityInstance singleResult = processEngine.getHistoryService().createHistoricActivityInstanceQuery().finished().activityId(name)
          .singleResult();
      assertThat("activity '" + name + "' not found!", singleResult, notNullValue());
    }

    /**
     * Assert process end event.
     * 
     * @param name
     *          name of the end event.
     */
    private void assertEndEvent(final String name) {
      assertActivityVisitedOnce(name);
      assertTrue(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult() == null);

    }

    public void waitsInManualProcessing() {
      final Execution execution = processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId)
          .activityId(SimpleProcessConstants.Elements.TASK_PROCESS_MANUALLY).singleResult();
      assertNotNull(execution);
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
  public void shouldStartAndWaitForManual() {

    // given
    glue.loadContractData(false);

    // when
    glue.startSimpleProcess();

    // then
    glue.waitsInManualProcessing();
  }

  @Test
  @Deployment(resources = SimpleProcessConstants.BPMN)
  public void shouldProcessContractManuallySuccessfully() {
    // given
    glue.loadContractData(false);
    glue.startSimpleProcess();
    glue.waitsInManualProcessing();

    // when
    final Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    final Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("processingErrorsPresent", Boolean.TRUE);
    processEngine.getTaskService().complete(task.getId(), variables);

    // then
    assertTrue("Instance not ended",
        processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult() == null);

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

}
