package surePark;

import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

public class AWSUtil {

	private static AWSUtil AWSUTIL;
	private static final String CONFIG_PROP_FILE = "config.properties";

	private AmazonKinesisClient kinesisClient;
	private String endPoint;
	private String serviceName;
	private String regionId;
	private String seed;
	private String stream;
	private String partition;

	private AWSUtil() {
		endPoint = PropertyUtils.getProperty("aws_end_point", CONFIG_PROP_FILE);
		serviceName = PropertyUtils.getProperty("aws_service_name", CONFIG_PROP_FILE);
		regionId = PropertyUtils.getProperty("aws_region_id", CONFIG_PROP_FILE);
		seed = PropertyUtils.getProperty("aws_seed", CONFIG_PROP_FILE);
		stream = PropertyUtils.getProperty("aws_stream_name", CONFIG_PROP_FILE);
		partition = PropertyUtils.getProperty("aws_partition", CONFIG_PROP_FILE);
		kinesisClient = new AmazonKinesisClient();
		kinesisClient.setEndpoint(endPoint, serviceName, regionId);
	}

	public static AWSUtil getInstance() {
		if (AWSUTIL == null) {
			AWSUTIL = new AWSUtil();
			return AWSUTIL;
		} else
			return AWSUTIL;
	}

	public synchronized void sendToKinesis(String data) {

		// Checking if object is created fine
		if (kinesisClient != null)
			System.out.println("\n\nGot kinesis\n\n");

		// Putting data into the stream!
		// ========================================
		String sequenceNumberOfPreviousRecord = seed;
		
		PutRecordRequest putRecordRequest = new PutRecordRequest();
		putRecordRequest.setStreamName(stream);
		putRecordRequest.setData(ByteBuffer.wrap(data.getBytes()));
		putRecordRequest.setPartitionKey(partition);
		putRecordRequest.setSequenceNumberForOrdering(sequenceNumberOfPreviousRecord);
		PutRecordResult putRecordResult = kinesisClient.putRecord(putRecordRequest);
		System.out.println(putRecordResult.toString());
		System.out.print("data: ");
		System.out.print(data);
		sequenceNumberOfPreviousRecord = putRecordResult.getSequenceNumber();
	}

}
