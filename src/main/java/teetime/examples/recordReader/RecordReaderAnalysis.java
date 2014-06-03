/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.examples.recordReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import teetime.framework.concurrent.StageTerminationPolicy;
import teetime.framework.concurrent.WorkerThread;
import teetime.framework.core.AbstractFilter;
import teetime.framework.core.Analysis;
import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;
import teetime.framework.core.IPipe;
import teetime.framework.core.IPipeline;
import teetime.framework.core.ISink;
import teetime.framework.core.ISource;
import teetime.framework.core.IStage;
import teetime.framework.sequential.MethodCallPipe;
import teetime.framework.sequential.QueuePipe;
import teetime.framework.util.BaseStage2StageExtractor;
import teetime.stage.CollectorSink;
import teetime.stage.kieker.File2RecordFilter;
import teetime.stage.kieker.className.ClassNameRegistryRepository;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class RecordReaderAnalysis extends Analysis {

	private static final int SECONDS = 1000;

	private WorkerThread workerThread;

	private File2RecordFilter file2RecordFilter;
	private CollectorSink<IMonitoringRecord> collector;

	private ClassNameRegistryRepository classNameRegistryRepository;

	@Override
	public void init() {
		super.init();
		final IPipeline pipeline = this.buildPipeline();
		this.workerThread = new WorkerThread(pipeline, 0);
	}

	@Override
	public void start() {
		super.start();

		this.workerThread.terminate(StageTerminationPolicy.TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION);

		this.workerThread.start();
		try {
			this.workerThread.join(60 * SECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	void setInputFile(final File file) {
		this.file2RecordFilter.fileInputPort.setAssociatedPipe(new MethodCallPipe<File>(file));
	}

	private IPipeline buildPipeline() {
		final BaseStage2StageExtractor baseStage2StageExtractor = new BaseStage2StageExtractor();

		this.classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		this.file2RecordFilter = new File2RecordFilter(this.classNameRegistryRepository);
		this.collector = new CollectorSink<IMonitoringRecord>();

		// add each stage to a stage list
		final List<IStage> stages = new LinkedList<IStage>();
		stages.addAll(baseStage2StageExtractor.extract(this.file2RecordFilter));
		stages.add(this.collector);

		// connect stages by pipes
		final List<IPipe<?>> pipes = new LinkedList<IPipe<?>>();
		pipes.add(this.connectWithSequentialPipe(this.file2RecordFilter.recordOutputPort, this.collector.objectInputPort));

		final IPipeline pipeline = new IPipeline() {
			@Override
			@SuppressWarnings("unchecked")
			public List<? extends IStage> getStartStages() {
				return baseStage2StageExtractor.extract(RecordReaderAnalysis.this.file2RecordFilter);
			}

			@Override
			public List<IStage> getStages() {
				return stages;
			}

			@Override
			public void fireStartNotification() throws Exception {
				for (final IStage stage : this.getStartStages()) {
					stage.notifyPipelineStarts();
				}
			}

			@Override
			public void fireStopNotification() {
				for (final IStage stage : this.getStartStages()) {
					stage.notifyPipelineStops();
				}
			}
		};

		return pipeline;
	}

	private <A extends ISource, B extends ISink<B>, T> IPipe<T> connectWithSequentialPipe(final IOutputPort<A, T> sourcePort,
			final IInputPort<B, T> targetPort) {
		final IPipe<T> pipe = new QueuePipe<T>();
		pipe.setSourcePort(sourcePort);
		pipe.setTargetPort(targetPort);
		return pipe;
	}

	WorkerThread getWorkerThread() {
		return this.workerThread;
	}

	public static void main(final String[] args) {
		final RecordReaderAnalysis analysis = new RecordReaderAnalysis();
		analysis.init();
		final long start = System.currentTimeMillis();
		analysis.start();
		final long end = System.currentTimeMillis();
		// analysis.terminate();
		final long duration = end - start;
		System.out.println("duration: " + duration + " ms"); // NOPMD (Just for example purposes)

		final IPipeline pipeline = analysis.workerThread.getPipeline();

		for (final IStage stage : pipeline.getStages()) {
			if (stage instanceof AbstractFilter<?>) {
//				System.out.println(stage.getClass().getName() + ": " + ((AbstractFilter<?>) stage).getOverallDurationInNs()); // NOPMD (Just for example purposes)
			}
		}
	}

	void setOutputRecordList(final List<IMonitoringRecord> records) {
		this.collector.setObjects(records);
	}

	public ClassNameRegistryRepository getClassNameRegistryRepository() {
		return this.classNameRegistryRepository;
	}

}
