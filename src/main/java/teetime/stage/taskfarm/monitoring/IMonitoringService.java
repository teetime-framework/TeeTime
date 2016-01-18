/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage.taskfarm.monitoring;

import java.util.List;

/**
 * Represents an interface on a monitoring service used to monitor varying object, e.g. pipes.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <K>
 *            type of monitored item
 * @param <V>
 *            type of data container containing the monitored data
 */
public interface IMonitoringService<K, V extends IMonitoringData> {

	/**
	 * Add object to list of monitored elements.
	 * 
	 * @param item
	 *            object to be monitored
	 */
	public void addMonitoredItem(final K item);

	/**
	 * @return all monitored data
	 */
	public List<?> getData();

	/**
	 * Adds a new measurement.
	 */
	public void doMeasurement();

}
