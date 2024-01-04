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

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.AllArgsConstructor;

/**
 * Abstract base class for implementing a chain of responsibility pattern in processing JsonNodes.
 * Each concrete subclass represents a specific processing step in the chain.
 */
@AllArgsConstructor
public abstract class AbstractNodeProcessor {

	/**
	 * Next node processor
	 */
	protected AbstractNodeProcessor next;

	/**
	 * Process the provided {@link JsonNode} with the remaining chain of processors.
	 *
	 * @param node The JsonNode to be processed.
	 * @return An instance of {@link JsonNode} representing the result of the processing.
	 * @throws IOException If an I/O error occurs during the processing.
	 */
	public JsonNode process(final JsonNode node) throws IOException {
		final JsonNode processedNode = processNode(node);

		if (next != null) {
			return next.process(processedNode);
		}

		return processedNode;
	}

	/**
	 * Process one {@link JsonNode}.
	 *
	 * @param node The JsonNode to be processed.
	 * @return An instance of {@link JsonNode} representing the result of the processing.
	 * @throws IOException If an I/O error occurs during the processing.
	 */
	protected abstract JsonNode processNode(JsonNode node) throws IOException;
}
