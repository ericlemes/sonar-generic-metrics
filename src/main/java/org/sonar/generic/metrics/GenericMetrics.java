/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.utils.log.Logger;

public class GenericMetrics implements Metrics {

  private final Logger LOG = LoggerWithDebugCheck.get(GenericMetrics.class);

  public static final String JSON_DATA_PROPERTY = "sonar.generic.metrics.jsondata";

  public static final String CONFIG_ENV_VARIABLE = "SONAR_GENERIC_METRICS_JSONDATA";

  private Configuration config;

  public static List<Metric> metrics;

  private FileReader fileReader;

  private Environment environment;

  public GenericMetrics(Configuration config){
    this.config = config;
    this.fileReader = new FileReaderImpl();
    this.environment = new EnvironmentImpl();
  }

  public void setFileReader(FileReader reader){
    this.fileReader = reader;
  }

  public void setEnvironment(Environment environment){
    this.environment = environment;
  }

  @Override
  public List<Metric> getMetrics() {
    LOG.debug("getMetrics");

    if (GenericMetrics.metrics != null)
      return GenericMetrics.metrics;

    Optional<String> configurationJson = config.get(JSON_DATA_PROPERTY);
    if (!configurationJson.isPresent()){
      LOG.warn(JSON_DATA_PROPERTY + " property is empty.");
      return new ArrayList<>();
    }
    metrics = parseJson(fileReader.readFile(configurationJson.get()));
    return metrics;
  }

  public static List<Metric> getAllMetrics(Environment environment, FileReader fileReader) {
    if (metrics != null)
      return GenericMetrics.metrics;

    String environmentVariable = environment.getEnvironmentVariable(CONFIG_ENV_VARIABLE);

    if (environmentVariable == null)
      return null;
    if (environmentVariable.isEmpty())
      return null;

    metrics = parseJson(fileReader.readFile(environmentVariable));

    return metrics;
  }

  private static List<Metric> parseJson(String jsonString){
    List<Metric> result = new ArrayList<>();
    JSONObject jsonData = new JSONObject(jsonString);
    JSONArray metricsJson = jsonData.getJSONArray("metrics");
    for (int i = 0; i < metricsJson.length(); i++){
      JSONObject metric = metricsJson.getJSONObject(i);
      result.add(parseMetric(metric));
    }
    return result;
  }

  private static Metric parseMetric(JSONObject jsonObject){
    Metric metric =
      new Metric.Builder(
        jsonObject.getString("key"), jsonObject.getString("name"), Metric.ValueType.valueOf(jsonObject.getString("type")))
        .setDescription(jsonObject.getString("description"))
        .setDirection(jsonObject.getInt("direction"))
        .setQualitative(jsonObject.getBoolean("qualitative"))
        .setDomain(jsonObject.getString("domain"))
        .create();

    return metric;
  }

  public static Metric getMetric(String metricKey){
    if (GenericMetrics.metrics == null)
      return null;

    for(Metric m : GenericMetrics.metrics){
      if (m.key().equals(metricKey))
        return m;
    }
    return null;
  }
}
