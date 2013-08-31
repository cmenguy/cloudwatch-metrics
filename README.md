CloudWatchMetrics
=================

Facilities for automatically sending Hadoop metrics to Amazon CloudWatch, using the metrics and metrics2 interfaces.
There are 2 different metrics handlers:

 - CloudWatchContext: send metrics using the old metrics interface.
 - CloudWatchSink: send metrics using the new metrics2 interface.

Once sent, metrics will be available in CloudWatch UI after a few minutes.

CloudWatchContext
-----------------

CloudWatchSink
--------------
