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
package teetime.framework;

public class TeeTimeThread extends Thread {

	private final AbstractRunnableStage runnable;

	public TeeTimeThread(final AbstractRunnableStage runnable, final String name) {
		super(runnable, name);
		this.runnable = runnable;
	}

	public void sendStartingSignal() {
		if (runnable instanceof RunnableProducerStage) {
			((RunnableProducerStage) runnable).triggerStartingSignal();
		}
	}

	@Override
	public void start() {
		synchronized (this) {
			runnable.stage.getOwningContext().getThreadService().getRunnableCounter().inc();
			super.start();
		}
	}
}
