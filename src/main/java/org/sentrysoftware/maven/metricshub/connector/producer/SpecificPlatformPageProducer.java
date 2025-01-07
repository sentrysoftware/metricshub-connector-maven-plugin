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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.producer.model.platform.Platform;

/**
 * Utility class for producing platform page related to connectors.
 */
public class SpecificPlatformPageProducer extends AbstractGroupConnectorsProducer {

	/**
	 * Constructor for the tag page producer.
	 *
	 * @param logger   The logger used for logging.
	 */
	public SpecificPlatformPageProducer(Log logger) {
		super(logger);
	}

	/**
	 * Produces the tag page report that lists all the connectors.
	 *
	 * @param sink                      The sink used for generating content.
	 * @param platform                  The platform to be listed as part of the report.
	 * @param connectorSubdirectoryName The connector subdirectory name.
	 * @param enterpriseConnectorIds    The enterprise connector identifiers.
	 */
	public void produce(
		final Sink sink,
		final Platform platform,
		final String connectorSubdirectoryName,
		final List<String> enterpriseConnectorIds
	) {
		Objects.requireNonNull(platform, () -> "platform cannot be null.");
		Objects.requireNonNull(sink, () -> "sink cannot be null.");
		Objects.requireNonNull(logger, () -> "logger cannot be null.");

		logger.debug("Generating Platform Page: " + SinkHelper.buildPageFilename(platform.getId()));

		final String displayName = platform.getDisplayName();
		final Map<String, JsonNode> connectors = platform.getConnectors();

		buildHeadAndBody(sink, connectorSubdirectoryName, enterpriseConnectorIds, displayName, connectors);
	}

	@Override
	protected String getIntroductionText(final String title) {
		return String.format("Discover all the available connectors related to the <code>%s</code> platform.", title);
	}
}
