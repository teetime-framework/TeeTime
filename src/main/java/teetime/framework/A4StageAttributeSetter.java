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
package teetime.framework;

import java.util.Set;

/**
 * Sets the attributes of all stages within the same thread
 */
class A4StageAttributeSetter {

	private final Configuration configuration;
	private final Set<Stage> threadableStages;

	public A4StageAttributeSetter(final Configuration configuration, final Set<Stage> threadableStages) {
		super();
		this.configuration = configuration;
		this.threadableStages = threadableStages;
	}

	public void setAttributes() {
		for (Stage threadableStage : threadableStages) {
			setAttributes(threadableStage);
		}
	}

	private void setAttributes(final Stage threadableStage) {
		IntraStageCollector visitor = new IntraStageCollector(threadableStage);
		Traverser traverser = new Traverser(visitor);
		traverser.traverse(threadableStage);

		setAttributes(threadableStage, traverser.getVisitedStages());
	}

	private void setAttributes(final Stage threadableStage, final Set<Stage> intraStages) {
		threadableStage.setExceptionHandler(configuration.getFactory().createInstance());
		// threadableStage.setOwningThread(owningThread);
		threadableStage.setOwningContext(configuration.getContext());

		for (Stage stage : intraStages) {
			stage.setExceptionHandler(threadableStage.exceptionListener);
			stage.setOwningThread(threadableStage.getOwningThread());
			stage.setOwningContext(threadableStage.getOwningContext());
		}
	}
}
