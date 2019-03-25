/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

public class EnvironmentImpl implements Environment {

  @Override
  public String getEnvironmentVariable(String variableName) {
    return System.getenv(variableName);
  }
}
