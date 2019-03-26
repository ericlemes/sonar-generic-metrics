/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import org.apache.commons.lang.NotImplementedException;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;

public class GenericMetricMeasureComputer implements MeasureComputer {

  private Metric metric;
  private final Logger LOG = LoggerWithDebugCheck.get(GenericMetricMeasureComputer.class);

  public GenericMetricMeasureComputer(Metric metric) {
    this.metric = metric;
  }

  @Override
  public MeasureComputerDefinition define(MeasureComputerDefinitionContext context) {
    return context.newDefinitionBuilder().setOutputMetrics(metric.key()).build();
  }

  @Override
  public void compute(MeasureComputerContext context) {
    if (context.getMeasure(metric.key()) != null)
      return;
    LOG.debug("Computing measures for " + metric.key());
    switch (context.getComponent().getType()) {
    case PROJECT:
    case MODULE:
    case DIRECTORY:
      computeChildMeasure(context);
      break;
    default:
      break;
    }
  }

  private void computeChildMeasure(MeasureComputerContext context) {
    System.out.println(metric.getType());
    switch (metric.getType()) {
    case INT:
      computeIntChildMeasure(context);
      break;
    case FLOAT:
      computeDoubleChildMeasure(context);
      break;
    case PERCENT:
      computePercentChildMeasure(context);
      break;
    case RATING:
      computeRatingChildMeasure(context);
      break;
    default:
      System.out.println("Im here");
      throw new NotImplementedException("No compute method for " + metric.getType());
    }
  }

  private void computeIntChildMeasure(MeasureComputerContext context) {
    int sum = 0;
    for (Measure m : context.getChildrenMeasures(metric.key())) {
      sum += m.getIntValue();
    }
    context.addMeasure(metric.key(), sum);
  }

  private void computeDoubleChildMeasure(MeasureComputerContext context) {
    double sum = 0;
    for (Measure m : context.getChildrenMeasures(metric.key())) {
      sum += m.getDoubleValue();
    }
    context.addMeasure(metric.key(), sum);
  }

  private void computePercentChildMeasure(MeasureComputerContext context) {
    double sum = 0;
    double noMeasures = 0;
    for (Measure m : context.getChildrenMeasures(metric.key())) {
      noMeasures++;
      sum += m.getDoubleValue();
    }
    double total = (noMeasures > 0) ? (sum / noMeasures) : 0.0;
    context.addMeasure(metric.key(), total);
  }

  private void computeRatingChildMeasure(MeasureComputerContext context) {
    double sum = 0;
    double noMeasures = 0;
    for (Measure m : context.getChildrenMeasures(metric.key())) {
      noMeasures++;
      sum += m.getIntValue();
    }
    int raiting = (noMeasures > 0) ? (int) Math.round(sum / noMeasures) : 0;
    context.addMeasure(metric.key(), raiting);
  }
}
