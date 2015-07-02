package teetime.framework;

class EmptyContext extends ConfigurationContext {

	private static final EmptyContext INSTANCE = new EmptyContext();

	private EmptyContext() {

	}

	static EmptyContext getInstance() {
		return EmptyContext.INSTANCE;
	}

}
