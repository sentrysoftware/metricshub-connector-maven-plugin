package org.sentrysoftware.maven.metricshub.connector.producer;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for common operations and methods with JsonNode objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonNodeHelper {

	private static final Map<String, Boolean> BOOLEAN_MAP = new HashMap<>();

	static {
		BOOLEAN_MAP.put("0", Boolean.FALSE);
		BOOLEAN_MAP.put("1", Boolean.TRUE);
		BOOLEAN_MAP.put("true", Boolean.TRUE);
		BOOLEAN_MAP.put("false", Boolean.FALSE);
	}

	/**
	 * Checks if the provided JsonNode is non-null and not a JSON null node.
	 *
	 * @param node The JsonNode to be checked.
	 * @return {@code true} if the node is non-null and not a JSON null, {@code false} otherwise.
	 */
	public static boolean nonNull(final JsonNode node) {
		return node != null && !node.isNull();
	}

	/**
	 * Converts an {@link ArrayNode} into a filtered {@link Stream} of non-null {@link JsonNode} elements.
	 * <p>
	 * This method transforms the provided {@link ArrayNode} into a sequential stream of {@link JsonNode}
	 * objects, filtering out any null elements from the stream. The resulting stream is ordered and immutable.
	 * </p>
	 * @param arrayNode The {@link ArrayNode} to be converted to a {@link Stream}.
	 * @return A sequential {@link Stream} of non-null {@link JsonNode} elements extracted from the given {@link ArrayNode}.
	 */
	public static Stream<JsonNode> stream(final ArrayNode arrayNode) {
		return StreamSupport
			.stream(
				Spliterators.spliterator(arrayNode.iterator(), arrayNode.size(), Spliterator.ORDERED | Spliterator.IMMUTABLE),
				false
			)
			.filter(Objects::nonNull);
	}

	/**
	 * Converts a {@link JsonNode} to a {@link List} of strings.
	 * This method invokes {@link #nodeToStringCollection(JsonNode, Supplier)} with
	 * an {@link ArrayList} as the collection type.
	 *
	 * @param node The {@link JsonNode} to convert to a list of strings.
	 * @return A {@link List} of strings representing the contents of the input node, or an empty list if the node is null.
	 */
	public static List<String> nodeToStringList(final JsonNode node) {
		return nodeToStringCollection(node, ArrayList::new);
	}

	/**
	 * Converts a {@link JsonNode} to a {@link Collection} of strings.<br>
	 * For a string node, the string is split using a comma as a separator, trailing
	 * and leading spaces are removed from each element.<br>
	 *
	 * @param <C>               The type of the collection.
	 * @param node              The {@link JsonNode} to convert to a collection of strings.
	 * @param collectionFactory The factory for the collection to be returned.
	 * @return A {@link Collection} of strings representing the contents of the input node, or an empty Collection if the node is null.
	 */
	public static <C extends Collection<String>> C nodeToStringCollection(
		final JsonNode node,
		final Supplier<C> collectionFactory
	) {
		if (nonNull(node)) {
			if (node.isArray()) {
				return convertSimpleArrayNodeToCollection((ArrayNode) node, collectionFactory);
			}
			return Stream.of(node.asText().split(",")).map(String::trim).collect(Collectors.toCollection(collectionFactory));
		}

		return Stream.<String>empty().collect(Collectors.toCollection(collectionFactory));
	}

	/**
	 * Converts a Jackson {@link ArrayNode} to a {@link Collection} of strings.
	 *
	 * @param <C> The type of the collection.
	 * @param arrayNode The {@code ArrayNode} to be converted to a collection of strings.
	 * @param collectionFactory The factory for the collection to be returned.
	 * @return A {@code List<String>} containing the text representations of the elements in the {@code ArrayNode}.
	 * @throws NullPointerException if {@code arrayNode} is {@code null}.
	 */
	public static <C extends Collection<String>> C convertSimpleArrayNodeToCollection(
		final ArrayNode arrayNode,
		final Supplier<C> collectionFactory
	) {
		return stream(arrayNode).map(JsonNode::asText).collect(Collectors.toCollection(collectionFactory));
	}

	/**
	 * Returns the text value of the provided JsonNode if it is non-null and not a JSON null node.
	 * If the node is null or represents a JSON null, the specified default value is returned.
	 *
	 * @param node         The JsonNode to extract text from.
	 * @param defaultValue The default value to return if the node is null or represents a JSON null.
	 * @return The text value of the JsonNode or the specified default value if the node is null or represents a JSON null.
	 */
	public static String nonNullTextOrDefault(final JsonNode node, final String defaultValue) {
		if (nonNull(node)) {
			return node.asText();
		}
		return defaultValue;
	}

	/**
	 * Returns the boolean value of the provided JsonNode if it is non-null and not a JSON null node.
	 * If the node is null or represents a JSON null, the specified default value is returned.
	 *
	 * @param node         The JsonNode to extract text from.
	 * @param defaultValue The default value to return if the node is null or represents a JSON null.
	 * @return The boolean value of the JsonNode or the specified default value if the node is null or represents a JSON null.
	 */
	public static boolean nonNullBooleanOrDefault(final JsonNode node, final boolean defaultValue) {
		if (nonNull(node)) {
			return BOOLEAN_MAP.getOrDefault(node.asText().trim(), defaultValue);
		}
		return defaultValue;
	}
}
