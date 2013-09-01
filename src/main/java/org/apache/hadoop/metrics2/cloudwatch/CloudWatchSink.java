package org.apache.hadoop.metrics2.cloudwatch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.hadoop.metrics.MetricsException;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsSink;
import org.apache.hadoop.metrics2.MetricsTag;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.google.common.collect.Lists;

public class CloudWatchSink implements MetricsSink {

    private AmazonCloudWatchClient _client;
    private String _namespace;
    private int _batch;

    private final List<MetricDatum> _metrics = new ArrayList<MetricDatum>();

    @Override
    public void init(SubsetConfiguration conf) {
        String accessKey = conf.getString("accesskey");
        String secretKey = conf.getString("secretkey");
        String region = conf.getString("region");
        String namespace = conf.getString("namespace");
        String batch = conf.getString("batch");

        try {
            _namespace = (null == namespace ? "Custom" : namespace);
            _batch = (null == batch ? 5 : Integer.valueOf(batch));

            AWSCredentials awsCredentials = new BasicAWSCredentials(
                    accessKey, secretKey);
            _client = new AmazonCloudWatchClient(awsCredentials);
            _client.setRegion(Region.getRegion(Regions.fromName(region)));
        } catch (Exception e) {
            throw new MetricsException(e.getMessage());
        }
    }

    @Override
    public void flush() {
        for (List<MetricDatum> partition : Lists.partition(_metrics,
                _batch)) {
            PutMetricDataRequest req = new PutMetricDataRequest()
                    .withNamespace(_namespace).withMetricData(partition);
            _client.putMetricData(req);
        }
        _metrics.clear();
    }

    @Override
    public void putMetrics(MetricsRecord record) {
        String hostName = "unknown";
        for (MetricsTag tag : record.tags()) {
            if (tag.name().equals("Hostname")) {
                hostName = tag.value();
            }
        }
        String context = (null == record.context() ? "unknown" : record
                .context());
        String group = (null == record.name() ? "unknown" : record.name());
        
        Date timestamp = new Date(record.timestamp());

        Dimension dimHost = new Dimension().withName("Hostname")
                .withValue(hostName);
        Dimension dimCtx = new Dimension().withName("Context").withValue(
                context);
        Dimension dimGroup = new Dimension().withName("Group").withValue(
                group);

        for (AbstractMetric metric : record.metrics()) {
            Dimension dimType = new Dimension().withName("Type")
                    .withValue(metric.type().name());
            List<Dimension> dimensions = Arrays.asList(dimHost, dimCtx,
                    dimGroup, dimType);
            MetricDatum datum = new MetricDatum()
                    .withDimensions(dimensions)
                    .withMetricName(metric.name())
                    .withTimestamp(timestamp)
                    .withValue(metric.value().doubleValue());
            _metrics.add(datum);
        }
    }
}