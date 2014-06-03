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

package teetime.stage.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import teetime.framework.core.AbstractFilter;
import teetime.framework.core.Context;
import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class Directory2FilesFilter extends AbstractFilter<Directory2FilesFilter> {

	public final IInputPort<Directory2FilesFilter, File> directoryInputPort = this.createInputPort();

	public final IOutputPort<Directory2FilesFilter, File> fileOutputPort = this.createOutputPort();

	private FileFilter filter;
	private Comparator<File> fileComparator;

	/**
	 * @since 1.10
	 */
	public Directory2FilesFilter(final FileFilter fileFilter) {
		this.setFilter(fileFilter);
	}

	/**
	 * @since 1.10
	 */
	public Directory2FilesFilter(final Comparator<File> fileComparator) {
		this.setFileComparator(fileComparator);
	}

	/**
	 * @since 1.10
	 */
	public Directory2FilesFilter(final FileFilter fileFilter, final Comparator<File> fileComparator) {
		this.setFilter(fileFilter);
		this.setFileComparator(fileComparator);
	}

	/**
	 * @since 1.10
	 */
	public Directory2FilesFilter() {
		super();
	}

	/**
	 * @since 1.10
	 */
	@Override
	protected boolean execute(final Context<Directory2FilesFilter> context) {
		final File inputDir = context.tryTake(this.directoryInputPort);
		if (inputDir == null) {
			return false;
		}

		final File[] inputFiles = inputDir.listFiles(this.filter);

		if (inputFiles == null) {
			this.logger.error("Directory '" + inputDir + "' does not exist or an I/O error occured.");
			return true;
		}

		if (this.fileComparator != null) {
			Arrays.sort(inputFiles, this.fileComparator);
		}

		for (final File file : inputFiles) {
			context.put(this.fileOutputPort, file);
		}

		return true;
	}

	public FileFilter getFilter() {
		return this.filter;
	}

	public void setFilter(final FileFilter filter) {
		this.filter = filter;
	}

	public Comparator<File> getFileComparator() {
		return this.fileComparator;
	}

	public void setFileComparator(final Comparator<File> fileComparator) {
		this.fileComparator = fileComparator;
	}

}
