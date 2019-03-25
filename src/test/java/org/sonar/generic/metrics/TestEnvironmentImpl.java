/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestEnvironmentImpl {

  @Test
  public void whenGettingExistingVariableShouldReturnValue(){
    EnvironmentImpl environment = new EnvironmentImpl();
    assertNotNull(environment.getEnvironmentVariable("PATH"));
  }
}
