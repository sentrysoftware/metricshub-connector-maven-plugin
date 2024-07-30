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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

/**
 * This class parses the connectors that are located under the source directory then produces
 * a map of {@link JsonNode} instances that need to be used by the underlying page producers.
 */
public class ConnectorLibraryParser {

	/**
	 * This inner class allows to visit the files contained within the connectors directory
	 */
	private static class ConnectorFileVisitor extends SimpleFileVisitor<Path> {

		@Getter
		private final Map<String, JsonNode> connectorsMap = new HashMap<>();

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			// Skip this path if it is a directory or not a YAML file
			if (Files.isDirectory(file) || !isYamlFile(file.toFile().getName())) {
				return FileVisitResult.CONTINUE;
			}

			final JsonNode connectorNode = YAML_OBJECT_MAPPER.readTree(file.toFile());
			if (!isConnector(connectorNode)) {
				return FileVisitResult.CONTINUE;
			}

			final JsonNode connector = ConnectorParser.withNodeProcessor(file.getParent()).parse(file.toFile());

			final Path fileNamePath = file.getFileName();

			if (fileNamePath != null) {
				final String filename = fileNamePath.toString();
				connectorsMap.put(filename.substring(0, filename.lastIndexOf('.')), connector);
			}

			return FileVisitResult.CONTINUE;
		}

		/**
		 * Whether the JsonNode is a final Connector. It means that this JsonNode defines the displayName section.
		 *
		 * @param connector JsonNode that contains connector's data
		 * @return <code>true</code> if the {@link JsonNode} is a final connector, otherwise false.
		 */
		private boolean isConnector(final JsonNode connector) {
			final JsonNode connectorNode = connector.get("connector");
			if (connectorNode != null && !connectorNode.isNull()) {
				final JsonNode displayName = connectorNode.get("displayName");
				return displayName != null && !displayName.isNull();
			}

			return false;
		}

		/**
		 * Whether the connector is a YAML file or not
		 *
		 * @param fileName The name of the file
		 * @return boolean value
		 */
		private boolean isYamlFile(final String fileName) {
			return fileName.toLowerCase().endsWith(".yaml");
		}
	}

	/**
	 * Parse connectors located under the source directory
	 *
	 * @param sourceDirectory Source directory of the connectors.
	 * @return Map of {@link JsonNode} instances indexed by the connector ID (connectors map: key=connector-id, value=JsonNode)
	 * @throws IOException if the file does not exist
	 */
	public Map<String, JsonNode> parse(@NonNull final Path sourceDirectory) throws IOException {
		final ConnectorFileVisitor fileVisitor = new ConnectorFileVisitor();

		Files.walkFileTree(sourceDirectory, fileVisitor);

		return fileVisitor.getConnectorsMap();
	}
}
