package teetime.framework.pipe;

public interface IMonitorablePipe {

	long getNumPushes();

	long getNumPulls();

	int size();

	long getPushThroughput();

	long getPullThroughput();
}
