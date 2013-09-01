package org.apache.hadoop.metrics.cloudwatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.metrics.ContextFactory;
import org.apache.hadoop.metrics.MetricsException;
import org.apache.hadoop.metrics.spi.AbstractMetricsContext;
import org.apache.hadoop.metrics.spi.OutputRecord;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

public class CloudWatchContext extends AbstractMetricsContext {

    private AmazonCloudWatchClient _client;
    private String _namespace;

    @Override
    public void init(String contextName, ContextFactory factory) {
        super.init(contextName, factory);

        String accessKey = getAttribute("accesskey");
        String secretKey = getAttribute("secretkey");
        String region = getAttribute("region");
        String namespace = getAttribute("namespace");

        try {
            _namespace = (null == namespace ? "Custom" : namespace);

            AWSCredentials awsCredentials = new BasicAWSCredentials(
                    accessKey, secretKey);
            _client = new AmazonCloudWatchClient(awsCredentials);
            _client.setRegion(Region.getRegion(Regions.fromName(region)));
        } catch (Exception e) {
            throw new MetricsException(e.getMessage());
        }
    }

    @Override
    protected void emitRecord(String contextName, String recordName,
            OutputRecord outRec) throws IOException {
        Object hostTag = outRec.getTag("Hostname");
        String hostName = (null == hostTag ? "unknown" : hostTag
                .toString());

        String context = (null == contextName ? "unknown" : contextName);
        String group = (null == recordName ? "unknown" : recordName);

        Date timestamp = new Date();

        Dimension dimHost = new Dimension().withName("Hostname")
                .withValue(hostName);
        Dimension dimCtx = new Dimension().withName("Context").withValue(
                context);
        Dimension dimGroup = new Dimension().withName("Group").withValue(
                group);
        List<Dimension> dimensions = Arrays.asList(dimHost, dimCtx,
                dimGroup);

        for (String metricName : outRec.getMetricNames()) {
            MetricDatum datum = new MetricDatum()
                    .withDimensions(dimensions).withMetricName(metricName)
                    .withTimestamp(timestamp)
                    .withValue(outRec.getMetric(metricName).doubleValue());
            PutMetricDataRequest req = new PutMetricDataRequest()
                    .withNamespace(_namespace).withMetricData(datum);
            _client.putMetricData(req);
        }
    }
}
