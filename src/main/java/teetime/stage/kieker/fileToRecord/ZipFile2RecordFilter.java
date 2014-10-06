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
package teetime.stage.kieker.fileToRecord;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.kieker.className.ClassNameRegistry;
import teetime.stage.kieker.className.MappingFileParser;

import kieker.common.record.IMonitoringRecord;
import kieker.common.util.filesystem.FSUtil;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class ZipFile2RecordFilter extends ConsumerStage<File> {

	private final OutputPort<IMonitoringRecord> outputPort = this.createOutputPort();

	private final MappingFileParser mappingFileParser;

	/**
	 * @since 1.10
	 */
	public ZipFile2RecordFilter() {
		this.mappingFileParser = new MappingFileParser(this.logger);
	}

	@Override
	protected void execute(final File zipFile) {
		final InputStream mappingFileInputStream = this.findMappingFileInputStream(zipFile);
		if (mappingFileInputStream == null) {
			return;
		}
		final ClassNameRegistry classNameRegistry = this.mappingFileParser.parseFromStream(mappingFileInputStream);

		try {
			this.createAndSendRecordsFromZipFile(zipFile, classNameRegistry);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createAndSendRecordsFromZipFile(final File zipFile, final ClassNameRegistry classNameRegistry)
			throws FileNotFoundException {
		final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
		final BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(zipInputStream, FSUtil.ENCODING));
		} catch (final UnsupportedEncodingException e) {
			this.logger.error("This exception should never occur.", e);
			return;
		}
		final DataInputStream input = new DataInputStream(new BufferedInputStream(zipInputStream, 1024 * 1024));

		ZipEntry zipEntry;
		try {
			while (null != (zipEntry = zipInputStream.getNextEntry())) { // NOCS NOPMD
				final String filename = zipEntry.getName();
				// TODO implement the zip filter
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private InputStream findMappingFileInputStream(final File zipFile) {
		ZipInputStream zipInputStream = null;
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry;
			while ((null != (zipEntry = zipInputStream.getNextEntry())) && !zipEntry.getName().equals(FSUtil.MAP_FILENAME)) { // NOCS NOPMD
				// do nothing, just skip to the map file if present
			}
			if (null == zipEntry) {
				this.logger.error("The zip file does not contain a Kieker log: " + zipFile.toString());
				return null;
			}
			return zipInputStream;
		} catch (final IOException ex) {
			this.logger.error("Error accessing ZipInputStream", ex);
		} finally {
			if (null != zipInputStream) {
				try {
					zipInputStream.close();
				} catch (final IOException ex) {
					this.logger.error("Failed to close ZipInputStream", ex);
				}
			}
		}

		return null;
	}

	public OutputPort<IMonitoringRecord> getOutputPort() {
		return outputPort;
	}

}
