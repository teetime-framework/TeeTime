package teetime.framework;

import java.util.Set;

public class A4StageAttributeSetter {

	private final Configuration configuration;
	private final Set<Stage> threadableStages;

	public A4StageAttributeSetter(final Configuration configuration, final Set<Stage> threadableStages) {
		super();
		this.configuration = configuration;
		this.threadableStages = threadableStages;
	}

	public void setAttributes() {
		for (Stage threadableStage : threadableStages) {
			IPipeVisitor pipeVisitor = new IntraStageCollector();
			Traverser traverser = new Traverser(pipeVisitor);
			traverser.traverse(threadableStage);

			setAttributes(threadableStage, traverser.getVisitedStages());
		}
	}

	private void setAttributes(final Stage threadableStage, final Set<Stage> intraStages) {
		threadableStage.setExceptionHandler(configuration.getFactory().createInstance());
		// threadableStage.setOwningThread(owningThread);
		threadableStage.setOwningContext(configuration.getContext());

		for (Stage stage : intraStages) {
			stage.setExceptionHandler(threadableStage.exceptionListener);
			stage.setOwningThread(threadableStage.getOwningThread());
			stage.setOwningContext(threadableStage.getOwningContext());
		}
	}
}
