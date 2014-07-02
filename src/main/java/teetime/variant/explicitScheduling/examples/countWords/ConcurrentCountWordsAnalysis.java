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

package teetime.variant.explicitScheduling.examples.countWords;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import teetime.util.Pair;
import teetime.variant.explicitScheduling.framework.concurrent.ConcurrentWorkStealingPipe;
import teetime.variant.explicitScheduling.framework.concurrent.ConcurrentWorkStealingPipeFactory;
import teetime.variant.explicitScheduling.framework.concurrent.SingleProducerSingleConsumerPipe;
import teetime.variant.explicitScheduling.framework.concurrent.StageTerminationPolicy;
import teetime.variant.explicitScheduling.framework.concurrent.WorkerThread;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.explicitScheduling.framework.core.IInputPort;
import teetime.variant.explicitScheduling.framework.core.IOutputPort;
import teetime.variant.explicitScheduling.framework.core.IPipeline;
import teetime.variant.explicitScheduling.framework.core.ISink;
import teetime.variant.explicitScheduling.framework.core.ISource;
import teetime.variant.explicitScheduling.framework.core.IStage;
import teetime.variant.explicitScheduling.framework.core.Pipeline;
import teetime.variant.explicitScheduling.framework.core.IInputPort.PortState;
import teetime.variant.explicitScheduling.framework.sequential.MethodCallPipe;
import teetime.variant.explicitScheduling.framework.sequential.QueuePipe;
import teetime.variant.explicitScheduling.stage.basic.RepeaterSource;
import teetime.variant.explicitScheduling.stage.basic.distributor.Distributor;
import teetime.variant.explicitScheduling.stage.basic.merger.Merger;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class ConcurrentCountWordsAnalysis extends Analysis {

	private static final int NUM_TOKENS_TO_REPEAT = 1000;
	private static final String START_DIRECTORY_NAME = ".";
	private static final int SECONDS = 1000;

	private static final int MAX_NUM_THREADS = 2; // 1:2150, 2:1400, 3:~1400, 4:~1400

	private WorkerThread[] ioThreads;
	private WorkerThread[] nonIoThreads;

	ConcurrentWorkStealingPipeFactory<?>[] pipeFactories;

	@Override
	public void init() {
		super.init();

		this.ioThreads = new WorkerThread[2];

		final IPipeline readerThreadPipeline = this.readerThreadPipeline();
		@SuppressWarnings("unchecked")
		final Distributor<File> distributor = (Distributor<File>) readerThreadPipeline.getStages().get(readerThreadPipeline.getStages().size() - 1);
		this.ioThreads[0] = new WorkerThread(readerThreadPipeline, 1);
		this.ioThreads[0].setName("startThread");
		this.ioThreads[0].setTerminationPolicy(StageTerminationPolicy.TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION);

		final IPipeline printingThreadPipeline = this.printingThreadPipeline();
		@SuppressWarnings("unchecked")
		final Merger<Pair<File, Integer>> merger = (Merger<Pair<File, Integer>>) printingThreadPipeline.getStages().get(0);
		this.ioThreads[1] = new WorkerThread(printingThreadPipeline, 2);
		this.ioThreads[1].setName("printingThread");
		this.ioThreads[1].setTerminationPolicy(StageTerminationPolicy.TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION);

		this.createPipeFactories();

		int numThreads = Runtime.getRuntime().availableProcessors();
		numThreads = Math.min(MAX_NUM_THREADS, numThreads); // only for testing purposes
		System.out.println("Using " + numThreads + " Threads.");

		this.nonIoThreads = new WorkerThread[numThreads];
		for (int i = 0; i < this.nonIoThreads.length; i++) {
			final IPipeline pipeline = this.buildNonIoPipeline(distributor, merger);
			this.nonIoThreads[i] = new WorkerThread(pipeline, 0);
			this.nonIoThreads[i].setTerminationPolicy(StageTerminationPolicy.TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION);
		}
	}

	@Override
	public void start() {
		super.start();

		for (final WorkerThread thread : this.ioThreads) {
			thread.start();
		}

		for (final WorkerThread thread : this.nonIoThreads) {
			thread.start();
		}

		System.out.println("Waiting for the non I/O worker threads to terminate..."); // NOPMD (Just for example purposes)
		for (final WorkerThread thread : this.nonIoThreads) {
			try {
				thread.join(60 * SECONDS);
			} catch (final InterruptedException e) {
				throw new IllegalStateException();
			}
		}

		System.out.println("Waiting for the I/O worker threads to terminate..."); // NOPMD (Just for example purposes)
		for (final WorkerThread thread : this.ioThreads) {
			try {
				thread.join(60 * SECONDS);
			} catch (final InterruptedException e) {
				throw new IllegalStateException();
			}
		}

		System.out.println("Analysis finished."); // NOPMD (Just for example purposes)
	}

	private void createPipeFactories() {
		this.pipeFactories = new ConcurrentWorkStealingPipeFactory[4];
		this.pipeFactories[0] = new ConcurrentWorkStealingPipeFactory<File>();
		this.pipeFactories[1] = new ConcurrentWorkStealingPipeFactory<File>();
		this.pipeFactories[2] = new ConcurrentWorkStealingPipeFactory<Pair<File, Integer>>();
		this.pipeFactories[3] = new ConcurrentWorkStealingPipeFactory<Pair<File, Integer>>();
	}

	private IPipeline readerThreadPipeline() {
		// create stages
		final RepeaterSource<String> repeaterSource = RepeaterSource.create(START_DIRECTORY_NAME, NUM_TOKENS_TO_REPEAT);
		repeaterSource.setAccessesDeviceId(1);
		final DirectoryName2Files directoryName2Files = new DirectoryName2Files();
		directoryName2Files.setAccessesDeviceId(1);
		final Distributor<File> distributor = new Distributor<File>();
		distributor.setAccessesDeviceId(1);

		// add each stage to a stage list
		final List<IStage> startStages = new LinkedList<IStage>();
		startStages.add(repeaterSource);

		final List<IStage> stages = new LinkedList<IStage>();
		stages.add(repeaterSource);
		stages.add(directoryName2Files);
		stages.add(distributor);

		// connect stages by pipes
		QueuePipe.connect(repeaterSource.OUTPUT, directoryName2Files.DIRECTORY_NAME);
		QueuePipe.connect(directoryName2Files.fileOutputPort, distributor.genericInputPort);

		repeaterSource.START.setAssociatedPipe(new MethodCallPipe<Boolean>(Boolean.TRUE));
		repeaterSource.START.setState(PortState.CLOSED);

		final Pipeline pipeline = new Pipeline();
		pipeline.setStartStages(startStages);
		pipeline.setStages(stages);
		return pipeline;
	}

	private IPipeline buildNonIoPipeline(final Distributor<File> readerDistributor, final Merger<Pair<File, Integer>> printingMerger) {
		// create stages
		final Distributor<File> distributor = new Distributor<File>();
		final CountWordsStage countWordsStage0 = new CountWordsStage();
		final CountWordsStage countWordsStage1 = new CountWordsStage();
		final Merger<Pair<File, Integer>> merger = new Merger<Pair<File, Integer>>();

		// add each stage to a stage list
		final List<IStage> startStages = new LinkedList<IStage>();
		startStages.add(distributor);

		final List<IStage> stages = new LinkedList<IStage>();
		stages.add(distributor);
		stages.add(countWordsStage0);
		stages.add(countWordsStage1);
		stages.add(merger);

		// connect stages by pipes
		SingleProducerSingleConsumerPipe.connect(readerDistributor.getNewOutputPort(), distributor.genericInputPort);

		this.connectWithStealAwarePipe(this.pipeFactories[0], distributor.getNewOutputPort(), countWordsStage0.FILE);
		this.connectWithStealAwarePipe(this.pipeFactories[1], distributor.getNewOutputPort(), countWordsStage1.FILE);
		this.connectWithStealAwarePipe(this.pipeFactories[2], countWordsStage0.WORDSCOUNT, merger.getNewInputPort());
		this.connectWithStealAwarePipe(this.pipeFactories[3], countWordsStage1.WORDSCOUNT, merger.getNewInputPort());

		SingleProducerSingleConsumerPipe.connect(merger.outputPort, printingMerger.getNewInputPort());

		final Pipeline pipeline = new Pipeline();
		pipeline.setStartStages(startStages);
		pipeline.setStages(stages);
		return pipeline;
	}

	private IPipeline printingThreadPipeline() {
		// create stages
		final Merger<Pair<File, Integer>> merger = new Merger<Pair<File, Integer>>();
		merger.setAccessesDeviceId(2);
		final OutputWordsCountSink outputWordsCountStage = new OutputWordsCountSink();
		outputWordsCountStage.setAccessesDeviceId(2);

		// add each stage to a stage list
		final List<IStage> startStages = new LinkedList<IStage>();
		startStages.add(merger);

		final List<IStage> stages = new LinkedList<IStage>();
		stages.add(merger);
		stages.add(outputWordsCountStage);

		// connect stages by pipes
		QueuePipe.connect(merger.outputPort, outputWordsCountStage.fileWordcountTupleInputPort);

		final Pipeline pipeline = new Pipeline();
		pipeline.setStartStages(startStages);
		pipeline.setStages(stages);
		return pipeline;
	}

	private <A extends ISource, B extends ISink<B>, T> void connectWithStealAwarePipe(final ConcurrentWorkStealingPipeFactory<?> pipeFactory,
			final IOutputPort<A, T> sourcePort, final IInputPort<B, T> targetPort) {
		@SuppressWarnings("unchecked")
		final ConcurrentWorkStealingPipe<T> pipe = (ConcurrentWorkStealingPipe<T>) pipeFactory.create();
		pipe.setSourcePort(sourcePort);
		pipe.setTargetPort(targetPort);
	}

	/**
	 * @since 1.10
	 */
	public static void main(final String[] args) {
		final ConcurrentCountWordsAnalysis analysis = new ConcurrentCountWordsAnalysis();
		analysis.init();
		final long start = System.currentTimeMillis();
		analysis.start();
		final long end = System.currentTimeMillis();
		// analysis.terminate();
		final long duration = end - start;
		System.out.println("duration: " + duration + " ms"); // NOPMD (Just for example purposes)

		analysis.analyzeThreads();
	}

	private void analyzeThreads() {
		long maxDuration = -1;
		WorkerThread maxThread = null;

		// FIXME resolve bug; see analysis results below;
		//
		// --- Thread[startThread,5,] ---
		// {RepeaterSource: numPushedElements={numPushedElements=1000, numTakenElements=1}}
		// {DirectoryName2Files: numPushedElements={numPushedElements=16000, numTakenElements=1000}}
		// {Distributor: numPushedElements={numPushedElements=16000, numTakenElements=16000}}
		// --- Thread[Thread-2,5,] ---
		// {Distributor: numPushedElements={numPushedElements=16008, numTakenElements=16008}}
		//
		// cause: the non-io distributor is executed faster than the io distributor

		for (final WorkerThread thread : this.ioThreads) {
			System.out.println("--- " + thread + " ---"); // NOPMD (Just for example purposes)
			for (final IStage stage : thread.getPipeline().getStages()) {
				System.out.println(stage); // NOPMD (Just for example purposes)
			}

			// final long durationInNs = thread.getDurationInNs();
			// System.out.println(thread + " takes " + TimeUnit.NANOSECONDS.toMillis(durationInNs) + " ms");
		}

		for (final WorkerThread thread : this.nonIoThreads) {
			System.out.println("--- " + thread + " ---"); // NOPMD (Just for example purposes)
			for (final IStage stage : thread.getPipeline().getStages()) {
				System.out.println(stage); // NOPMD (Just for example purposes)
			}

			// final long durationInNs = thread.getDurationInNs();
			// System.out.println(thread + " takes " + TimeUnit.NANOSECONDS.toMillis(durationInNs) + " ms");

			// if (durationInNs > maxDuration) {
			// maxDuration = durationInNs;
			// maxThread = thread;
			// }
		}

		// System.out.println("maxThread: " + maxThread.toString() + " takes " + TimeUnit.NANOSECONDS.toMillis(maxDuration) + " ms"); // NOPMD (Just for example
		// purposes)
	}
}