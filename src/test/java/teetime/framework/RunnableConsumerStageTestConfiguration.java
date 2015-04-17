/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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

import java.util.ArrayList;
import java.util.List;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class RunnableConsumerStageTestConfiguration extends AnalysisConfiguration {

	private final List<Integer> collectedElements = new ArrayList<Integer>();
	private final CollectorSink<Integer> collectorSink;

	public RunnableConsumerStageTestConfiguration(final Integer... inputElements) {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(inputElements);
		if (inputElements.length > 0) {
			addThreadableStage(producer);
		}

		CollectorSink<Integer> collectorSink = new CollectorSink<Integer>(collectedElements);
		addThreadableStage(collectorSink);

		IPipeFactory pipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
		pipeFactory.create(producer.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;
	}

	public List<Integer> getCollectedElements() {
		return collectedElements;
	}

	public Thread getConsumerThread() {
		return collectorSink.getOwningThread();
	}
}