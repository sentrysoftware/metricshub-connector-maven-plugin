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

import static org.sentrysoftware.maven.metricshub.connector.Constants.YAML_OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;

/**
 * This class parses the connector YAML file and produces the corresponding {@link JsonNode}
 */
@AllArgsConstructor
public class ConnectorParser {

	private AbstractNodeProcessor nodeProcessor;

	/**
	 * Parses the specified connector file.
	 *
	 * @param connectorFile The file to be parsed.
	 *
	 * @return A new {@link JsonNode} object.
	 * @throws IOException If an IO error occurs during deserialization or processing.
	 */
	public JsonNode parse(final File connectorFile) throws IOException {
		final JsonNode node = YAML_OBJECT_MAPPER.readTree(connectorFile);

		if (nodeProcessor != null) {
			return nodeProcessor.process(node);
		}

		return node;
	}

	/**
	 * Creates a new {@link ConnectorParser} with extends and constants.
	 *
	 * @param connectorDirectory The directory where all the connectors are located.
	 * @return new instance of {@link ConnectorParser}
	 */
	public static ConnectorParser withNodeProcessor(final Path connectorDirectory) {
		return new ConnectorParser(NodeProcessorHelper.withExtendsAndConstantsProcessor(connectorDirectory));
	}
}
