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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.ConnectorDefaultVariable;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;

/**
 * Implementation for reading information from a JSON structure representing
 * a connector.
 *
 * <p>
 * This class provides methods to extract various fields and information from a
 * {@link JsonNode} representing a connector. It serves as an utility
 * for reading data associated with a connector. Various methods are available
 * to retrieve specific attributes such as supersedes, detection, criteria or
 * other relevant details.
 * </p>
 * <p>
 * Instances of this class are typically created with a {@link JsonNode}
 * containing connector information, and the provided methods can then be used
 * to access and extract specific details from the JSON structure.
 * </p>
 *
 */
@AllArgsConstructor
public class ConnectorJsonNodeReader {

	/**
	 * Defines a regular expression pattern for matching metric names enclosed in curly braces
	 */
	private static final Pattern METRIC_NAME_PATTERN = Pattern.compile("^\\s*([^\\{]*)\\{.*\\}\\s*$");

	/**
	 * Defines a regular expression pattern for matching a state metric
	 */
	private static final Pattern STATE_METRIC_PATTERN = Pattern.compile(
		"^\\s*([^\\{]*)\\{.*(\\s*state\\s*=\\s*.*)\\}\\s*$"
	);

	/**
	 * Defines a regular expression pattern for matching connector variables name.
	 */
	private static final Pattern CONNECTOR_VARIABLE_PATTERN = Pattern.compile("\\$\\{var::(.*?)\\}");

	private final JsonNode connector;

	/**
	 * Retrieves the display name property of the connector located under the <em>connector</em> JSON node.
	 *
	 * @return The display name as a String.
	 */
	public String getDisplayName() {
		return getConnectorSection()
			.map(node -> node.get("displayName"))
			.filter(JsonNodeHelper::nonNull)
			.map(JsonNode::asText)
			.orElse("");
	}

	/**
	 * Retrieves the information property of the connector, if available.
	 *
	 * @param defaultValue The default value to return if the information property is null or represents a JSON null.
	 * @return The information property as a String, or the specified default value if not present.
	 */
	public String getInformationOrDefault(final String defaultValue) {
		final JsonNode information = getConnectorSection().map(node -> node.get("information")).orElse(null);

		return JsonNodeHelper.nonNullTextOrDefault(information, defaultValue);
	}

	/**
	 * Retrieves the platforms property of the connector, if available.
	 *
	 * @return The platforms property as a Set, or the specified default value if not present.
	 */
	public Set<String> getPlatforms() {
		final JsonNode platforms = getConnectorSection().map(node -> node.get("platforms")).orElse(null);

		return JsonNodeHelper.nodeToStringCollection(platforms, TreeSet<String>::new);
	}

	/**
	 * Retrieves the list of supersedes values from the connector's detection property, if available.
	 *
	 * @return A list of strings representing the supersedes values, or an empty list if not present.
	 */
	public List<String> getSupersedes() {
		final JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode supersedes = detection.get("supersedes"); // NOSONAR JsonNodeHelper.nonNull() is already called
			return JsonNodeHelper.nodeToStringList(supersedes);
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves the list of appliesTo (OS) values from the connector's detection property, if available.
	 *
	 * @return A list of strings representing the OS values, or an empty list if not present.
	 */
	public List<String> getAppliesTo() {
		final JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode appliesTo = detection.get("appliesTo"); // NOSONAR JsonNodeHelper.nonNull() is already called
			return JsonNodeHelper.nodeToStringList(appliesTo);
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves the required MetricsHub version from the connector's detection criteria.
	 * <p>
	 * This method retrieves the required MetricsHub version by extracting the detection criteria
	 * obtained from the connector. It looks for criteria of type "productRequirements" and extracts
	 * the associated engine version. If no matching criteria are found or the necessary information is
	 * not available, the method returns <code>null</code>.
	 * </p>
	 * @return The required MetricsHub version, or null if the information is not available.
	 */
	public String getRequiredMetricsHubVersion() {
		final JsonNode criteria = getDetectionCriteria();

		// If criteria information is not available or is not an array, return null
		if (!JsonNodeHelper.nonNull(criteria) || !criteria.isArray()) { // NOSONAR JsonNodeHelper.nonNull() is already called
			return null;
		}

		// Filter criteria of type "productRequirements" and extract the engine version
		return JsonNodeHelper
			.stream((ArrayNode) criteria)
			.filter(node -> {
				final JsonNode typeNode = getType(node);
				return JsonNodeHelper.nonNull(typeNode) && "productrequirements".equalsIgnoreCase(typeNode.asText());
			})
			.map(node -> node.get("engineVersion"))
			.filter(JsonNodeHelper::nonNull)
			.findFirst()
			.map(JsonNode::asText)
			.orElse(null);
	}

	/**
	 * Retrieves the type field value from the specified node.
	 *
	 * @param node {@link JsonNode} instance
	 * @return The "type" field value from the given node as a {@link JsonNode}, or {@code null} if not available.
	 */
	private JsonNode getType(final JsonNode node) {
		return node.get("type");
	}

	/**
	 * Retrieves the detection criteria from the connector's detection section.
	 *
	 * @return The "criteria" field from the "detection" section as a {@link JsonNode}, or {@code null} if not available.
	 */
	private JsonNode getDetectionCriteria() {
		final JsonNode detection = getDetection();

		// If detection information is not available, return null
		if (!JsonNodeHelper.nonNull(detection)) {
			return null;
		}

		return detection.get("criteria"); // NOSONAR JsonNodeHelper.nonNull() is already called
	}

	/**
	 * Retrieves the detection from the connector
	 *
	 * @return The "detection" field from the connector node as a {@link JsonNode}, or {@code null} if not available.
	 */
	private JsonNode getDetection() {
		return getConnectorSection().map(node -> node.get("detection")).orElse(null);
	}

	/**
	 * Retrieves the reliesOn property of the connector, if available.
	 *
	 * @param defaultValue The default value to return if the reliesOn property is null or represents a JSON null.
	 * @return The reliesOn property as a String, or the specified default value if not present.
	 */
	public String getReliesOnOrDefault(final String defaultValue) {
		final JsonNode reliesOn = getConnectorSection().map(node -> node.get("reliesOn")).orElse(null);

		return JsonNodeHelper.nonNullTextOrDefault(reliesOn, defaultValue);
	}

	/**
	 * Retrieves the "connector" section from the connector
	 *
	 * @return The "connector" field from the connector node as an Optional of {@link JsonNode}.
	 */
	private Optional<JsonNode> getConnectorSection() {
		return Optional.ofNullable(connector.get("connector"));
	}

	/**
	 * Retrieves the set of {@link TechnologyType}s used by the monitor jobs in the connector.
	 * <p>
	 * This method iterates through the monitors defined in the connector and collects the {@link TechnologyType}s
	 * associated with their discovery, collect, and simple jobs. The result is a set of unique technology types.
	 * </p>
	 * @return A {@link Set} of {@link TechnologyType}s used by monitors in the connector.
	 */
	public Set<TechnologyType> getTechnologies() {
		final Set<TechnologyType> technologies = new HashSet<>();
		getMonitors()
			.ifPresent(monitors -> monitors.forEach(monitor -> collectTechnologies(technologies, getMonitorJobs(monitor))));

		return technologies;
	}

	/**
	 * Gets the monitors from the connector.
	 *
	 * @return The monitors as an Optional of {@link JsonNode}.
	 */
	public Optional<JsonNode> getMonitors() {
		return Optional.ofNullable(connector.get("monitors"));
	}

	/**
	 * Collects technology types from the specified jobs and adds them to the provided set.
	 * <p>
	 * This method processes the sources defined in each job and extracts the technology type from the "type" field.
	 * The detected technology types are added to the provided set of technology.
	 * </p>
	 * @param technology The set of {@link TechnologyType}s to which the collected technology types are added.
	 * @param jobs         The JSON nodes representing different jobs (e.g., discovery, collect, simple).
	 */
	private void collectTechnologies(final Set<TechnologyType> technologies, final JsonNode... jobs) {
		Stream
			.of(jobs)
			.filter(JsonNodeHelper::nonNull)
			.forEach(job -> {
				final JsonNode sources = job.get("sources");
				if (!JsonNodeHelper.nonNull(sources)) {
					return;
				}
				sources.forEach(source -> {
					final JsonNode typeNode = getType(source);
					if (JsonNodeHelper.nonNull(typeNode)) {
						TechnologyType.getTechnologyType(typeNode.asText()).ifPresent(technologies::add);
					}
				});
			});
	}

	/**
	 * Retrieves a list of sudo commands configured in the connector.
	 * <p>
	 * This method retrieves the sudo commands defined in the "sudoCommands" section of the connector.
	 * The commands are returned as a {@link List} of strings. If the "sudoCommands" section is not present or
	 * is not of the expected type, an empty list is returned.
	 * </p>
	 * @return A {@link List} of strings representing the sudo commands set in the connector.
	 */
	public List<String> getSudoCommands() {
		final JsonNode sudoCommands = connector.get("sudoCommands");
		return JsonNodeHelper.nodeToStringList(sudoCommands);
	}

	/**
	 * Retrieves a set of connection types specified in the connector's detection.
	 * <p>
	 * This method looks for the "connectionTypes" field within the "detection" section of the connector configuration.
	 * If the "detection" section is present and contains the "connectionTypes" field, the method returns a {@link Set}
	 * of case-insensitive strings representing the connection types. If the "detection" section or the "connectionTypes" field is not
	 * present or is not of the expected type, an empty set is returned.
	 *
	 * @return A case-insensitive {@link Set} of strings representing the connection types (<em>local</em> and/or <em>remote</em>).
	 */
	public Set<String> getConnectionTypes() {
		final JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode connectionTypes = detection.get("connectionTypes"); // NOSONAR JsonNodeHelper.nonNull() is already called
			return nodeToCaseInsensitiveSet(connectionTypes);
		}
		return Collections.emptySet();
	}

	/**
	 * Converts a {@link JsonNode} to a case-insensitive set of strings.
	 *
	 * @param node The {@link JsonNode} to convert to a case-insensitive set.
	 * @return A case-insensitive {@link Set} of strings representing the contents of the input node, or an empty set if the node is null
	 *         or not of the expected types.
	 */
	private Set<String> nodeToCaseInsensitiveSet(final JsonNode node) {
		return JsonNodeHelper.nodeToStringList(node).stream().map(String::toLowerCase).collect(Collectors.toSet());
	}

	/**
	 * Checks if auto-detection is disabled for the connector.
	 * <p>
	 * This method looks for the presence of the "disableAutoDetection" field within the "detection" section of the connector.
	 * If the field is present and is a boolean value, the method returns its boolean value. If the "detection" section
	 * or the "disableAutoDetection" field is not present or is not a boolean, the method assumes auto-detection is enabled
	 * and returns false.
	 * </p>
	 * @return {@code true} if auto-detection is explicitly disabled, {@code false} if explicitly enabled, or {@code false} if the
	 *         configuration is not present.
	 */
	public boolean isAutoDetectionDisabled() {
		final JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode disableAutoDetection = detection.get("disableAutoDetection"); // NOSONAR JsonNodeHelper.nonNull() is already called
			if (JsonNodeHelper.nonNull(disableAutoDetection) && disableAutoDetection.isBoolean()) {
				return disableAutoDetection.asBoolean();
			}
		}
		return false;
	}

	/**
	 * Retrieves the "onLastResort" configuration value from the connector's detection section.
	 *
	 * @return The "onLastResort" configuration value as a string, or {@code null} if not configured.
	 */
	public String getOnLastResort() {
		final JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode onLastResort = detection.get("onLastResort"); // NOSONAR JsonNodeHelper.nonNull() is already called
			if (JsonNodeHelper.nonNull(onLastResort)) {
				return onLastResort.asText();
			}
		}
		return null;
	}

	/**
	 * Retrieves the detection criteria as a list of {@link JsonNode} objects from the connector's detection section.
	 *
	 * @return A list of {@link JsonNode} objects representing the detection criteria, or an empty list if not configured.
	 */
	public List<JsonNode> getCriteria() {
		final JsonNode detectionCriteria = getDetectionCriteria();

		if (JsonNodeHelper.nonNull(detectionCriteria) && detectionCriteria.isArray()) { // NOSONAR JsonNodeHelper.nonNull() is already called
			return JsonNodeHelper.stream((ArrayNode) detectionCriteria).collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	/**
	 * Retrieves the metric keys from the specified monitor node.
	 *
	 * @param monitor The monitor node as a {@link JsonNode}.
	 * @return A {@link Set} containing the metric keys.
	 */
	public Set<String> getMonitorMetrics(final JsonNode monitor) {
		final Set<String> metricKeys = new HashSet<>();

		collectMetrics(metricKeys, getMonitorJobs(monitor));

		return metricKeys;
	}

	/**
	 * Retrieves the attribute keys from the specified monitor node.
	 *
	 * @param monitor The monitor node as a {@link JsonNode}.
	 * @return A {@link Set} containing the attribute keys.
	 */
	public Set<String> getMonitorAttributes(final JsonNode monitor) {
		final Set<String> attributeKeys = new HashSet<>();

		collectAttributes(attributeKeys, getMonitorJobs(monitor));

		return attributeKeys;
	}

	/**
	 * Retrieves the monitor's jobs from the specified monitor node.
	 *
	 * @param monitor The monitor node as a {@link JsonNode}.
	 * @return An array containing the monitor's jobs
	 */
	public JsonNode[] getMonitorJobs(final JsonNode monitor) {
		final JsonNode discovery = monitor.get("discovery");
		final JsonNode collect = monitor.get("collect");
		final JsonNode simple = monitor.get("simple");

		return new JsonNode[] { discovery, collect, simple };
	}

	/**
	 * Collects attributes from the specified monitor jobs and adds them to the set of attribute keys.
	 *
	 * @param attributeKeys The set to which attribute keys will be added.
	 * @param jobs          The monitor jobs as an array of {@link JsonNode}.
	 */
	private void collectAttributes(final Set<String> attributeKeys, final JsonNode... jobs) {
		Stream
			.of(jobs)
			.filter(JsonNodeHelper::nonNull)
			.forEach(job -> {
				final JsonNode mapping = getMapping(job);
				if (!JsonNodeHelper.nonNull(mapping)) {
					return;
				}

				final JsonNode attributes = mapping.get("attributes");
				if (!JsonNodeHelper.nonNull(attributes)) {
					return;
				}

				attributes
					.fieldNames()
					.forEachRemaining(key -> {
						if (skipKey(key)) {
							return;
						}
						attributeKeys.add(key);
					});
			});
	}

	/**
	 * Checks if the key should be skipped based on a specific condition.
	 *
	 * @param key The key to check.
	 * @return {@code true} if the key should be skipped, {@code false} otherwise.
	 */
	private boolean skipKey(final String key) {
		return key.startsWith("__");
	}

	/**
	 * Collects metrics from the specified monitor jobs, manages state metrics, and adds them to the set of metric keys.
	 *
	 * @param metricKeys The set to which metric keys will be added.
	 * @param jobs       The monitor jobs as an array of {@link JsonNode}.
	 */
	private void collectMetrics(final Set<String> metricKeys, final JsonNode... jobs) {
		Stream
			.of(jobs)
			.filter(JsonNodeHelper::nonNull)
			.forEach(job -> {
				final JsonNode mapping = getMapping(job);
				if (!JsonNodeHelper.nonNull(mapping)) {
					return;
				}

				final JsonNode metric = mapping.get("metrics");
				if (!JsonNodeHelper.nonNull(metric)) {
					return;
				}

				metric
					.fieldNames()
					.forEachRemaining(key -> {
						if (skipKey(key)) {
							return;
						}

						// Manage state metrics
						final String metricName = includeStatesInMetricName(key);

						metricKeys.add(metricName);
					});
			});
	}

	/**
	 * Includes states in the metric name if defined in the metric definitions.
	 *
	 * @param metricName The original metric name.
	 * @return The updated metric name with states included.
	 */
	private String includeStatesInMetricName(final String metricName) {
		String name = metricName;
		// The metric name already contains the state attribute. No need to update the name
		if (STATE_METRIC_PATTERN.matcher(metricName).matches()) {
			return name;
		}

		final JsonNode metricDefinitions = connector.get("metrics");
		// No metric definitions?
		if (!JsonNodeHelper.nonNull(metricDefinitions)) {
			return name;
		}

		final String metricNameWithoutAttributes = extractMetricName(name);

		final JsonNode metricDefinition = metricDefinitions.get(metricNameWithoutAttributes);

		// No metric definition?
		if (!JsonNodeHelper.nonNull(metricDefinition)) {
			return name;
		}

		final JsonNode type = getType(metricDefinition);

		// Check the type object. Must be non null and object defining state set
		if (!JsonNodeHelper.nonNull(metricDefinition) || !type.isObject()) {
			return name;
		}

		final JsonNode stateSet = type.get("stateSet");
		final List<String> stateSetList = JsonNodeHelper.nodeToStringList(stateSet);
		if (!stateSet.isEmpty()) {
			// Include the state values in the metric name
			Collections.sort(stateSetList);
			final String states = String.join("|", stateSetList);
			if (metricNameWithoutAttributes.equals(name)) {
				name = String.format("%s{state=\"%s\"}", name, states);
			} else if (name.endsWith("}")) {
				name = name.replace("}", String.format(", state=\"%s\"}", states));
			}
		}

		return name;
	}

	/**
	 * This method removes attribute parts from the metric name
	 *
	 * @param name metric name with or without attributes
	 *
	 * @return metric name without attributes
	 */
	public static final String extractMetricName(final String name) {
		// Use a Matcher to find the pattern in the input string
		final Matcher matcher = METRIC_NAME_PATTERN.matcher(name);

		// If the pattern is found, replace it with an empty string; otherwise, return the original string
		return matcher.find() ? matcher.replaceFirst("$1").trim() : name.trim();
	}

	/**
	 * Checks if there is a blade monitor job with mapping directives.
	 *
	 * @return {@code true} if there is a blade monitor job with mapping directives, otherwise {@code false}.
	 */
	public boolean hasBladeMonitorJob() {
		final JsonNode monitors = getMonitors().orElse(null);
		if (JsonNodeHelper.nonNull(monitors)) {
			final JsonNode bladeMonitorJob = monitors.get("blade"); // NOSONAR JsonNodeHelper.nonNull() is already called
			if (JsonNodeHelper.nonNull(bladeMonitorJob)) {
				final JsonNode[] bladeJobs = getMonitorJobs(bladeMonitorJob);
				for (JsonNode bladeJob : bladeJobs) {
					if (JsonNodeHelper.nonNull(bladeJob) && JsonNodeHelper.nonNull(getMapping(bladeJob))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves the mapping information from the given monitor job.
	 *
	 * @param job The monitor job.
	 * @return The mapping information as a {@link JsonNode}, or {@code null} if not available.
	 */
	private JsonNode getMapping(final JsonNode job) {
		return job.get("mapping");
	}

	/**
	 * Retrieves and adds a specified tag to the detection JSON node's "tags" list.
	 * <p>
	 * Adds either "enterprise" or "community" to the "tags" field based on the {@code isEnterprise} parameter.
	 * If the "tags" field is absent or null, it initializes a new array with the specified tag.
	 * </p>
	 *
	 * @param isEnterprise {@code true} to add "enterprise" to the tags; {@code false} to add "community".
	 * @return a list of tags as strings, including the added tag, or an empty list if the detection node is null.
	 */
	public List<String> getAndCompleteTags(final boolean isEnterprise) {
		JsonNode detection = getDetection();
		if (JsonNodeHelper.nonNull(detection)) {
			final JsonNode tagsNode = detection.get("tags"); // NOSONAR JsonNodeHelper.nonNull() is already called
			final ArrayNode tagsArrayNode = tagsNode != null && !tagsNode.isNull()
				? (ArrayNode) tagsNode
				: JsonNodeFactory.instance.arrayNode();

			tagsArrayNode.add(isEnterprise ? "enterprise" : "community");

			((ObjectNode) detection).set("tags", tagsArrayNode);
			return JsonNodeHelper.nodeToStringList(tagsArrayNode);
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves a list of tags from the detection JSON node.
	 *
	 * @return a list of tags as strings, or an empty list if no tags are found.
	 */
	public List<String> getTags() {
		JsonNode detection = getDetection();
		return JsonNodeHelper.nonNull(detection)
			? JsonNodeHelper.nodeToStringList(detection.get("tags")) // NOSONAR JsonNodeHelper.nonNull() is already called
			: Collections.emptyList();
	}

	/**
	 * Retrieves all variable names from the connector template.
	 * Variables are expected to be in the format: ${var::variableName}.
	 *
	 * @return a set of unique variable names found within the connector template.
	 */
	public Set<String> getVariablesNames() {
		final String stringConnector = connector.toString();
		final Set<String> variables = new HashSet<>();

		final Matcher matcher = CONNECTOR_VARIABLE_PATTERN.matcher(stringConnector);

		while (matcher.find()) {
			variables.add(matcher.group(1));
		}
		return variables;
	}

	/**
	 * Retrieves the default connector variables declared in the connector.
	 * These variables include their descriptions and default values.
	 *
	 * @return a map of variable names to their corresponding {@link ConnectorDefaultVariable} objects,
	 *         each containing a description and a default value. Returns an empty map if no variables are declared.
	 */
	public Map<String, ConnectorDefaultVariable> getDefaultVariables() {
		final JsonNode variablesNode = getConnectorSection().map(node -> node.get("variables")).orElse(null);

		final Map<String, ConnectorDefaultVariable> defaultVariables = new HashMap<>();
		if (JsonNodeHelper.nonNull(variablesNode)) {
			variablesNode // NOSONAR JsonNodeHelper.nonNull() is already called
				.fields()
				.forEachRemaining(entry -> {
					final String variableName = entry.getKey();
					final JsonNode variableValue = entry.getValue();

					final JsonNode description = variableValue.get("description");
					final JsonNode defaultValue = variableValue.get("defaultValue");

					// Create a ConnectorDefaultVariable object and put it into the map
					final ConnectorDefaultVariable connectorDefaultVariable = new ConnectorDefaultVariable(
						JsonNodeHelper.nonNullTextOrDefault(description, null),
						JsonNodeHelper.nonNullTextOrDefault(defaultValue, null)
					);
					defaultVariables.put(variableName, connectorDefaultVariable);
				});
		}
		return defaultVariables;
	}

	/**
	 * Retrieves the relative path of the connector that was saved during parsing.
	 * This path is used to generate a link to the connector's source code.
	 *
	 * @return The saved relative path as a string.
	 */
	public String getRelativePath() {
		return connector.get("relativePath").asText();
	}
}
