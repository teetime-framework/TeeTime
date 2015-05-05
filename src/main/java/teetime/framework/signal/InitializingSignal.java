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
package teetime.framework.signal;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Stage;

public final class InitializingSignal implements ISignal {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartingSignal.class);
	private final List<Exception> catchedExceptions = new LinkedList<Exception>();

	public InitializingSignal() {}

	@Override
	public void trigger(final Stage stage) {
		try {
			stage.onInitializing();
		} catch (Exception e) { // NOCS (Stages can throw any arbitrary Exception)
			this.catchedExceptions.add(e);
			LOGGER.error("Exception while sending the start signal", e);
		}
	}

	public List<Exception> getCatchedExceptions() {
		return this.catchedExceptions;
	}

}
