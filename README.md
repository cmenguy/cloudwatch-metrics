CloudWatchMetrics
=================

Facilities for automatically sending Hadoop metrics to Amazon CloudWatch, using the metrics and metrics2 interfaces.
There are 2 different metrics handlers:

 - `CloudWatchContext`: send metrics using the old metrics interface.
 - `CloudWatchSink`: send metrics using the new metrics2 interface.

Once sent, metrics will be available in the CloudWatch UI after a few minutes.

Options
-------

The following options are available in both interfaces:

 - accesskey: Your personal AWS access key.
 - secretkey: Your personal AWS secret key.
 - region: The AWS region ID.
 - batch: The maximum number of metrics to send in a single AWS request.

Setup
-----

To create a jar, run:

    mvn package

CloudWatchContext
-----------------

To enable metrics, update **hadoop-metrics.properties** to use `CloudWatchContext`, for example:

CloudWatchSink
--------------

To enable metrics, update **hadoop-metrics2.properties** to use `CloudWatchSink`, for example:

    *.sink.cloudwatch.class=com.wiley.ptl.hadoop.samples.chapter16.CloudWatchSink
    *.period=10
    namenode.sink.cloudwatch.accesskey=[accessKey]
    namenode.sink.cloudwatch.secretkey=[secretKey]
    namenode.sink.cloudwatch.region=us-east-1
    namenode.sink.cloudwatch.namespace=Example
