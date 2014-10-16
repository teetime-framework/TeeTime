package teetime.examples.cipher;

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CipherByteArray;
import teetime.stage.CipherByteArray.CipherMode;
import teetime.stage.InitialElementProducer;
import teetime.stage.ZipByteArray;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.ByteArrayFileWriter;
import teetime.stage.io.File2ByteArray;

public class CipherConfiguration extends AnalysisConfiguration {

	public CipherConfiguration(final String inputFile, final String outputFile, final String password) {
		final File input = new File(inputFile);
		final File output = new File(outputFile);

		InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		File2ByteArray f2b = new File2ByteArray();
		CipherByteArray enc = new CipherByteArray(password, CipherMode.ENCRYPT);
		ZipByteArray comp = new ZipByteArray(ZipMode.COMP);
		ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		CipherByteArray decrypt = new CipherByteArray(password, CipherMode.DECRYPT);
		ByteArrayFileWriter writer = new ByteArrayFileWriter(output);

		IPipeFactory factory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

		factory.create(init.getOutputPort(), f2b.getInputPort());
		factory.create(f2b.getOutputPort(), enc.getInputPort());
		factory.create(enc.getOutputPort(), comp.getInputPort());
		factory.create(comp.getOutputPort(), decomp.getInputPort());
		factory.create(decomp.getOutputPort(), decrypt.getInputPort());
		factory.create(decrypt.getOutputPort(), writer.getInputPort());

		this.getFiniteProducerStages().add(init);

	}
}
