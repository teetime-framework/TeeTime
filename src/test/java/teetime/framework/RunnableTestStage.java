package teetime.framework;


class RunnableTestStage extends AbstractProducerStage<Object> {

	boolean executed, initialized;

	@Override
	protected void executeStage() {
		executed = true;
		this.terminate();
	}

	@Override
	protected void execute() {

	}

	@Override
	public void onInitializing() throws Exception {
		super.onInitializing();
		initialized = true;
	}

}
