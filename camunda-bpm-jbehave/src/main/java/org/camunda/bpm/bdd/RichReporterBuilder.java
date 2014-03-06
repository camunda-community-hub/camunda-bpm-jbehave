package org.camunda.bpm.bdd;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.XML;

import java.util.Properties;

import org.jbehave.core.reporters.StoryReporterBuilder;

/**
 * Report builder.
 */
public class RichReporterBuilder extends StoryReporterBuilder {

  /**
   * Constructs the builder.
   */
  public RichReporterBuilder() {
    withDefaultFormats() //
        .withViewResources(getViewResources()) //
        .withFormats(CONSOLE, HTML, XML) //
        .withFailureTrace(JBehaveConstants.REPORT_FAILURE_TRACE) //
        .withFailureTraceCompression(JBehaveConstants.COMPRESS_FAILURE_TRACE);
  }

  /**
   * Retrieves the configuration of the view.
   */
  final static Properties getViewResources() {
    final Properties viewResources = new Properties();
    viewResources.put("decorateNonHtml", "false");
    return viewResources;
  }

}