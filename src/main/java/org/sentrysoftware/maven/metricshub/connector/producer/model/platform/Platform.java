package org.sentrysoftware.maven.metricshub.connector.producer.model.platform;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import org.sentrysoftware.maven.metricshub.connector.producer.SinkHelper;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;

/**
 * MetricsHub Connector Platform implementation.
 */
public class Platform {

	/**
	 * Comparator to sort the technology types by display name.
	 */
	private static final Comparator<? super TechnologyType> COMPARATOR = (p1, p2) ->
		p1.getDisplayName().compareTo(p2.getDisplayName());

	/**
	 * The unique identifier of the platform
	 */
	@Getter
	private String id;

	/**
	 * The supported platform name (as specified by the connector `platforms` property)
	 */
	@Getter
	private String displayName;

	/**
	 * The icon path of the platform
	 */
	@Getter
	private String iconPath;

	/**
	 * The technology types supported by the platform
	 */
	private Set<TechnologyType> technologies = new TreeSet<>(COMPARATOR);

	/**
	 * Connectors associated with this platform
	 */
	private Map<String, JsonNode> connectors = new LinkedHashMap<>();

	/**
	 * Constructor for the Platform class.
	 *
	 * @param id           The unique identifier of the platform.
	 * @param displayName  The name of the platform.
	 * @param iconPath     The icon path of the platform.
	 */
	public Platform(final String id, final String displayName, final String iconPath) {
		this.id = id;
		this.displayName = displayName;
		this.iconPath = iconPath;
	}

	/**
	 * Adds a connector to the platform.
	 * @param connectorId The connector unique identifier.
	 * @param connector   The connector as a {@link JsonNode}.
	 */
	public void addConnector(final String connectorId, final JsonNode connector) {
		connectors.put(connectorId, connector);
	}

	/**
	 * Adds technology types to the platform.
	 * @param technologies The technology types to add.
	 */
	public void addTechnologies(final Set<TechnologyType> technologies) {
		this.technologies.addAll(technologies);
	}

	/**
	 * Formats the platforms of a connector.
	 *
	 * @param platforms The platforms to format.
	 * @return A string representation of the platforms.
	 */
	public static String formatPlatforms(final Set<String> platforms) {
		return platforms.stream().map(SinkHelper::replaceCommaWithSpace).collect(Collectors.joining(", "));
	}

	/**
	 * Gets the technology types supported by the platform.
	 *
	 * @return The technology types supported by the platform.
	 */
	public Set<TechnologyType> getTechnologies() {
		return Collections.unmodifiableSet(technologies);
	}

	/**
	 * Gets the connectors associated with this platform.
	 *
	 * @return The connectors associated with this platform.
	 */
	public Map<String, JsonNode> getConnectors() {
		return Collections.unmodifiableMap(connectors);
	}
}
