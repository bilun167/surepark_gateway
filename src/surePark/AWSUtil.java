package surePark;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

public class AWSUtil {

    public static void main(String[] args) throws IOException {
        
        // Creating a kinesis stream
        // =========================
        // This is a sample stream.
        // We will use this program to be the producer for now.
        // will move to sample 2 for the consumer
        AmazonKinesisClient kinesisClient = new AmazonKinesisClient();
        kinesisClient.setEndpoint("kinesis.ap-southeast-1.amazonaws.com"
                                 ,"kinesis"
                                 ,"ap-southeast-1");
        
        // Checking if object is created fine
        if(kinesisClient!=null) System.out.println("\n\nGot kinesis\n\n");
        
        /*
         *
        // Creating the streamRequest
        CreateStreamRequest createStreamRequest = new CreateStreamRequest();
        createStreamRequest.setStreamName("exhaust1");
        createStreamRequest.setShardCount(1);
        
        // Creating the stream
        kinesisClient.createStream(createStreamRequest);
        *
        */

        // Putting data into the stream! exciting!!
        // ========================================
        String sequenceNumberOfPreviousRecord = "20";
        int j = 0;
        while(true) 
        {
        	try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            j++;
            PutRecordRequest putRecordRequest = new PutRecordRequest();
            putRecordRequest.setStreamName("exhaust1");
            String data = String.format("[Ashu]parking lot - %d",j);
            putRecordRequest.setData(ByteBuffer
                .wrap(data.getBytes()));
            putRecordRequest
                .setPartitionKey(String.format("partitionKey-%d",j));
            putRecordRequest
                .setSequenceNumberForOrdering(sequenceNumberOfPreviousRecord);
            PutRecordResult putRecordResult 
                = kinesisClient.putRecord(putRecordRequest);
            System.out.println(putRecordResult.toString());
            System.out.print(", data: ");
            System.out.print(data);
            sequenceNumberOfPreviousRecord 
                = putRecordResult.getSequenceNumber();
        }
    } 


}
