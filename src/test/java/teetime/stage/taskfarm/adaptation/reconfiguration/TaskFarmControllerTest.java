/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage.taskfarm.adaptation.reconfiguration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.MD5Stage;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.util.framework.port.PortAction;

/**
 * @author Christian Claus Wiechmann
 */
public class TaskFarmControllerTest {

	@Ignore
	@Test
	public void test() throws InterruptedException {
		TestMerger<String> merger = new TestMerger<String>();
		MD5Stage stage = new MD5Stage();
		TaskFarmStage<String, String, MD5Stage> taskFarm = new TaskFarmStage<String, String, MD5Stage>(stage, merger, 100);

		final TaskFarmController<String, String> controller = new TaskFarmController<String, String>(taskFarm);

		sendPortActionToMerger(merger, controller);

		assertThat(merger.numberOfPortActions, is(equalTo(1)));
	}

	private void sendPortActionToMerger(final TestMerger<String> merger, final TaskFarmController<String, String> controller) throws InterruptedException {
		Thread adder = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					controller.addStageToTaskFarm();
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
		});
		adder.start();

		while (merger.numberOfPortActions == 0) {
			Thread.sleep(10); // FIXME sleeps infinitely
		}
	}

	private class TestMerger<T> extends DynamicMerger<T> {
		volatile int numberOfPortActions;

		@Override
		public boolean addPortActionRequest(final PortAction<DynamicMerger<T>> newPortActionRequest) {
			numberOfPortActions++;
			return super.addPortActionRequest(newPortActionRequest);
		}
	}

}
