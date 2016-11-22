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

	public enum ExecutionState {
		/**
		 * Represents the state where the stage has been initialized, but not yet been executed.
		 * This state is used to implement the null object pattern.
		 * It avoids to check for <code>this.lastState == null</code>.
		 */
		INITIALIZED,
		/** Represents the state where the stage is being executed. */
		ACTIVE,
		/** Represents the state where the stage is waiting for its passive successor stage to return. */
		ACTIVE_WAITING,
		/** Represents the state where the stage is waiting for its active successor stage to consume the elements within the interconnected pipe. */
		BLOCKED,
		/** Represents the stage where the stage has been terminated. */
		TERMINATED,
	}

	// TODO vielleicht überflüssig
	public final static int SENDING_FAILED = -1;
	public final static int PULLING_FAILED = -2;
	public final static int GENERAL_EXCEPTION = -3;
	public final static int NOTHING_FAILED = 0;

	private final ExecutionState executionState;
	private final long timeStamp;
	private final int cause;

	public StateChange(final ExecutionState state) {
		this(state, System.nanoTime(), 0);
	}

	public StateChange(final ExecutionState state, final int cause) {
		this(state, System.nanoTime(), cause);
	}

	public StateChange(final ExecutionState state, final long timeStamp, final int cause) {
		this.executionState = state;
		this.timeStamp = timeStamp;
		this.cause = cause;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getCause() {
		return cause;
	}

	public ExecutionState getExecutionState() {
		return executionState;
	}

}
