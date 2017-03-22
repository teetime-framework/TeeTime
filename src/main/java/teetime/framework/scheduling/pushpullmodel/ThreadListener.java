package teetime.framework.scheduling.pushpullmodel;

import teetime.framework.AbstractStage;

interface ThreadListener {

	void onBeforeStart(AbstractStage stage);

	void onAfterTermination(AbstractStage stage);
}

class DefaultThreadListener implements ThreadListener {

	@Override
	public void onBeforeStart(final AbstractStage stage) {
		// do nothing
	}

	@Override
	public void onAfterTermination(final AbstractStage stage) {
		// do nothing
	}
}
