/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.stage;

import teetime.util.TimestampObject;

public class PortTypeConfiguration {

	public static <T> void setPortTypes(final ObjectProducer<T> stage, final Class<T> clazz) {
		stage.getOutputPort().setType(clazz);
	}

	public static <T> void setPortTypes(final CollectorSink<T> stage, final Class<T> clazz) {
		stage.getInputPort().setType(clazz);
	}

	public static <T> void setPortTypes(final StartTimestampFilter stage) {
		stage.getInputPort().setType(TimestampObject.class);
		stage.getOutputPort().setType(TimestampObject.class);
	}
}
