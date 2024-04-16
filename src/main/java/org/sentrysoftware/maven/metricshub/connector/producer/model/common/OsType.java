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

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Enumeration representing different operating system types.
 */
@AllArgsConstructor
public enum OsType {
	/**
	 * HP OpenVMS
	 */
	VMS("HP OpenVMS", "vms"),

	/**
	 * HP Tru64
	 */
	TRU64("HP Tru64", "tru64"),

	/**
	 * HP-UX
	 */
	HPUX("HP-UX", "hp"),

	/**
	 * IBM AIX
	 */
	AIX("IBM AIX", "aix"),

	/**
	 * Linux
	 */
	LINUX("Linux", "linux"),

	/**
	 * Out-Of-Band
	 */
	OOB("Out-Of-Band", "management"),

	/**
	 * Microsoft Windows
	 */
	WINDOWS("Microsoft Windows", "win"),

	/**
	 * Network Device
	 */
	NETWORK("Network Device", "network"),

	/**
	 * Storage System
	 */
	STORAGE("Storage System", "storage"),

	/**
	 * Oracle Solaris
	 */
	SOLARIS("Oracle Solaris", "solaris");

	@Getter
	private String displayName;

	@Getter
	private String possibleHostType;

	/**
	 * Maps each OsType to a compiled representation of the regular expression that detects it.
	 */
	private static final Map<OsType, Pattern> DETECTORS = Map.ofEntries(
		new SimpleEntry<>(LINUX, Pattern.compile("^lin$|^linux$")),
		new SimpleEntry<>(WINDOWS, Pattern.compile("^(microsoft\\s*)?windows$|^win$|^nt$")),
		new SimpleEntry<>(
			OOB,
			Pattern.compile("^management$|^mgmt$|^management\\s*card$|^out-of-band$|^out\\s*of\\s*band$|^oob$")
		),
		new SimpleEntry<>(NETWORK, Pattern.compile("^network$|^switch$")),
		new SimpleEntry<>(STORAGE, Pattern.compile("^storage$|^san$|^library$|^array$")),
		new SimpleEntry<>(VMS, Pattern.compile("^vms$|^(hp\\s*)?open\\s*vms$")),
		new SimpleEntry<>(TRU64, Pattern.compile("^tru64$|^osf1$|^hp\\s*tru64\\s*unix$")),
		new SimpleEntry<>(HPUX, Pattern.compile("^hp-ux$|^hpux$|^hp$")),
		new SimpleEntry<>(AIX, Pattern.compile("^ibm(\\s*|-)aix$|^aix$|^rs6000$")),
		new SimpleEntry<>(SOLARIS, Pattern.compile("^((sun|oracle)\\s*)?solaris$|^sunos$"))
	);

	/**
	 * Detects the {@link OsType} display name using the provided value.
	 *
	 * @param value The value to process.
	 * @return The display name of the detected OS type, or the original value if no match is found.
	 */
	public static String detectDisplayName(final String value) {
		// Null? returns null
		if (value == null) {
			return value;
		}

		// Check all regex in DETECTORS to see which one matches
		return detect(value).map(OsType::getDisplayName).orElse(value);
	}

	/**
	 * Detects the operating system type based on the provided string value.
	 *
	 * @param value The string value used for operating system detection.
	 * @return An {@code Optional} containing the detected {@code OsType}, or empty if no match is found.
	 * @throws IllegalArgumentException If the provided value is null.
	 */
	public static Optional<OsType> detect(@NonNull final String value) {
		final String lCaseValue = value.trim().toLowerCase();
		for (Map.Entry<OsType, Pattern> detector : DETECTORS.entrySet()) {
			if (detector.getValue().matcher(lCaseValue).find()) {
				return Optional.of(detector.getKey());
			}
		}
		return Optional.empty();
	}

	/**
	 * Converts a list of operating system types to a list of operating system display names.
	 *
	 * @param osTypeValues The list of operating system types.
	 * @return A list of String values based on the provided list of operating system types.
	 */
	public static List<String> mapToDisplayNames(final List<String> osTypeValues) {
		return osTypeValues.stream().map(OsType::detectDisplayName).filter(Objects::nonNull).collect(Collectors.toList());
	}
}
