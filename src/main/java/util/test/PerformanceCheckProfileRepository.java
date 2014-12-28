package util.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceCheckProfileRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceCheckProfileRepository.class);

	public static final PerformanceCheckProfileRepository INSTANCE = new PerformanceCheckProfileRepository();

	private final Map<Class<?>, AbstractProfiledPerformanceAssertion> performanceCheckProfiles = new HashMap<Class<?>, AbstractProfiledPerformanceAssertion>();
	private String currentProfile;

	public PerformanceCheckProfileRepository() {
		String hostName = getHostName();
		// this.currentProfile = System.getProperty("TestProfile", "ChwWork");
		currentProfile = hostName;
		LOGGER.info("Using test profile '" + this.currentProfile + "'");
	}

	private String getHostName() {
		String hostname = "Unknown";

		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			LOGGER.warn("Hostname can not be resolved");
		}

		return hostname;
	}

	public void setCurrentProfile(final String currentProfile) {
		this.currentProfile = currentProfile;
	}

	public String getCurrentProfile() {
		return this.currentProfile;
	}

	public void register(final Class<?> testClass, final AbstractProfiledPerformanceAssertion profile) {
		if (profile.getCorrespondingPerformanceProfile().equals(this.currentProfile)) {
			this.performanceCheckProfiles.put(testClass, profile);
		}
	}

	public AbstractProfiledPerformanceAssertion get(final Class<?> clazz) {
		return this.performanceCheckProfiles.get(clazz);
	}
}
