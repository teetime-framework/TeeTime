/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
