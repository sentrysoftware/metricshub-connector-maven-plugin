package org.sentrysoftware.maven.metricshub.connector;

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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.MavenReportException;
import org.sentrysoftware.maven.metricshub.connector.producer.ConnectorJsonNodeReader;
import org.sentrysoftware.maven.metricshub.connector.producer.PlatformReportProducer;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.OsType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.platform.Platform;

/**
 * Builds the Platform Requirements page (platform-requirements.html) based
 * on the content of the connector files.
 *
 * <p>
 * This plugin goal is a report goal that works in the <em>site</em> build lifecycle. It
 * simply needs to be declared in the report section of the pom.xml.
 * </p>
 * <p>
 * The goal actually reads the .yaml files (Connectors) located in ${sourceDirectory} and
 * produces one HTML Web page that is decorated using the specified Maven skin in
 * site.xml.
 * </p>
 *
 */
@Mojo(
	name = "metricshub-connector-platforms",
	aggregator = false,
	defaultPhase = LifecyclePhase.SITE,
	requiresDependencyResolution = ResolutionScope.RUNTIME,
	requiresOnline = false,
	requiresProject = true,
	threadSafe = true
)
public class PlatformReport extends AbstractConnectorReport {

	@Override
	public String getOutputName() {
		return "platform-requirements";
	}

	@Override
	public String getName(Locale locale) {
		return "Supported Platform and Requirements";
	}

	@Override
	public String getDescription(Locale locale) {
		return "List of all supported platforms by the MetricsHub Connectors in ${project.name} ${project.version}, grouped by system type.";
	}

	@Override
	protected void doReport() throws MavenReportException {
		// Some info
		logger.info(
			String.format(
				"Generating %s.html for %s %s from %s",
				getOutputName(),
				project.getDescription(),
				project.getVersion(),
				sourceDirectory
			)
		);

		new PlatformReportProducer(logger).produce(getMainSink(), determineOsPlatforms());
	}

	/**
	 * Determine the list the platforms covered by the connectors collectively.
	 *
	 * @return {@link Map} of {@link Platform} instances indexed by OS.
	 */
	private Map<String, List<Platform>> determineOsPlatforms() {
		final Map<String, List<Platform>> osPlatforms = new HashMap<>();

		for (Entry<String, JsonNode> connectorEntry : connectors.entrySet()) {
			final String connectorId = connectorEntry.getKey();
			final JsonNode connector = connectorEntry.getValue();

			// Create a new reader to fetch connector information
			final ConnectorJsonNodeReader connectorJsonNodeReader = new ConnectorJsonNodeReader(connector);

			final List<String> osList = OsType.mapToDisplayNames(connectorJsonNodeReader.getAppliesTo());

			for (String os : osList) {
				final String osToDisplay;
				final String displayName = connectorJsonNodeReader.getDisplayName();
				if (os.equalsIgnoreCase(OsType.OOB.getDisplayName()) && connectorJsonNodeReader.hasBladeMonitorJob()) {
					// Special case for OOB: if the connector discovers blade instances, we'll say it's a Blade Chassis
					osToDisplay = "Blade Chassis";
				} else if (displayName.toLowerCase().contains("vmware")) {
					// Special case for VMware
					osToDisplay = "VMware ESX";
				} else {
					osToDisplay = os;
				}

				// If the specified osToDisplay key is not already associated with a list of platforms,
				// attempt to compute its value using a new array list.
				final List<Platform> platformList = osPlatforms.computeIfAbsent(osToDisplay, k -> new ArrayList<>());

				// Create a new platform and attempt to find the same platform in the platform list
				final Platform newPlatform = new Platform(
					connectorJsonNodeReader.getPlatformsOrDefault("N/A"),
					osToDisplay,
					connectorJsonNodeReader
						.getTechnologies()
						.stream()
						.map(TechnologyType::getDisplayName)
						.collect(Collectors.joining(", "))
				);

				final Optional<Platform> maybePlaform = platformList
					.stream()
					.filter(existingPlatform -> existingPlatform.equals(newPlatform))
					.findFirst();

				final String reliesOn = connectorJsonNodeReader.getReliesOnOrDefault("N/A");

				// Found?
				if (maybePlaform.isPresent()) {
					maybePlaform.get().addConnectorInformation(connectorId, displayName, reliesOn);
				} else {
					newPlatform.addConnectorInformation(connectorId, displayName, reliesOn);
					platformList.add(newPlatform);
				}
			}
		}
		return osPlatforms;
	}
}
