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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import org.sentrysoftware.maven.metricshub.connector.Constants;

/**
 * The {@code ExtendsProcessor} class performs the merging of extended connectors.
 * <p>
 * This processor is designed to handle the merging of extended connectors specified under the "extends" section
 * of a given JSON node. The merging process involves recursively combining extended connectors and the provided
 * JSON node. The merging logic is implemented in the {@link #doMerge(JsonNode)} method.
 * </p>
 * @see AbstractNodeProcessor
 */
public class ExtendsProcessor extends AbstractNodeProcessor {

	private final Path connectorDirectory;

	/**
	 * Constructs a new instance of ExtendsProcessor with the specified connector
	 * directory, YAML ObjectMapper, and optional next processor.
	 *
	 * @param connectorDirectory The directory path for connectors.
	 * @param next               The next processor in the processing chain.
	 */
	public ExtendsProcessor(Path connectorDirectory, AbstractNodeProcessor next) {
		super(next);
		this.connectorDirectory = connectorDirectory;
	}

	@Override
	public JsonNode processNode(JsonNode node) throws IOException {
		return doMerge(node);
	}

	/**
	 * Merges extended connectors recursively.<br>
	 * Merge logic:<br>
	 * <ol>
	 *   <li>Merged extended connectors located under the extends section of the given node.</li>
	 *   <li>Once all the extended connectors are merged, merge the given JsonNode (node) with the extended connectors that have been merged.</li>
	 * </ol>
	 * <br>
	 * A recursive merge is applied for each extended connector because it can extend another connector too. That's why doMerge
	 * is called for each extended connector.
	 * @param node {@link JsonNode} to process
	 * @return {@link JsonNode} instance
	 * @throws IOException
	 */
	private JsonNode doMerge(JsonNode node) throws IOException {
		JsonNode extNode = node.get("extends");

		JsonNode result = node;
		if (extNode != null && extNode.isArray()) {
			final ArrayNode extNodeArray = (ArrayNode) extNode;
			final Iterator<JsonNode> iter = extNodeArray.iterator();

			JsonNode extended = null;
			if (iter.hasNext()) {
				extended = doMerge(getJsonNode(iter));
				while (iter.hasNext()) {
					final JsonNode extendedNext = doMerge(getJsonNode(iter));
					merge(extended, extendedNext);
				}
			}

			extNodeArray.removeAll();

			if (extended != null) {
				result = merge(extended, node);
			}
		}
		return result;
	}

	/**
	 * Gets the next {@link JsonNode} from the iterator
	 *
	 * @param iterator {@link Iterator} over a collection of {@link JsonNode}
	 * @return {@link JsonNode} object
	 * @throws IOException
	 */
	private JsonNode getJsonNode(Iterator<JsonNode> iterator) throws IOException {
		return Constants.YAML_OBJECT_MAPPER.readTree(
			connectorDirectory.resolve(iterator.next().asText() + ".yaml").toFile()
		);
	}

	/**
	 * Merge the given mainNode and updateNode.
	 * Merge strategy:<br>
	 * <ol>
	 *   <li>Arrays of objects are appended from <code>updateNode</code> to <code>mainNode</code>.</li>
	 *   <li>Arrays of simple values from <code>updateNode</code> erase the ones in <code>mainNode</code>.</li>
	 *   <li><code>updateNode</code> object values overwrite <code>mainNode</code> object values.<li>
	 * </ol>
	 *
	 * @param mainNode   The main JsonNode to be merged.
	 * @param updateNode The JsonNode containing updates to be merged into the mainNode.
	 * @return {@link JsonNode} merged
	 */
	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
		final Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {
			final String fieldName = fieldNames.next();
			final JsonNode jsonNode = mainNode.get(fieldName);
			if (jsonNode != null && jsonNode.isArray() && updateNode.get(fieldName).isArray()) {
				// both JSON nodes are arrays
				mergeJsonArray(updateNode, fieldName, jsonNode);
			} else if (jsonNode != null && jsonNode.isObject()) {
				// both JSON nodes are objects, merge them
				merge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {
					// overwrite field
					final JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).set(fieldName, value);
				}
			}
		}
		return mainNode;
	}

	/**
	 * Handles the specific merge logic for arrays, considering the merge strategy.
	 *
	 * @param updateNode      The JsonNode containing the array to be merged.
	 * @param fieldName       The name of the field corresponding to the array in the updateNode.
	 * @param mainArrayNode   The mainArrayNode in the main JsonNode to be merged.
	 */
	private static void mergeJsonArray(final JsonNode updateNode, final String fieldName, final JsonNode mainArrayNode) {
		ArrayNode mainArray = (ArrayNode) mainArrayNode;
		ArrayNode extendedArray = (ArrayNode) updateNode.get(fieldName);

		if (mainArray.size() != 0 && mainArray.get(0).isObject()) {
			// Array of objects gets merged (appended)
			for (int i = 0; i < extendedArray.size(); i++) {
				mainArray.add(extendedArray.get(i));
			}
		} else {
			// Simple array gets overwritten
			mainArray.removeAll();
			mainArray.addAll(extendedArray);
		}
	}
}
