/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		final StringBuilder builder = new StringBuilder(this.invalidPortConnections.size() * 40);
		builder.append("2002 - ");
		builder.append(this.invalidPortConnections.size());
		builder.append(" invalid port connections were detected.\n");
		Joiner.on("\n").appendTo(builder, this.invalidPortConnections);
		return builder.toString();
	}

}
