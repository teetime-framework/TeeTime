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
