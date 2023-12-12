package org.sentrysoftware.maven.metricshub.connector.producer.model.criteria;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sentrysoftware.maven.metricshub.connector.producer.JsonNodeHelper;

/**
 * Factory class for creating instances of {@link AbstractCriterion} based on connector criterion types.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CriterionFactory {

	/**
	 * Mapping of connector source types to corresponding criterion creators.
	 * <p>
	 * This static map associates connector criterion types (such as "http", "ipmi", "service", etc.) with their
	 * corresponding {@link CriterionFactory}. The map is used to identify the criterion based on
	 * its string type when processing connector criteria.
	 * </p>
	 *
	 * <p>Each entry in the map consists of a criterion type string and a corresponding function that creates
	 * the specific {@link AbstractCriterion} implementation for that type.</p>
	 * </p>
	 */
	private static final Map<String, Function<JsonNode, AbstractCriterion>> CRITERION_FACTORY_MAP;

	static {
		final Map<String, Function<JsonNode, AbstractCriterion>> map = new HashMap<>();
		map.put("devicetype", CriterionFactory::newDeviceTypeCriterion);
		map.put("http", CriterionFactory::newHttpCriterion);
		map.put("ipmi", CriterionFactory::newIpmiCriterion);
		map.put("oscommand", CriterionFactory::newOsCommandCriterion);
		map.put("process", CriterionFactory::newProcessCriterion);
		map.put("productrequirements", CriterionFactory::newProductRequirementsCriterion);
		map.put("service", CriterionFactory::newServiceCriterion);
		map.put("snmpget", CriterionFactory::newSnmpGetCriterion);
		map.put("snmpgetnext", CriterionFactory::newSnmpGetNextCriterion);
		map.put("wbem", CriterionFactory::newWbemCriterion);
		map.put("wmi", CriterionFactory::newWmiCriterion);

		CRITERION_FACTORY_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * Creates a new {@link DeviceTypeCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link DeviceTypeCriterion} instance.
	 */
	private static DeviceTypeCriterion newDeviceTypeCriterion(final JsonNode node) {
		return new DeviceTypeCriterion(node);
	}

	/**
	 * Creates a new {@link HttpCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link HttpCriterion} instance.
	 */
	private static HttpCriterion newHttpCriterion(final JsonNode node) {
		return new HttpCriterion(node);
	}

	/**
	 * Creates a new {@link IpmiCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link IpmiCriterion} instance.
	 */
	private static IpmiCriterion newIpmiCriterion(final JsonNode node) {
		return new IpmiCriterion(node);
	}

	/**
	 * Creates a new {@link OsCommandCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link OsCommandCriterion} instance.
	 */
	private static AbstractCriterion newOsCommandCriterion(final JsonNode node) {
		return new OsCommandCriterion(node);
	}

	/**
	 * Creates a new {@link ProcessCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link ProcessCriterion} instance.
	 */
	private static ProcessCriterion newProcessCriterion(final JsonNode node) {
		return new ProcessCriterion(node);
	}

	/**
	 * Creates a new {@link ProductRequirementsCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link ProductRequirementsCriterion} instance.
	 */
	private static ProductRequirementsCriterion newProductRequirementsCriterion(final JsonNode node) {
		return new ProductRequirementsCriterion(node);
	}

	/**
	 * Creates a new {@link ServiceCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link ServiceCriterion} instance.
	 */
	private static ServiceCriterion newServiceCriterion(final JsonNode node) {
		return new ServiceCriterion(node);
	}

	/**
	 * Creates a new {@link SnmpGetCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link SnmpGetCriterion} instance.
	 */
	private static AbstractSnmpCriterion newSnmpGetCriterion(final JsonNode node) {
		return new SnmpGetCriterion(node);
	}

	/**
	 * Creates a new {@link SnmpGetNextCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link SnmpGetNextCriterion} instance.
	 */
	private static SnmpGetNextCriterion newSnmpGetNextCriterion(final JsonNode node) {
		return new SnmpGetNextCriterion(node);
	}

	/**
	 * Creates a new {@link WbemCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link WbemCriterion} instance.
	 */
	private static AbstractWqlCriterion newWbemCriterion(final JsonNode node) {
		return new WbemCriterion(node);
	}

	/**
	 * Creates a new {@link WmiCriterion} instance based on the provided {@link JsonNode}.
	 *
	 * @param node The {@link JsonNode} containing criterion configuration.
	 * @return A new {@link WmiCriterion} instance.
	 */
	private static WmiCriterion newWmiCriterion(final JsonNode node) {
		return new WmiCriterion(node);
	}

	/**
	 * Creates an {@link Optional} instance containing an {@link AbstractCriterion} based on the provided {@link JsonNode} criterion type.
	 *
	 * @param criterionNode The {@link JsonNode} containing criterion configuration.
	 * @return An {@link Optional} containing the created {@link AbstractCriterion}, or an empty {@link Optional} if the criterion type is null or not recognized.
	 */
	public static Optional<AbstractCriterion> withJsonNode(final JsonNode criterionNode) {
		final String type = JsonNodeHelper.nonNullTextOrDefault(criterionNode.get("type"), null);
		if (type == null) {
			return Optional.empty();
		}

		return Optional
			.ofNullable(CRITERION_FACTORY_MAP.get(type.trim().toLowerCase()))
			.map(creator -> creator.apply(criterionNode))
			.map(Optional::of)
			.orElse(Optional.empty());
	}
}
