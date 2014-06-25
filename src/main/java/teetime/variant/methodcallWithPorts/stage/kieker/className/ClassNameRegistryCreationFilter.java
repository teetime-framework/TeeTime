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
package teetime.variant.methodcallWithPorts.stage.kieker.className;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class ClassNameRegistryCreationFilter extends ConsumerStage<File, File> {

	private ClassNameRegistryRepository classNameRegistryRepository;

	private final MappingFileParser mappingFileParser;

	/**
	 * @since 1.10
	 */
	public ClassNameRegistryCreationFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		this();
		this.classNameRegistryRepository = classNameRegistryRepository;
	}

	/**
	 * @since 1.10
	 */
	public ClassNameRegistryCreationFilter() {
		super();
		this.mappingFileParser = new MappingFileParser(this.logger);
	}

	@Override
	protected void execute5(final File inputDir) {
		final File mappingFile = this.mappingFileParser.findMappingFile(inputDir);
		if (mappingFile == null) {
			return;
		}

		try {
			final ClassNameRegistry classNameRegistry = this.mappingFileParser.parseFromStream(new FileInputStream(mappingFile));
			this.classNameRegistryRepository.put(inputDir, classNameRegistry);
			this.send(inputDir);

			// final String filePrefix = this.mappingFileParser.getFilePrefixFromMappingFile(mappingFile);
			// context.put(this.filePrefixOutputPort, filePrefix); // TODO pass prefix
		} catch (final FileNotFoundException e) {
			this.logger.error("Mapping file not found.", e); // and skip this directory
		}
	}

	public ClassNameRegistryRepository getClassNameRegistryRepository() {
		return this.classNameRegistryRepository;
	}

	public void setClassNameRegistryRepository(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.classNameRegistryRepository = classNameRegistryRepository;
	}

	@Override
	protected void execute4(final CommittableQueue<File> elements) {
		throw new IllegalStateException();
	}

}
