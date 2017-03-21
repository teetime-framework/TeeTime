package teetime.framework.scheduling.globaltaskqueue;

import teetime.framework.AbstractStage;

/**
 * Created by nilsziermann on 05.01.17.
 */
public class StageBuffer {
	private final AbstractStage stage;
	private boolean done;

	public StageBuffer(final AbstractStage stage, final boolean done) {
		this.stage = stage;
		this.done = done;
	}

	public AbstractStage getStage() {
		return stage;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(final boolean done) {
		this.done = done;
	}
}
