package teetime.stage.taskfarm.monitoring.extraction;

import teetime.framework.pipe.IMonitorablePipe;

class ExtractionTestPipe implements IMonitorablePipe {

	private long numPushes = 0;
	private long numPulls = 0;
	private int size = 1;
	private final int capacity = 1000;
	private long pushThroughput = 1;
	private long pullThroughput = 1;
	private int numWaits = 1;
	private boolean active = true;

	@Override
	public long getNumPushes() {
		if (active) {
			this.numPushes++;
		} else {
			this.numPushes = 0;
		}
		return this.numPushes;
	}

	@Override
	public long getNumPulls() {
		if (active) {
			this.numPulls += 2;
		} else {
			this.numPulls = 0;
		}
		return this.numPulls;
	}

	@Override
	public int size() {
		if (active) {
			this.size *= 2;
		} else {
			this.size = 0;
		}
		return this.size;
	}

	@Override
	public int capacity() {
		return this.capacity;
	}

	@Override
	public long getPushThroughput() {
		if (active) {
			this.pushThroughput += 3;
		} else {
			this.pushThroughput = 0;
		}
		return this.pushThroughput;
	}

	@Override
	public long getPullThroughput() {
		if (active) {
			this.pullThroughput += 5;
		} else {
			this.pullThroughput = 0;
		}
		return this.pullThroughput;
	}

	@Override
	public int getNumWaits() {
		if (active) {
			this.numWaits += 7;
		} else {
			this.numWaits = 0;
		}
		return this.numWaits;
	}

	public void setNumPushes(final long numPushes) {
		this.numPushes = numPushes;
	}

	public void setNumPulls(final long numPulls) {
		this.numPulls = numPulls;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	public void setPushThroughput(final long pushThroughput) {
		this.pushThroughput = pushThroughput;
	}

	public void setPullThroughput(final long pullThroughput) {
		this.pullThroughput = pullThroughput;
	}

	public void setNumWaits(final int numWaits) {
		this.numWaits = numWaits;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}
}
