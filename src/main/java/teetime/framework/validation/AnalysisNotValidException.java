package teetime.framework.validation;

import java.util.List;

import com.google.common.base.Joiner;

public class AnalysisNotValidException extends RuntimeException {

	private static final long serialVersionUID = 455596493924684318L;

	private final List<InvalidPortConnection> invalidPortConnections;

	public AnalysisNotValidException(final List<InvalidPortConnection> invalidPortConnections) {
		super();
		this.invalidPortConnections = invalidPortConnections;

	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder(this.invalidPortConnections.size() * 40);
		builder.append(this.invalidPortConnections.size());
		builder.append(" invalid port connections were detected.\n");
		Joiner.on("\n").appendTo(builder, this.invalidPortConnections);
		return builder.toString();
	}

}
