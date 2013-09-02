CloudWatchMetrics
=================

Facilities for automatically sending Hadoop metrics to Amazon CloudWatch, using the metrics and metrics2 interfaces.
There are 2 different metrics handlers:

 - `CloudWatchContext`: send metrics using the old metrics interface.
 - `CloudWatchSink`: send metrics using the new metrics2 interface.

Once sent, metrics will be available in the CloudWatch UI after a few minutes.

Dependencies
------------

 - AWS SDK >= 1.5.3
 - Guava >= 14.0.1
 - CDH 4.3.1

Options
-------

The following options are available in both interfaces:

 - accesskey: Your personal AWS access key.
 - secretkey: Your personal AWS secret key.
 - region: The AWS region ID.
 - namespace: The CloudWatch namespace. Defaults to "Custom".
 - batch: The maximum number of metrics to send in a single AWS request. Defaults to 5.

Setup
-----

To package the classes and dependencies into a single jar (except the Hadoop classes), run:

    mvn package

Update your **hadoop-env.sh** file and include the following:

    export HADOOP_CLASSPATH="/path/to/cloudwatch-metrics-[version].jar"

Restart your services for this change to take effect.

CloudWatchContext
-----------------

To enable metrics, update **hadoop-metrics.properties** to use `CloudWatchContext`, for example:

    namenode.class=org.apache.hadoop.metrics.cloudwatch.CloudWatchContext
    namenode.period=10
    namenode.accesskey=[accessKey]
    namenode.secretkey=[secretKey]
    namenode.region=us-east-1

CloudWatchSink
--------------

To enable metrics, update **hadoop-metrics2.properties** to use `CloudWatchSink`, for example:

    *.sink.cloudwatch.class=org.apache.hadoop.metrics2.cloudwatch.CloudWatchSink
    *.period=10
    namenode.sink.cloudwatch.accesskey=[accessKey]
    namenode.sink.cloudwatch.secretkey=[secretKey]
    namenode.sink.cloudwatch.region=us-east-1
    namenode.sink.cloudwatch.namespace=Example
