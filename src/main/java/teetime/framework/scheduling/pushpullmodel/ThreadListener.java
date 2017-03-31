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

import teetime.framework.AbstractStage;

interface ThreadListener {

	void onBeforeStart(AbstractStage stage);

	void onAfterTermination(AbstractStage stage);
}

class DefaultThreadListener implements ThreadListener {

	@Override
	public void onBeforeStart(final AbstractStage stage) {
		// do nothing
	}

	@Override
	public void onAfterTermination(final AbstractStage stage) {
		// do nothing
	}
}
