package teetime.examples.cipher;

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.PipeFactory;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;
import teetime.stage.InitialElementProducer;
import teetime.stage.io.ByteArrayFileWriter;
import teetime.stage.io.CipherByteArray;
import teetime.stage.io.CipherByteArray.CipherMode;
import teetime.stage.io.File2ByteArray;
import teetime.stage.io.ZipByteArray;
import teetime.stage.io.ZipByteArray.ZipMode;

public class CipherConfiguration extends AnalysisConfiguration {

	private final PipeFactory pipeFactory = PipeFactory.INSTANCE;
	private final File input, output;
	private final String password;

	public CipherConfiguration(final String inputFile, final String outputFile, final String password) {
		this.input = new File(inputFile);
		this.output = new File(outputFile);
		this.password = password;

		InitialElementProducer<File> init = new InitialElementProducer<File>(this.input);
		File2ByteArray f2b = new File2ByteArray();
		CipherByteArray enc = new CipherByteArray(this.password, CipherMode.ENCRYPT);
		ZipByteArray comp = new ZipByteArray(ZipMode.COMP);
		ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		CipherByteArray decrypt = new CipherByteArray(this.password, CipherMode.DECRYPT);
		ByteArrayFileWriter writer = new ByteArrayFileWriter(output);

		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(init.getOutputPort(), f2b.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(f2b.getOutputPort(), enc.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(enc.getOutputPort(), comp.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(comp.getOutputPort(), decomp.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(decomp.getOutputPort(), decrypt.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(decrypt.getOutputPort(), writer.getInputPort());

		this.getFiniteProducerStages().add(init);

	}
}
