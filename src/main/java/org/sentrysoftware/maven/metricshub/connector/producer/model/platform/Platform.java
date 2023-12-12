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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Platform to MetricsHub Connector implementation
 * It is made of { os ; platform ; technology }.
 */
public class Platform implements Comparable<Platform> {

	/**
	 * The supported platform name (as specified by the connector `platforms` property)
	 */
	@Getter
	private final String name;

	/**
	 * The operating system for this platform (Linux, Microsoft Windows, etc.)
	 */
	@Getter
	private final String os;

	/**
	 * The type of connection we're using to support this platform (SNMP, WBEM, SSH, etc.)
	 */
	@Getter
	private final String technology;

	/**
	 * Connector ID to display name mapping
	 */
	private final Map<String, String> connectors = new LinkedHashMap<>();

	/**
	 * All the prerequisites for this platform
	 */
	private final Set<String> prerequisites = new LinkedHashSet<>();

	private final int hashCode;

	/**
	 * Constructs a new instance of the platform
	 *
	 * @param name       The supported platform name (as specified by the connector `platforms` property)
	 * @param os         The operating system for this platform (Linux, Microsoft Windows, etc.)
	 * @param technology The type of connection we're using to support this platform (SNMP, WBEM, SSH, etc.)
	 */
	public Platform(String name, String os, String technology) {
		this.name = name;
		this.os = os;
		this.technology = technology;

		// The hash code needs to be computed only once to avoid calling String.toLowerCase many times.
		this.hashCode = lowerCaseHashCode(name, os, technology);
	}

	/**
	 * Calculates the hashCode code of the concatenated lowercased strings.
	 *
	 * @param elements The elements to be concatenated and hashed.
	 * @return The hashCode code of the concatenated lowercased strings.
	 */
	private int lowerCaseHashCode(final String... elements) {
		int result = 1;

		for (String element : elements) {
			result = 31 * result + (element != null ? element.toLowerCase().hashCode() : 0);
		}

		return result;
	}

	/**
	 * We're sorting Platforms by putting entries that starts with "Any " at the end of the list
	 *
	 * @param other the {@link Platform} to be compared.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Platform other) {
		if (other == null) {
			return 1;
		}

		int comparison = this.os.compareToIgnoreCase(other.os);
		if (comparison == 0) {
			// Special case for platforms that start with "Any "
			if (this.name.toLowerCase().startsWith("any ")) {
				if (other.name.toLowerCase().startsWith("any ")) {
					comparison = this.name.compareToIgnoreCase(other.name);
				} else {
					comparison = 1;
				}
			} else if (other.name.toLowerCase().startsWith("any ")) {
				comparison = -1;
			} else {
				comparison = this.name.compareToIgnoreCase(other.name);
			}
		}

		return comparison;
	}

	/**
	 * Adds information about a connector, including its identifier, display name, and a prerequisite.
	 *
	 * @param connectorId  The identifier of the connector.
	 * @param displayName  The display name of the connector.
	 * @param reliesOn     Considered as the technical prerequisites for this connector.
	 */
	public void addConnectorInformation(final String connectorId, final String displayName, final String reliesOn) {
		connectors.put(connectorId, displayName);
		prerequisites.add(reliesOn);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Platform other = (Platform) obj;
		// @formatter:off
		return
			this.name.equalsIgnoreCase(other.name) &&
			this.os.equalsIgnoreCase(other.os) &&
			this.technology.equalsIgnoreCase(other.technology);
		// @formatter:on
	}

	/**
	 * Gets the connectors associated with the platform.
	 *
	 * @return A {@code Map} containing connector names as keys and their associated display names as values.
	 */
	public Map<String, String> getConnectors() {
		return connectors
			.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
	}

	/**
	 * Gets the prerequisites associated with the platform.
	 *
	 * @return A {@code Set} containing prerequisites.
	 */
	public Set<String> getPrerequisites() {
		return prerequisites.stream().collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
