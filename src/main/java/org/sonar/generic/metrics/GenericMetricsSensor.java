/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

import java.io.File;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;

public class GenericMetricsSensor implements Sensor {

  private final Logger LOG = LoggerWithDebugCheck.get(GenericMetricsSensor.class);

  private Configuration config;

  private FileReader fileReader;

  public GenericMetricsSensor(Configuration config){
    this.config = config;
    this.fileReader = new FileReaderImpl();
  }

  public void setFileReader(FileReader fileReader){
    this.fileReader = fileReader;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .name("Generic Metrics Sensor")
      .onlyOnFileType(InputFile.Type.MAIN);
  }

  @Override
  public void execute(SensorContext context) {
    LOG.debug("Execute GenericMetricsSensor");

    Optional<String> fileName = config.get(GenericMetrics.JSON_DATA_PROPERTY);
    if (!fileName.isPresent()){
      LOG.debug("No json data");
      return;
    }

    String json = fileReader.readFile(fileName.get());
    JSONObject rootObject = new JSONObject(json);

    JSONArray measures = rootObject.getJSONArray("measures");
    for (int i = 0; i < measures.length(); i++){
      JSONObject measure = measures.getJSONObject(i);
      processMeasure(context, measure);
    }
  }

  private void processMeasure(SensorContext context, JSONObject measure){
    String fileName = measure.getString("file");
    FilePredicate predicate = context.fileSystem().predicates().is(new File(fileName));

    String metricKey = measure.getString("metric-key");
    Metric m = GenericMetrics.getMetric(metricKey);
    if (m == null){
      LOG.error("Could not find metric key: " + metricKey);
      return;
    }

    InputComponent file = context.fileSystem().inputFile(predicate);
    if (file == null){
      LOG.warn(fileName + " not found during scan");
      return;
    }

    Object value = measure.get("value");
    if (m.getType() == Metric.ValueType.INT)
      context.newMeasure().forMetric(m).on(file).withValue((int)value).save();
    if (m.getType() == Metric.ValueType.FLOAT)
      context.newMeasure().forMetric(m).on(file).withValue((double)value).save();
    else
      LOG.error("Unsupported type " + m.getType().name());
  }

}
