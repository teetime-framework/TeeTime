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
	private final Set<AbstractStage> threadableStages;

	public A4StageAttributeSetter(final Configuration configuration, final Set<AbstractStage> threadableStages) {
		super();
		this.configuration = configuration;
		this.threadableStages = threadableStages;
	}

	public void setAttributes() {
		for (AbstractStage threadableStage : threadableStages) {
			setAttributes(threadableStage);
		}
	}

	private void setAttributes(final AbstractStage threadableStage) {
		IntraStageCollector visitor = new IntraStageCollector(threadableStage);
		Traverser traverser = new Traverser(visitor);
		traverser.traverse(threadableStage);

		setAttributes(threadableStage, traverser.getVisitedStages());
	}

	private void setAttributes(final AbstractStage threadableStage, final Set<AbstractStage> intraStages) {
		threadableStage.setExceptionHandler(configuration.getFactory().createInstance(threadableStage.getOwningThread()));
		// threadableStage.setOwningThread(owningThread);
		threadableStage.setOwningContext(configuration.getContext());

		for (AbstractStage stage : intraStages) {
			stage.setExceptionHandler(threadableStage.exceptionListener);
			stage.setOwningThread(threadableStage.getOwningThread());
			stage.setOwningContext(threadableStage.getOwningContext());
		}
	}
}
