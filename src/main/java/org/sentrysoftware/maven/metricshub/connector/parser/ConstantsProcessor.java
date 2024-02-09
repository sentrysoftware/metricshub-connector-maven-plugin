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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The constant processor performs constant replacements over a {@link JsonNode} instance
 */
public class ConstantsProcessor extends AbstractNodeProcessor {

	/**
	 * Constants Pattern
	 */
	private static final String CONSTANTS_REFERENCE_PATTERN = "${constant::%s}";

	/**
	 * Constructs a ConstantsProcessor without a next processor.
	 */
	public ConstantsProcessor() {
		super(null);
	}

	@Override
	public JsonNode processNode(final JsonNode node) {
		final JsonNode constantsNode = node.get("constants");

		if (constantsNode != null && constantsNode.isObject()) {
			final List<String> constantKeys = new ArrayList<>(constantsNode.size());
			constantsNode.fieldNames().forEachRemaining(constantKeys::add);

			final Map<String, String> replacements = new HashMap<>();
			for (String key : constantKeys) {
				final JsonNode child = constantsNode.get(key);
				replacements.put(String.format(CONSTANTS_REFERENCE_PATTERN, key), child.asText());
			}

			final UnaryOperator<String> updater = value -> performReplacements(replacements, value);

			final Predicate<String> predicate = Objects::nonNull;

			JsonNodeUpdater.builder().withJsonNode(node).withUpdater(updater).withPredicate(predicate).build().update();
		}

		return node;
	}

	/**
	 * Replace placeholders in the given value with corresponding values from the provided
	 * key-value pairs in the replacements {@link Map}.
	 *
	 * @param replacements Key-value pairs representing placeholders and their replacement values.
	 *                     <br>Example: { $constants.query1=MyQuery1, $constants.query2=MyQuery2 }
	 * @param value        The string to be replaced.
	 * @return A new {@link String} with the placeholders replaced.
	 */
	private String performReplacements(final Map<String, String> replacements, String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}

		// Loop over each placeholder and perform replacement
		for (final Entry<String, String> entry : replacements.entrySet()) {
			final String key = entry.getKey();
			if (value.contains(key)) {
				value = value.replace(key, entry.getValue());
			}
		}

		// return the new value
		return value;
	}
}
