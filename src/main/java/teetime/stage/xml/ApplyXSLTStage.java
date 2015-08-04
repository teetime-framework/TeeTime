/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage.xml;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import teetime.stage.basic.AbstractTransformation;

public class ApplyXSLTStage extends AbstractTransformation<Document, String> {

	private final File xslt;
	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

	public ApplyXSLTStage(final String xsltFile) {
		this.xslt = new File(xsltFile);
	}

	public ApplyXSLTStage(final File xsltFile) {
		this.xslt = xsltFile;
	}

	@Override
	protected void execute(final Document inputDocument) {
		DOMSource source = new DOMSource(inputDocument);
		Source stylesheet = new StreamSource(xslt);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			Transformer transformer = transformerFactory.newTransformer(stylesheet);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		StringBuffer buffer = writer.getBuffer();
		outputPort.send(buffer.toString());
	}

}
