package teetime.examples.tokenizer;

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.ByteArray2String;
import teetime.stage.CipherByteArray;
import teetime.stage.CipherByteArray.CipherMode;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.Tokenizer;
import teetime.stage.ZipByteArray;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.File2ByteArray;

public class TokenizerConfiguration extends AnalysisConfiguration {

	private static final PipeFactoryRegistry pipeFactoryRegistry = PipeFactoryRegistry.INSTANCE;
	private final File input;
	private final String password;
	private final Counter<String> counter;

	public TokenizerConfiguration(final String inputFile, final String password) {
		this.input = new File(inputFile);
		this.password = password;

		InitialElementProducer<File> init = new InitialElementProducer<File>(this.input);
		File2ByteArray f2b = new File2ByteArray();
		ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		CipherByteArray decrypt = new CipherByteArray(this.password, CipherMode.DECRYPT);
		ByteArray2String b2s = new ByteArray2String();
		Tokenizer tokenizer = new Tokenizer(" ");
		counter = new Counter<String>();

		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				init.getOutputPort(), f2b.getInputPort());
		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				f2b.getOutputPort(), decomp.getInputPort());
		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				decomp.getOutputPort(), decrypt.getInputPort());
		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				decrypt.getOutputPort(), b2s.getInputPort());
		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				b2s.getOutputPort(), tokenizer.getInputPort());
		pipeFactoryRegistry.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false).create(
				tokenizer.getOutputPort(), counter.getInputPort());

		this.getFiniteProducerStages().add(init);
	}

	public int getTokenCount() {
		return this.counter.getNumElementsPassed();
	}

}
