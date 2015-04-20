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
package teetime.framework.test;

import teetime.framework.InputPort;
import teetime.framework.Stage;

public final class InputHolder<I> {

	private final StageTester stageTester;
	private final Stage stage;
	private final Iterable<Object> input;

	private InputPort<Object> port;

	@SuppressWarnings("unchecked")
	InputHolder(final StageTester stageTester, final Stage stage, final Iterable<I> input) {
		this.stageTester = stageTester;
		this.stage = stage;
		this.input = (Iterable<Object>) input;
	}

	@SuppressWarnings("unchecked")
	public StageTester to(final InputPort<? extends I> port) {
		if (port.getOwningStage() != stage) {
			throw new AssertionError();
		}
		this.port = (InputPort<Object>) port;

		return stageTester;
	}

	public Iterable<Object> getInput() {
		return input;
	}

	public InputPort<Object> getPort() {
		return port;
	}

}
