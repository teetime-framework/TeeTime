package teetime.stage.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import teetime.stage.basic.AbstractTransformation;

public class LoadXMLStage extends AbstractTransformation<String, Document> {

	private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	@Override
	protected void execute(final String filename) {
		File file = new File(filename);
		Document document = null;

		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(file);
		} catch (ParserConfigurationException e) {
			// parser cannot be build
			e.printStackTrace();
		} catch (SAXException e) {
			// parser init error
			e.printStackTrace();
		} catch (IOException e) {
			// i/o error
			e.printStackTrace();
		}

		outputPort.send(document);
	}

}
