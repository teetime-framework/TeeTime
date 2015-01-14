package teetime.examples;

public enum HostName {

	CHW_HOME("Nogge-PC"),
	CHW_WORK("chw-PC"),
	NIE_WORK("nie-PC");

	private final String hostName;

	HostName(final String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}

	@Override
	public String toString() {
		return getHostName();
	}
}
