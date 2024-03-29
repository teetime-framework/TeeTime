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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import teetime.framework.test.StageTester;

public class ApplyXSLTToDocumentStageTest {

	@Test
	public void applyXSLTStageTest() throws FileNotFoundException, IOException {
		File xmlFile = createExampleXMLFile();

		LoadXMLToDocumentStage loadStage = new LoadXMLToDocumentStage();
		List<Document> documents = new ArrayList<Document>();

		StageTester.test(loadStage).and().send(xmlFile.getAbsolutePath()).to(loadStage.getInputPort()).and()
				.receive(documents).from(loadStage.getOutputPort()).start();

		Document outputXML = documents.get(0);
		NodeList as = outputXML.getDocumentElement().getChildNodes();
		assertThat(as.getLength(), is(equalTo(5)));
		documents.clear();

		File xsltFile = createExampleXSLTFile();

		ApplyXSLTToDocumentStage xsltStage = new ApplyXSLTToDocumentStage(xsltFile);
		List<String> outputs = new ArrayList<String>();

		StageTester.test(xsltStage).and().send(outputXML).to(xsltStage.getInputPort()).and().receive(outputs)
				.from(xsltStage.getOutputPort()).start();

		assertThat(outputs.get(0), is(equalTo("54321")));

		xmlFile.delete();
		xsltFile.delete();
	}

	private File createExampleXSLTFile() throws IOException, FileNotFoundException {
		File xsltFile = File.createTempFile("applyxslttest", ".xslt");
		OutputStream outputStreamXSLT = Files.newOutputStream(xsltFile.toPath());

		String xslt = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n"
				+ "<xsl:output method=\"text\"/>\r\n" + "<xsl:template match=\"/\">\r\n"
				+ "    <xsl:apply-templates select=\"root/a\">\r\n"
				+ "        <xsl:sort select=\"position()\" data-type=\"number\" order=\"descending\"/>\r\n"
				+ "    </xsl:apply-templates>\r\n" + "</xsl:template>\r\n" + "<xsl:template match=\"root\">\r\n"
				+ "<xsl:value-of select=\".\"/>\r\n" + "</xsl:template>\r\n" + "</xsl:stylesheet>\r\n";
		outputStreamXSLT.write(xslt.getBytes());
		outputStreamXSLT.flush();
		outputStreamXSLT.close();
		return xsltFile;
	}

	private File createExampleXMLFile() throws IOException, FileNotFoundException {
		File xmlFile = File.createTempFile("applyxslttest", ".xml");
		OutputStream outputStreamXML = Files.newOutputStream(xmlFile.toPath());

		String xml = "<?xml version=\"1.0\" ?>"
				+ "<root><a>1</a><a value=\"2\">2</a><a value=\"3\">3</a><a value=\"4\">4</a><a value=\"5\">5</a></root>";
		outputStreamXML.write(xml.getBytes());
		outputStreamXML.flush();
		outputStreamXML.close();
		return xmlFile;
	}
}
