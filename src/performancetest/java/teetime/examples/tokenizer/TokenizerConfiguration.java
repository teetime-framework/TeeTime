package teetime.examples.tokenizer;

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.PipeFactory;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;
import teetime.stage.InitialElementProducer;
import teetime.stage.io.ByteArray2String;
import teetime.stage.io.CipherByteArray;
import teetime.stage.io.CipherByteArray.CipherMode;
import teetime.stage.io.File2ByteArray;
import teetime.stage.io.Printer;
import teetime.stage.io.Tokenizer;
import teetime.stage.io.ZipByteArray;
import teetime.stage.io.ZipByteArray.ZipMode;

public class TokenizerConfiguration extends AnalysisConfiguration {

	private final PipeFactory pipeFactory = PipeFactory.INSTANCE;
	private final File input;
	private final String password;

	public TokenizerConfiguration(final String inputFile, final String password) {
		this.input = new File(inputFile);
		this.password = password;

		InitialElementProducer<File> init = new InitialElementProducer<File>(this.input);
		File2ByteArray f2b = new File2ByteArray();
		CipherByteArray enc = new CipherByteArray(this.password, CipherMode.ENCRYPT);
		ZipByteArray comp = new ZipByteArray(ZipMode.COMP);
		ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		CipherByteArray decrypt = new CipherByteArray(this.password, CipherMode.DECRYPT);
		ByteArray2String b2s = new ByteArray2String();
		Tokenizer tokenizer = new Tokenizer(" ");
		Printer<String> pt = new Printer<String>();

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
				.create(decrypt.getOutputPort(), b2s.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(b2s.getOutputPort(), tokenizer.getInputPort());
		this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false)
				.create(tokenizer.getOutputPort(), pt.getInputPort());

		this.getFiniteProducerStages().add(init);

	}
}
