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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import teetime.framework.test.StageTester;

public class LoadXMLToDocumentStageTest {

	@Test
	public void loadXMLStageTest() {
		try {
			File xmlFile = createExampleXMLFile();

			LoadXMLToDocumentStage stage = new LoadXMLToDocumentStage();
			List<Document> documents = new ArrayList<Document>();

			StageTester.test(stage).and()
					.send(xmlFile.getAbsolutePath()).to(stage.getInputPort()).and()
					.receive(documents).from(stage.getOutputPort())
					.start();

			Document output = documents.get(0);
			NodeList as = output.getDocumentElement().getChildNodes();
			assertThat(as.getLength(), is(equalTo(5)));
			assertThat(as.item(0).getAttributes().item(0).getNodeValue(), is(equalTo("1")));
			assertThat(as.item(1).getAttributes().item(0).getNodeValue(), is(equalTo("2")));
			assertThat(as.item(2).getAttributes().item(0).getNodeValue(), is(equalTo("3")));
			assertThat(as.item(3).getAttributes().item(0).getNodeValue(), is(equalTo("4")));
			assertThat(as.item(4).getAttributes().item(0).getNodeValue(), is(equalTo("5")));

			xmlFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createExampleXMLFile() throws IOException, FileNotFoundException {
		File xmlFile = File.createTempFile("loadxmltest", ".xml");
		FileOutputStream outputStream = new FileOutputStream(xmlFile);

		String xml = "<?xml version=\"1.0\" ?>"
				+ "<root><a value=\"1\" /><a value=\"2\">2</a><a value=\"3\">3</a><a value=\"4\">4</a><a value=\"5\">5</a></root>";
		outputStream.write(xml.getBytes());
		outputStream.flush();
		outputStream.close();
		return xmlFile;
	}

}
