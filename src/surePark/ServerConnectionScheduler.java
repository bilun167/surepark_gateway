package surePark;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

public class ServerConnectionScheduler {

	private static final Logger LOG = Logger
			.getLogger(ServerConnectionScheduler.class);

	private static ServerConnectionScheduler INSTANCE = null;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private AWSUtil awsutil = null;

	private ServerConnectionScheduler(AWSUtil awsutil) {
		this.awsutil = awsutil;
	}

	/**
	 * ServerConnectionScheduler getInstance.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ServerConnectionScheduler getInstance(AWSUtil awsutil)
			throws Exception {
		if (awsutil == null) {
			throw new Exception();
		}
		if (INSTANCE == null) {
			synchronized (ServerConnectionScheduler.class) {
				if (INSTANCE == null) {
					INSTANCE = new ServerConnectionScheduler(awsutil);
				}
			}
		}
		return INSTANCE;
	}

	public void reconnectionScheduler(long delayInSeconds) {
		final Runnable reconnect = new Runnable() {
			public void run() {
				while (true) {
					String coordinatorMAC = DataProcessorTask
							.getCoordinatorMAC();
					coordinatorMAC = coordinatorMAC == null ? "xyxyxyxrandomMAC"
							: coordinatorMAC;

					String rpieIP = ServerUtils.getInstance().getIpAddress();

					if (coordinatorMAC != null && rpieIP != null) {
						if (LOG.isInfoEnabled())
							LOG.info("coordinatorMAC-" + coordinatorMAC);
						if (LOG.isInfoEnabled())
							LOG.info("rpieIP-" + rpieIP);
						String ipString = "IP," + coordinatorMAC + "," + rpieIP;
						if (LOG.isInfoEnabled())
							LOG.info("Sending to Kinesis ... ipString : "
									+ ipString);

						awsutil.sendToKinesis(ipString);
						break;
					}
				}
			}
		};

		scheduler.scheduleAtFixedRate(reconnect, 10, delayInSeconds, SECONDS);

	}
}
