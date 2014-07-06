# _Camunda-BPM-JBehave_

_Business Processes are no units and should be tested using behavior specifications. Camunda-BPM-JBehave is set of
libraries allowing to use a famous BDD framework [JBehave](http://jbehave.org/) for testing of processes using Camunda BPM embedded engine._

## Introduction

At the latest after BPMN 2.0 became directly executable by the process engines, the question of tests of business processes is crucial in order to allow high-quality application development. The incredible popularity of JUnit framework leads to the equal treatment of testing with JUnit testing, which does not hold for testing complex behavior. A much better approach to test behavior specifications is provided by Behavior-Driven Development (BDD) frameworks. This projects combines a well-known BDD framework JBehave with some test approaches we developed for testing BPMN 2.0 models.


## Get started
In order to start testing, you need a valid BPMN 2.0 model and at least interfaces of the Java delegates referenced from the process model. The test itself consists of two parts: JBehave stories written in Gherkin and some glue code to control the application. The JBehave part is pretty trivial, you can just use the following snippet in your code:

```java
/**
 * JBehave Tests for the process.
 */
@UsingSteps(instances = { SimpleProcessSteps.class, CamundaSteps.class })
@UsingNeedle(provider = { CamundaSupport.class })
public class SimpleBTest extends JBehaveTestBase {
  @Override
  protected URL getStoryLocation() {
    return this.getClass().getResource("/");
  }

  @Override
  protected List<String> storyPaths() {
    return super.storyPaths();
  }
}
```

Please note, that the `UsingSteps` annotation is listing classes containing the glue steps. 

![Simple Process](/blob/master/camunda-bpm-jbehave-examples/src/main/resources/simple.png)


The story definition looks as following:

```gherkin
Scenario: Automatic processing

Given the process definition simple.bpmn
And the contract is automatically processible
And the contract processing succeeds
When the process simple-process is started
Then the contract is loaded
And the contract is processed automatically
And the process is finished with event event_contract_processed
```

For this story, the following glue code is required, since the rest of the definitions is generic
and is available in `CamundaSteps`.

```java
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
    final ProcessInstance instance = support.getProcessInstance();
    assertThat(instance).hasPassed(Elements.SERVICE_LOAD_CONTRACT_DATA);
  }

  @Then("the contract is processed automatically")
  public void contractIsProcessed() {
    assertThat(support.getProcessInstance()).hasPassed(Elements.SERVICE_PROCESS_CONTRACT_AUTOMATICALLY);
  }
}

```

More details (presentation in German)
_Check this video on BPM testing on [camunda community event Hamburg 2013](http://www.holisticon.de/2013/12/testgetriebene-geschaeftsprozessmodellierung-camunda-community-meeting-bei-holisticon/)_

## Roadmap

- Integrate with better Eclipse runner

## News and Noteworthy
- Switch to new Needle4J and JBehave using it
- JBehave 3.9 integration
- Needle 2.3 integration
- Execution as Maven Build

<a name="resources"/>
## Resources

* [User Guide](./camunda-bpm-jbehave/blob/master/README.md)
* [API Docs](http://camunda.github.io/camunda-bpm-jbehave/apidocs/) 
* [Issue Tracker](https://github.com/camunda/camunda-bpm-jbehave/issues) 
* [Roadmap](https://github.com/camunda/camunda-bpm-jbehave/issues/milestones?state=open&with_issues=no) 
* [Download](https://github.com/camunda/camunda-bpm-jbehave/releases)
* [Continuous Integration](https://plexiti.ci.cloudbees.com/job/camunda-bpm-assert/job/camunda-bpm-assert/)
* [Questions at camunda BPM users list](https://groups.google.com/forum/?fromgroups#!forum/camunda-bpm-users)
* [Feedback at camunda BPM dev list](https://groups.google.com/forum/?fromgroups#!forum/camunda-bpm-dev)


## Maintainer

*  _[Simon Zambrovski](https://github.com/zambrovski)_
*  _[Jan Galinski](https://github.com/galinski)_

## License

Apache License, Version 2.0
