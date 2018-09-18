/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import org.mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.LoggerLevel;

public class TestLoggerWithDebugCheck {

  private LoggerWithDebugCheck logger;

  @Mock
  private Logger internalLogger;

  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    this.logger = new LoggerWithDebugCheck(this.internalLogger);
  }

  @Test
  public void testWhenCreatingWithStandardConstructorShouldNotThrow(){
    new LoggerWithDebugCheck(TestLoggerWithDebugCheck.class);
  }

  @Test
  public void testWhenCallingIsTraceEnabledShouldInvokeInternalLogger(){
    this.logger.isTraceEnabled();
    verify(this.internalLogger).isTraceEnabled();
  }

  @Test
  public void testWhenCallingTraceShouldInvokeInternalLogger(){
    this.logger.trace("testString");
    verify(this.internalLogger).trace("testString");

    this.logger.trace("testString", 1);
    verify(this.internalLogger).trace("testString", 1);

    this.logger.trace("testString", 1, 2);
    verify(this.internalLogger).trace("testString", 1, 2);

    this.logger.trace("testString", 1, 2, 3, 4);
    verify(this.internalLogger).trace("testString", 1, 2, 3, 4);
  }

  @Test
  public void testWhenCallingIsDebugEnabledShouldInvokeInternalLogger(){
    this.logger.isDebugEnabled();
    verify(this.internalLogger).isDebugEnabled();
  }

  @Test
  public void testWhenCallingDebugAndDebugEnabledShouldInvokeInternalLogger(){
    when(this.internalLogger.isDebugEnabled()).thenReturn(true);

    this.logger.debug("testString");
    verify(this.internalLogger).debug("testString");

    this.logger.debug("testString", 1);
    verify(this.internalLogger).debug("testString", 1);

    this.logger.debug("testString", 1, 2);
    verify(this.internalLogger).debug("testString", 1, 2);

    this.logger.debug("testString", 1, 2, 3);
    verify(this.internalLogger).debug("testString", 1, 2, 3);
  }

  @Test
  public void testWhenCallingDebugAndDebugDisabledShouldInvokeInternalLogger(){
    when(this.internalLogger.isDebugEnabled()).thenReturn(false);

    this.logger.debug("testString");
    verify(this.internalLogger, times(0)).debug("testString");

    this.logger.debug("testString", 1);
    verify(this.internalLogger, times(0)).debug("testString", 1);

    this.logger.debug("testString", 1, 2);
    verify(this.internalLogger, times(0)).debug("testString", 1, 2);

    this.logger.debug("testString", 1, 2, 3);
    verify(this.internalLogger, times(0)).debug("testString", 1, 2, 3);
  }

  @Test
  public void testWhenCallingInfoShouldInvokeInternalLogger(){
    this.logger.info("testString");
    verify(this.internalLogger).info("testString");

    this.logger.info("testString", 1);
    verify(this.internalLogger).info("testString", 1);

    this.logger.info("testString", 1, 2);
    verify(this.internalLogger).info("testString", 1, 2);

    this.logger.info("testString", 1, 2, 3, 4);
    verify(this.internalLogger).info("testString", 1, 2, 3, 4);
  }

  @Test
  public void testWhenCallingWarnShouldInvokeInternalLogger(){
    this.logger.warn("testString");
    verify(this.internalLogger).warn("testString");

    this.logger.warn("testString", 1);
    verify(this.internalLogger).warn("testString", 1);

    this.logger.warn("testString", 1, 2);
    verify(this.internalLogger).warn("testString", 1, 2);

    Exception e = new Exception();
    this.logger.warn("testString", e);
    verify(this.internalLogger).warn("testString", e);

    this.logger.warn("testString", 1, 2, 3, 4);
    verify(this.internalLogger).warn("testString", 1, 2, 3, 4);
  }

  @Test
  public void testWhenCallingErrorShouldInvokeInternalLogger(){
    this.logger.error("testString");
    verify(this.internalLogger).error("testString");

    this.logger.error("testString", 1);
    verify(this.internalLogger).error("testString", 1);

    this.logger.error("testString", 1, 2);
    verify(this.internalLogger).error("testString", 1, 2);

    Exception e = new Exception();
    this.logger.error("testString", e);
    verify(this.internalLogger).error("testString", e);

    this.logger.error("testString", 1, 2, 3, 4);
    verify(this.internalLogger).error("testString", 1, 2, 3, 4);
  }

  @Test
  public void testWhenCallingSetLevelShouldInvokeInternalLogger(){
    this.logger.setLevel(LoggerLevel.INFO);
    verify(this.internalLogger).setLevel(LoggerLevel.INFO);
  }

  @Test
  public void testWhenCallingGetLevelShouldInvokeInternalLogger(){
    this.logger.getLevel();
    verify(this.internalLogger).getLevel();
  }

  @Test
  public void whenCallingGetShouldReturnInstance(){
    assertNotNull(LoggerWithDebugCheck.get(TestLoggerWithDebugCheck.class));
  }

}
