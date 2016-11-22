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
package teetime.stage.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import teetime.stage.basic.AbstractTransformation;

/**
 * Represents a transformation stage. It accepts a file path and tries to
 * interpret that file as an XML file. If successful, it is sent to the
 * output port as a {@link Document}.
 *
 * @author Christian Claus Wiechmann
 */
public class LoadXMLToDocumentStage extends AbstractTransformation<String, Document> {

	private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	@Override
	protected void execute(final String filename) {
		File file = new File(filename);
		Document document;

		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(file); // NOPMD DU-Anomaly: must remain in try-scope
		} catch (ParserConfigurationException e) {
			// parser cannot be build
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			// parser init error
			throw new IllegalStateException(e);
		} catch (IOException e) {
			// i/o error
			throw new IllegalStateException(e);
		}

		outputPort.send(document);
	}

}
