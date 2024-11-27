package org.sentrysoftware.maven.metricshub.connector.producer.model.common;

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

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing different technology types such as HTTP, IPMI, SNMP, WMI, WBEM, Command Lines, etc.
 */
@AllArgsConstructor
public enum TechnologyType {
	/**
	 * HTTP/REST
	 */
	HTTP("HTTP/REST"),

	/**
	 * Intelligent Platform Management Interface (IPMI)
	 */
	IPMI("IPMI"),

	/**
	 * Command Lines
	 */
	COMMAND_LINES("Command Lines"),

	/**
	 * Simple Network Management Protocol (SNMP)
	 */
	SNMP("SNMP"),

	/**
	 * Web-Based Enterprise Management (WBEM)
	 */
	WBEM("WBEM"),

	/**
	 * Windows Management Instrumentation (WMI) / Windows Remote Management (WinRM)
	 */
	WMI("WMI/WinRM"),

	/**
	 * SQL
	 */
	SQL("SQL");

	@Getter
	private String displayName;

	/**
	 * Mapping of connector source types to corresponding technology types.
	 * <p>
	 * This static map associates connector source types (such as "http", "ipmi", etc.) with their
	 * corresponding {@link TechnologyType}. This map is used to identify the technology type based on
	 * the source type when processing connector technology.
	 * </p>
	 */
	// @formatter:off
	private static final Map<String, TechnologyType> TECHNOLOGY_TYPE_MAP = Map.of(
		"http", HTTP,
		"ipmi", IPMI,
		"oscommand", COMMAND_LINES,
		"commandline", COMMAND_LINES,
		"snmptable", SNMP,
		"snmpget", SNMP,
		"wbem", WBEM,
		"wmi", WMI,
		"sql", SQL
	);

	// @formatter:on

	/**
	 * Retrieves the technology type associated with the provided source key.
	 * <p>
	 * This method looks up the {@link TechnologyType} associated with the given source type
	 * in the {@link #TECHNOLOGY_TYPE_MAP}. The source key is case-insensitive and trimmed before the lookup.
	 *
	 * @param sourceType The source type for which to retrieve the corresponding {@link TechnologyType}.
	 * @return An {@link Optional} containing the associated {@link TechnologyType}, or an empty {@link Optional}
	 *         if the technology source key is not found in the map.
	 */
	public static Optional<TechnologyType> getTechnologyType(final String sourceType) {
		if (sourceType == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(TECHNOLOGY_TYPE_MAP.get(sourceType.trim().toLowerCase()));
	}
}
