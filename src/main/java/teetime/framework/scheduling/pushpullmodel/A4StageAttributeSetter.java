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

import java.util.Set;

import teetime.framework.*;
import teetime.framework.exceptionHandling.AbstractExceptionListener;

/**
 * Sets the attributes of all stages within the same thread
 */
class A4StageAttributeSetter {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;
	private static final ConfigurationFacade CONFIG_FACADE = ConfigurationFacade.INSTANCE;

	// requires: factory and context
	private final Configuration configuration;
	private final Set<AbstractStage> threadableStages;

	public A4StageAttributeSetter(final Configuration configuration, final Set<AbstractStage> threadableStages) {
		super();
		this.configuration = configuration;
		this.threadableStages = threadableStages;
	}

	public void setAttributes() {
		for (AbstractStage threadableStage : threadableStages) {
			IntraStageCollector collector = new IntraStageCollector(threadableStage);
			Traverser traverser = new Traverser(collector);
			traverser.traverse(threadableStage);

			setAttributes(threadableStage, collector.getIntraStages());
		}
	}

	private void setAttributes(final AbstractStage threadableStage, final Set<AbstractStage> intraStages) {
		AbstractRunnableStage runnable;
		if (threadableStage.isProducer()) {
			runnable = new RunnableProducerStage(threadableStage);
		} else {
			runnable = new RunnableConsumerStage(threadableStage);
		}

		Thread newThread = new TeeTimeThread(runnable, "Thread for " + threadableStage.getId());
		AbstractExceptionListener exceptionhandler = CONFIG_FACADE.getFactory(configuration).createInstance(newThread);
		ConfigurationContext context = CONFIG_FACADE.getContext(configuration);

		intraStages.add(threadableStage);
		for (AbstractStage stage : intraStages) {
			STAGE_FACADE.setOwningThread(stage, newThread);
			STAGE_FACADE.setExceptionHandler(stage, exceptionhandler);
			STAGE_FACADE.setOwningContext(stage, context);
		}
	}
}
