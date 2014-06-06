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
package teetime.examples.traceReconstruction;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.concurrent.StageTerminationPolicy;
import teetime.framework.concurrent.WorkerThread;
import teetime.framework.core.Analysis;
import teetime.framework.core.IPipeline;
import teetime.framework.core.IStage;
import teetime.framework.core.Pipeline;
import teetime.framework.sequential.QueuePipe;
import teetime.stage.Cache;
import teetime.stage.CountingFilter;
import teetime.stage.InstanceOfFilter;
import teetime.stage.io.File2TextLinesFilter;
import teetime.stage.kieker.MonitoringLogDirectory2Files;
import teetime.stage.kieker.className.ClassNameRegistryCreationFilter;
import teetime.stage.kieker.className.ClassNameRegistryRepository;
import teetime.stage.kieker.fileToRecord.textLine.TextLine2RecordFilter;
import teetime.stage.kieker.traceReconstruction.TraceReconstructionFilter;
import teetime.stage.predicate.IsIMonitoringRecordInRange;
import teetime.stage.predicate.IsOperationExecutionRecordTraceIdPredicate;
import teetime.stage.predicate.PredicateFilter;
import teetime.stage.stringBuffer.StringBufferFilter;
import teetime.stage.stringBuffer.handler.IMonitoringRecordHandler;
import teetime.stage.stringBuffer.handler.StringHandler;
import teetime.stage.util.TextLine;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class TraceReconstructionAnalysis2 extends Analysis {
	private static final int SECONDS = 1000;

	private WorkerThread workerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;

	@Override
	public void init() {
		super.init();
		// IPipeline clockPipeline = buildClockPipeline();
		// this.clockThread = new WorkerThread(clockPipeline, 1);
		// Clock clockStage=(Clock) clockPipeline.getStartStages().get(0);

		final IPipeline pipeline = this.buildPipeline();
		this.workerThread = new WorkerThread(pipeline, 0);
	}

	/**
	 * @param clockStage
	 * @since 1.10
	 */
	private IPipeline buildPipeline() {
		// predicates TODO
		final IsIMonitoringRecordInRange isIMonitoringRecordInRange = new IsIMonitoringRecordInRange(0, 1000);
		final IsOperationExecutionRecordTraceIdPredicate isOperationExecutionRecordTraceIdPredicate = new IsOperationExecutionRecordTraceIdPredicate(
				false, null);

		final ClassNameRegistryCreationFilter classNameRegistryCreationFilter = new ClassNameRegistryCreationFilter(
				this.classNameRegistryRepository);
		final MonitoringLogDirectory2Files directory2FilesFilter = new MonitoringLogDirectory2Files();
		final File2TextLinesFilter file2TextLinesFilter = new File2TextLinesFilter();
		final Cache<TextLine> cache = new Cache<TextLine>();

		final TextLine2RecordFilter textLine2RecordFilter = new TextLine2RecordFilter(this.classNameRegistryRepository);
		final StringBufferFilter<IMonitoringRecord> stringBufferFilter = new StringBufferFilter<IMonitoringRecord>();
		final PredicateFilter<IMonitoringRecord> timestampFilter = new PredicateFilter<IMonitoringRecord>(
				isIMonitoringRecordInRange);
		final PredicateFilter<OperationExecutionRecord> traceIdFilter = new PredicateFilter<OperationExecutionRecord>(
				isOperationExecutionRecordTraceIdPredicate);
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
		final CountingFilter<TraceEventRecords> countingFilter = new CountingFilter<TraceEventRecords>();

		// configure stages
		stringBufferFilter.getDataTypeHandlers().add(new IMonitoringRecordHandler());
		stringBufferFilter.getDataTypeHandlers().add(new StringHandler());

		// add each stage to a stage list
		final LinkedList<IStage> startStages = new LinkedList<IStage>();
		startStages.add(classNameRegistryCreationFilter);

		final List<IStage> stages = new LinkedList<IStage>();
		stages.add(classNameRegistryCreationFilter);
		stages.add(directory2FilesFilter);
		stages.add(file2TextLinesFilter);
		stages.add(cache);

		stages.add(textLine2RecordFilter);
		stages.add(stringBufferFilter);
		stages.add(timestampFilter);
		stages.add(traceIdFilter);
		stages.add(instanceOfFilter);
		stages.add(traceReconstructionFilter);
		stages.add(countingFilter);

		// connect pipes
		QueuePipe.connect(classNameRegistryCreationFilter.filePrefixOutputPort,
				directory2FilesFilter.filePrefixInputPort);
		QueuePipe.connect(classNameRegistryCreationFilter.relayDirectoryOutputPort,
				directory2FilesFilter.directoryInputPort);
		QueuePipe.connect(directory2FilesFilter.fileOutputPort, file2TextLinesFilter.fileInputPort);
		QueuePipe.connect(file2TextLinesFilter.textLineOutputPort, cache.objectInputPort);
		// QueuePipe.connect(XXX, cache.sendInputPort);
		QueuePipe.connect(cache.objectOutputPort, textLine2RecordFilter.textLineInputPort);
		QueuePipe.connect(textLine2RecordFilter.recordOutputPort, stringBufferFilter.objectInputPort);
		QueuePipe.connect(stringBufferFilter.objectOutputPort, timestampFilter.inputPort);
		// QueuePipe.connect(timestampFilter.matchingOutputPort, traceIdFilter.inputPort);
		// QueuePipe.connect(timestampFilter.mismatchingOutputPort, YYY); // ignore this case
		QueuePipe.connect(traceIdFilter.matchingOutputPort, instanceOfFilter.inputPort);
		// QueuePipe.connect(traceIdFilter.mismatchingOutputPort, traceIdFilter.inputPort); // ignore this case
		// QueuePipe.connect(clockStage.timestampOutputPort, traceReconstructionFilter.timestampInputPort); // ignore
		// this case
		QueuePipe.connect(instanceOfFilter.matchingOutputPort, traceReconstructionFilter.recordInputPort);
		// QueuePipe.connect(instanceOfFilter.mismatchingOutputPort, instanceOfFilter.inputPort); // ignore this case
		QueuePipe.connect(traceReconstructionFilter.traceValidOutputPort, countingFilter.INPUT_OBJECT);
		// QueuePipe.connect(traceReconstructionFilter.traceInvalidOutputPort, ); // ignore this case

		final Pipeline pipeline = new Pipeline();
		pipeline.setStartStages(startStages);
		pipeline.setStages(stages);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.workerThread.setTerminationPolicy(StageTerminationPolicy.TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION);

		this.workerThread.start();
		try {
			this.workerThread.join(60 * SECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
