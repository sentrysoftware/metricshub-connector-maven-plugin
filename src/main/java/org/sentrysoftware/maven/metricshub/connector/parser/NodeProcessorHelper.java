package org.sentrysoftware.maven.metricshub.connector.parser;

/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * MetricsHub Connector Maven Plugin
 * ჻჻჻჻჻჻
 * Copyright (C) 2023 Sentry Software
 * ჻჻჻჻჻჻
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱
 */

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@code NodeProcessorHelper} class provides utility methods for creating instances of node processors.
 * <p>
 * This class encapsulates static factory methods for creating various node processors, such as the
 * {@link ConstantsProcessor} and the {@link ExtendsProcessor} with a {@link ConstantsProcessor} destination.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeProcessorHelper {

	/**
	 * Creates a new {@link ConstantsProcessor}
	 *
	 * @return new {@link ConstantsProcessor}
	 */
	private static AbstractNodeProcessor newConstantsProcessor() {
		return new ConstantsProcessor();
	}

	/**
	 * Create a {@link ExtendsProcessor} with {@link ConstantsProcessor} destination
	 *
	 * @param connectorDirectory Used to locate a the connector parent directory in a file system
	 * @return new {@link ExtendsProcessor} instance
	 */
	public static AbstractNodeProcessor withExtendsAndConstantsProcessor(final Path connectorDirectory) {
		return new ExtendsProcessor(connectorDirectory, newConstantsProcessor());
	}
}
