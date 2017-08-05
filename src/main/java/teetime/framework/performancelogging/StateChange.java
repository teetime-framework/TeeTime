/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.performancelogging;

/**
 * This class stores an active/inactive flag at a given time.
 *
 * @author Adrian
 *
 */
public class StateChange {

	public enum StageActivationState {
		/**
		 * Represents the state where the stage has been initialized, but not yet been executed.
		 * This state is used to implement the null object pattern.
		 * It avoids to check for <code>this.lastState == null</code>.
		 */
		INITIALIZED,
		/** Represents the state where the stage is being executed. */
		ACTIVE,
		/** Represents the state where the stage is waiting for its passive successor stage to return. */
		// ACTIVE_WAITING,
		/** Represents the state where the stage is waiting for its active successor stage to consume the elements within the interconnected pipe. */
		BLOCKED,
		/** Represents the stage where the stage has been terminated. */
		TERMINATED,
	}

	private final StageActivationState stageActivationState;
	private final long timeStamp;

	public StateChange(final StageActivationState state, final long timeStamp) {
		this.stageActivationState = state;
		this.timeStamp = timeStamp;
	}

	public StageActivationState getStageActivationState() {
		return stageActivationState;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

}
