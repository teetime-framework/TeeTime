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
package teetime.framework.scheduling.pushpullmodel;

class TeeTimeThread extends Thread {

	// private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private final AbstractRunnableStage runnable;
	private ThreadListener listener;

	public TeeTimeThread(final AbstractRunnableStage runnable, final String name) {
		super(runnable, name);
		this.runnable = runnable;
		setListener(new DefaultThreadListener());
	}

	public void sendStartingSignal() {
		if (runnable instanceof RunnableProducerStage) {
			((RunnableProducerStage) runnable).triggerStartingSignal();
		}
	}

	@Override
	public void start() {
		getListener().onBeforeStart(runnable.stage);
		super.start();
	}

	@Override
	public void run() {
		try {
			super.run();
		} finally {
			getListener().onAfterTermination(runnable.stage);
		}
	}

	public ThreadListener getListener() {
		return listener;
	}

	public void setListener(final ThreadListener listener) {
		this.listener = listener;
	}

}
