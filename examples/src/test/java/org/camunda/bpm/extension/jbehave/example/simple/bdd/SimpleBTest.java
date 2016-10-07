package org.camunda.bpm.extension.jbehave.example.simple.bdd;

import java.net.URL;
import java.util.List;

import org.camunda.bpm.bdd.JBehaveTestBase;
import org.camunda.bpm.bdd.steps.CamundaSteps;
import org.camunda.bpm.extension.jbehave.example.simple.steps.SimpleProcessSteps;
import org.camunda.bpm.test.CamundaSupport;
import org.jbehave.core.annotations.UsingSteps;
import org.jbehave.core.annotations.needle.UsingNeedle;

/**
 * JBehave Tests for processes.
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
