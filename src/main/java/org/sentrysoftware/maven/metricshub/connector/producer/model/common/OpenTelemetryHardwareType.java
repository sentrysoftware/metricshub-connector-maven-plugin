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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Enumeration representing different OpenTelemetry hardware monitor types. See
 * <a href=
 * "https://opentelemetry.io/docs/specs/semconv/system/hardware-metrics/">Semantic
 * Conventions for Hardware Metrics</a>
 */
@Getter
@AllArgsConstructor
public enum OpenTelemetryHardwareType {
	/**
	 * Host
	 */
	HOST("host"),
	/**
	 * Battery
	 */
	BATTERY("battery"),
	/**
	 * Blade Chassis
	 */
	BLADE("blade"),
	/**
	 * CPU
	 */
	CPU("cpu"),
	/**
	 * Disk Controller
	 */
	DISK_CONTROLLER("disk_controller"),
	/**
	 * Enclosure
	 */
	ENCLOSURE("enclosure"),
	/**
	 * Fan
	 */
	FAN("fan"),
	/**
	 * GPU
	 */
	GPU("gpu"),
	/**
	 * LED
	 */
	LED("led"),
	/**
	 * Logical Disk
	 */
	LOGICAL_DISK("logical_disk"),
	/**
	 * Memory Module
	 */
	MEMORY("memory"),
	/**
	 * Network Device
	 */
	NETWORK("network"),
	/**
	 * Other Device
	 */
	OTHER_DEVICE("other_device"),
	/**
	 * Physical Disk
	 */
	PHYSICAL_DISK("physical_disk"),
	/**
	 * Power Supply
	 */
	POWER_SUPPLY("power_supply"),
	/**
	 * Robotics
	 */
	ROBOTICS("robotics"),
	/**
	 * Tape Drive
	 */
	TAPE_DRIVE("tape_drive"),
	/**
	 * Temperature
	 */
	TEMPERATURE("temperature"),
	/**
	 * Voltage
	 */
	VOLTAGE("voltage");

	private String key;

	/**
	 * Set of hardware monitor types
	 */
	public static final Set<String> MONITOR_TYPES = Stream
		.of(values())
		.map(OpenTelemetryHardwareType::getKey)
		.collect(Collectors.toSet());

	/**
	 * Checks whether the given string represents a hardware monitor type.
	 * <p>
	 * This method performs a case-insensitive and whitespace-trimmed comparison with the predefined set
	 * of hardware monitor types.
	 * </p>
	 *
	 * @param type The string to be checked for hardware monitor type.
	 * @return {@code true} if the provided string is a hardware monitor type, {@code false} otherwise.
	 * @throws IllegalArgumentException if the provided value is {@code null}.
	 */
	public static boolean isHardwareMonitorType(@NonNull final String type) {
		return MONITOR_TYPES.contains(type.trim().toLowerCase());
	}
}
