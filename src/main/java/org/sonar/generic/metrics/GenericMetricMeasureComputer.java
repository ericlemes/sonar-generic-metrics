/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;

public class GenericMetricMeasureComputer implements MeasureComputer {

  private Metric metric;
  private final Logger LOG = LoggerWithDebugCheck.get(GenericMetricMeasureComputer.class);

  public GenericMetricMeasureComputer(Metric metric){
    this.metric = metric;
  }

  @Override
  public MeasureComputerDefinition define(MeasureComputerDefinitionContext context) {
    return context.newDefinitionBuilder().setOutputMetrics(metric.key()).build();
  }

  private int intSum;
  private double doubleSum;

  @Override
  public void compute(MeasureComputerContext context) {
    LOG.debug("Computing measures for " + metric.key());
    if (context.getComponent().getType() == Component.Type.PROJECT){
      addProjectMeasure(context);
    }
    else if (context.getComponent().getType() == Component.Type.FILE){
      sumFileMeasure(context, context.getMeasure(metric.key()));
    }
  }

  private void addProjectMeasure(MeasureComputerContext context){
    switch (metric.getType()) {
    case INT:
      context.addMeasure(metric.key(), intSum);
      break;
    case FLOAT:
      context.addMeasure(metric.key(), doubleSum);
      break;
    default:
      LOG.warn("Type not expected: " + metric.valueType().toString());
      break;
    }
  }

  private void sumFileMeasure(MeasureComputerContext context, Measure m) {
    if (m == null)
      return;

    switch (metric.getType()) {
      case INT:
        intSum += m.getIntValue();
        break;
      case FLOAT:
        doubleSum += m.getDoubleValue();
        break;
      default:
        LOG.warn("Type not expected: " + metric.valueType().toString());
        break;
    }
  }
}
