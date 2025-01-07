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
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.Constants;

/**
 * Abstract class for producing pages that group connectors.
 */
public abstract class AbstractGroupConnectorsProducer extends AbstractPageProducer {

	/**
	 * Constructor for the AbstractGroupConnectorsProducer.
	 *
	 * @param logger The logger used for logging.
	 */
	protected AbstractGroupConnectorsProducer(Log logger) {
		super(logger);
	}

	/**
	 * Builds the head and body of the page.
	 *
	 * @param sink                      The sink used for generating content
	 * @param connectorSubdirectoryName The connector subdirectory name
	 * @param enterpriseConnectorIds    The enterprise connector identifiers
	 * @param title                     The title of the page
	 * @param connectors                The map of connector identifiers to their corresponding JsonNodes
	 */
	protected void buildHeadAndBody(
		final Sink sink,
		final String connectorSubdirectoryName,
		final List<String> enterpriseConnectorIds,
		final String title,
		final Map<String, JsonNode> connectors
	) {
		// Create the head element of the page
		buildHead(sink, title);

		sink.body();

		// Links to the main page and full listing
		ConnectorPageProducer.backLinks(
			sink,
			String.format("../../%s", Constants.CONNECTORS_DIRECTORY_OUTPUT_FILE_NAME),
			String.format("../../%s", Constants.CONNECTORS_FULL_LISTING_FILE_NAME)
		);

		// Title
		sink.section1();
		sink.sectionTitle1();
		sink.text(title);
		sink.sectionTitle1_();

		sink.paragraph();
		sink.rawText(getIntroductionText(title));
		sink.paragraph_();

		// Table of connectors
		buildConnectorsTable(sink, connectors, connectorSubdirectoryName, enterpriseConnectorIds, true);

		// Close the page
		sink.section1_();
		sink.body_();
		sink.close();
	}

	/**
	 * Returns the introduction text for the group page.
	 *
	 * @param title The category.
	 * @return The introduction text.
	 */
	protected abstract String getIntroductionText(String title);
}
