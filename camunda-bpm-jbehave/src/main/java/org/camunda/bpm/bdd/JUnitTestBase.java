package org.camunda.bpm.bdd;

import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;
import static org.needle4j.injection.InjectionProviders.providerForInstance;
import static org.needle4j.injection.InjectionProviders.providersToSet;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.test.CamundaSupport;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.junit.needle.NeedleAnnotatedPathRunner;
import org.jbehave.core.reporters.PrintStreamStepdocReporter;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.PrintStreamStepMonitor;
import org.jbehave.core.steps.needle.NeedleStepsFactory;
import org.junit.runner.RunWith;
import org.needle4j.injection.InjectionProvider;

import com.google.common.collect.Sets;
import org.needle4j.injection.InjectionProviders;

@RunWith(NeedleAnnotatedPathRunner.class)
public abstract class JUnitTestBase extends JUnitStories {
  
  static {
    Slf4jLoggerRule.DEFAULT.before();
  }

  @Override
  public Configuration configuration() {

    final Configuration configuration = new MostUsefulConfiguration();
    configuration.useStoryReporterBuilder(new RichReporterBuilder().withCodeLocation(CodeLocations.codeLocationFromClass(getClass())));
    configuration.useStepMonitor(new PrintStreamStepMonitor());
    configuration.usePendingStepStrategy(new FailingUponPendingStep());
    configuration.useStepdocReporter(new PrintStreamStepdocReporter());

    return configuration;
  }

  protected URL getStoryLocation() {
    return codeLocationFromPath(JBehaveConstants.DEFAULT_STORY_LOCATION);
  }

  @Override
  public InjectableStepsFactory stepsFactory() {
    return new NeedleStepsFactory(configuration(), getInjectionProviders(), getStepClasses());
  }

  @Override
  public List<String> storyPaths() {
    return new StoryFinder().findPaths(getStoryLocation(), JBehaveConstants.STORY_PATTERN, JBehaveConstants.NO_EXCLUDE);
  }

  public abstract Class<?>[] getStepClasses();

  /**
   * Retrieves the list of injection providers.
   * 
   * @return list of injection providers, including the one for Camunda support.
   */
  public Set<InjectionProvider<?>> getInjectionProviders() {
    return providersToSet(new CamundaSupport());
  }

}
