/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

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

  @Override
  public void compute(MeasureComputerContext context) {
    LOG.debug("Computing measures for " + metric.key());
    switch (context.getComponent().getType()){
      case PROJECT:
      case MODULE:
      case DIRECTORY:
        sumChildMeasure(context);
        break;
    }
  }

  private void sumChildMeasure(MeasureComputerContext context){
    switch (metric.getType()) {
    case INT:
      sumIntChildMeasure(context);
      break;
    case FLOAT:
      sumDoubleChildMeasure(context);
      break;
    }
  }

  private void sumIntChildMeasure(MeasureComputerContext context){
    int sum = 0;
    for(Measure m : context.getChildrenMeasures(metric.key())) {
      sum += m.getIntValue();
    }
    context.addMeasure(metric.key(), sum);
  }

  private void sumDoubleChildMeasure(MeasureComputerContext context){
    double sum = 0;
    for(Measure m : context.getChildrenMeasures(metric.key())) {
      sum += m.getDoubleValue();
    }
    context.addMeasure(metric.key(), sum);
  }
}
