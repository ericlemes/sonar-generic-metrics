# Sonar Generic Metrics

SonarQube Plugin to publish any metrics based on a JSON file.

## Status

| | |
|:----|:----|
| **Build** | [![Build Status](https://img.shields.io/appveyor/ci/ericlemes/sonar-generic-metrics.svg)](https://ci.appveyor.com/project/ericlemes/sonar-generic-metrics)|
| **Coverage** | [![Code Coverage](https://codecov.io/gh/ericlemes/sonar-generic-metrics/branch/master/graph/badge.svg)](https://codecov.io/gh/ericlemes/sonar-generic-metrics) |

## How to use

SonarQube has the scanner side and the server side and the setup needs to happen on both sides.

### Server Side (Compute Engine side)

Create a json file with this following structure:

```json
{
  "metrics": [
    {
      "key": "metric1",
      "name": "Metric 1",
      "type": "INT",
      "description": "Description Metric 1",
      "direction": -1,
      "qualitative": false,
      "domain": "Generic Metric"
    },
    {
      "key": "metric2",
      "name": "Metric 2",
      "type": "FLOAT",
      "description": "Description Metric 2",
      "direction": 0,
      "qualitative": false,
      "domain": "Generic Metric"
    }
  ]
}
```

These will be the metrics registered by the server during server startup.
You can register as many as you want, but bear in mind that they will all be registered in one go.
If you have more than one project scanner by SonarQube that uses different metrics, on the server side, they all needs to be together.
For example, if `Project` 1 uses `Metric 1` and `Project 2` uses `Metric 2`, your json file on server side must have all the metrics in a single file.

The metric `key` is very important. This is an unique identifier for your metric and must not clash with any other existing metric.

Next step is to deploy this file on your server and create an environment variable pointing to that file, for example

```properties
SONAR_GENERIC_METRICS_JSONDATA=c:\full\path\to\my\metrics.json
```

The file must be accessible by the user that runs your server process.

### Scanner side

On your scanner, you will have to generate a json file with the data that you would like to publish. For example:

```json
{
  "file-measures": [
    {
      "metric-key": "metric1",
      "file": "test.txt",
      "value": 10
    },
    {
      "metric-key": "metric2",
      "file": "path\\related\\to\\root\\scan\\anotherfile.txt",
      "value": 1.5
    }
  ],
  "project-measures": []
}
```

Then when running our Sonar Scanner, add the following property:

```properties
sonar.generic.metrics.jsondata=path\to\your.json
```

Sonar Generic Metrics will calculate a project level measure for each of your metrics by summing your measures.
If the metric type is `PERCENT` or `RATING`, then simple averaging will be used instead.

This behaviour can be overwritten by adding a measure to `project-measure`, allowing you to pre-define the project level measure. For example:

```json
{
  "file-measures": [
    {
      "metric-key": "metric1",
      "file": "file1.txt",
      "value": 10
    },
    {
      "metric-key": "metric1",
      "file": "file2.text",
      "value": 20
    }
  ],
  "project-measures": [
    {
      "metric-key": "metric1",
      "value": 5
    }
  ]
}
```

### Result

In your SonarQube instance, you will see something similar to:

![screenshot](doc/img/Screenshot1.png "Screenshot 1")

## Limitations

- Not all metrics types are supported.
- The metrics will be displayed in the "Measures" tab of SonarQube. No other kinds of metrics are supported

### Supported Data Types

| Name | Type | Status |
|:-----|:-----|:-------|
| INT | Integer | Supported |
| FLOAT | Double | Supported |
| PERCENT | Double | Supported |
| RATING | Integer | Supported |
| STRING | String | Supported |
| DATA | String | Supported |
| MILLISEC | Long | Not Supported |
| BOOL | Boolean | Not Supported |
| LEVEL | [Metric.Level](http://javadocs.sonarsource.org/latest/apidocs/index.html?org/sonar/api/measures/Metric.Level.html) | Not Supported |
| DISTRIB | String | Not Supported |
| WORK_DUR | Long | Not Supported |
