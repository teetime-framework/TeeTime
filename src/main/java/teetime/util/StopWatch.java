package teetime.util;

public final class StopWatch {

	private long startTimeInNs;
	private long endTimeInNs;

	public final void start() {
		this.startTimeInNs = System.nanoTime();
	}

	public final void end() {
		this.endTimeInNs = System.nanoTime();
	}

	public final long getDurationInNs() {
		return this.endTimeInNs - this.startTimeInNs;
	}
}
