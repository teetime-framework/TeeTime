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

package teetime.variant.methodcallWithPorts.stage.kieker.fileToRecord.textLine;

import java.util.HashSet;
import java.util.Set;

import teetime.util.list.CommittableQueue;
import teetime.variant.explicitScheduling.stage.MappingException;
import teetime.variant.explicitScheduling.stage.util.TextLine;
import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;
import teetime.variant.methodcallWithPorts.stage.kieker.fileToRecord.RecordFromTextLineCreator;

import kieker.common.exception.IllegalRecordFormatException;
import kieker.common.exception.MonitoringRecordException;
import kieker.common.exception.UnknownRecordTypeException;
import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class TextLine2RecordFilter extends ConsumerStage<TextLine, IMonitoringRecord> {

	private final Set<String> unknownTypesObserved = new HashSet<String>();

	private boolean ignoreUnknownRecordTypes;

	private boolean abortDueToUnknownRecordType;

	private RecordFromTextLineCreator recordFromTextLineCreator;

	/**
	 * @since 1.10
	 */
	public TextLine2RecordFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.recordFromTextLineCreator = new RecordFromTextLineCreator(classNameRegistryRepository);
	}

	/**
	 * @since 1.10
	 */
	public TextLine2RecordFilter() {
		super();
	}

	public boolean isIgnoreUnknownRecordTypes() {
		return this.ignoreUnknownRecordTypes;
	}

	public void setIgnoreUnknownRecordTypes(final boolean ignoreUnknownRecordTypes) {
		this.ignoreUnknownRecordTypes = ignoreUnknownRecordTypes;
	}

	public RecordFromTextLineCreator getRecordFromTextLineCreator() {
		return this.recordFromTextLineCreator;
	}

	public void setRecordFromTextLineCreator(final RecordFromTextLineCreator recordFromTextLineCreator) {
		this.recordFromTextLineCreator = recordFromTextLineCreator;
	}

	@Override
	protected void execute4(final CommittableQueue<TextLine> elements) {
		throw new IllegalStateException();
	}

	@Override
	protected void execute5(final TextLine textLine) {
		try {
			final IMonitoringRecord record = this.recordFromTextLineCreator.createRecordFromLine(textLine.getTextFile(), textLine.getTextLine());
			this.send(record);
		} catch (final MonitoringRecordException e) {
			this.logger.error("Could not create record from text line: '" + textLine + "'", e);
		} catch (final IllegalRecordFormatException e) {
			this.logger.error("Illegal record format: " + textLine, e);
		} catch (final MappingException e) {
			this.logger.error("", e);
		} catch (final UnknownRecordTypeException e) {
			final String classname = e.getClassName();
			if (!this.ignoreUnknownRecordTypes) {
				this.abortDueToUnknownRecordType = true;
				this.logger.error("Failed to load record type " + classname, e);
			} else if (!this.unknownTypesObserved.contains(classname)) {
				this.unknownTypesObserved.add(classname);
				this.logger.error("Failed to load record type " + classname, e); // log once for this type
			}
		}
	}

}