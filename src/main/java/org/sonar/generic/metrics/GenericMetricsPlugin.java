/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.util.List;
import org.sonar.api.Plugin;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;

public class GenericMetricsPlugin implements Plugin {

  private final Logger LOG = LoggerWithDebugCheck.get(GenericMetricsPlugin.class);

  private Environment environment;

  private FileReader fileReader;

  public GenericMetricsPlugin() {
    this.environment = new EnvironmentImpl();
    this.fileReader = new FileReaderImpl();
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void setFileReader(FileReader fileReader) {
    this.fileReader = fileReader;
  }

  @Override
  public void define(Context context) {
    LOG.debug("Registering sonar.generic.metrics");

    context.addExtension(GenericMetrics.class);
    context.addExtension(GenericMetricsSensor.class);

    List<Metric> metrics = GenericMetrics.getAllMetrics(environment, fileReader);
    if (metrics == null) {
      LOG.debug(GenericMetrics.CONFIG_ENV_VARIABLE + " not set. ");
    } else {
      for (Metric m : metrics) {
        LOG.debug("Registering measure computer for metric " + m.key());
        context.addExtension(new GenericMetricMeasureComputer(m));
      }
    }
  }
}
