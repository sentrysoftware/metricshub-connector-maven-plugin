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

import static org.sentrysoftware.maven.metricshub.connector.Constants.BOOTSTRAP_MEDIUM_3_CLASS;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.OsType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;

/**
 * Abstract class for producing pages.
 */
@AllArgsConstructor
public abstract class AbstractPageProducer {

	protected Log logger;

	/**
	 * Builds the table header row.
	 *
	 * @param sink The sink used for generating content.
	 */
	private void buildTableHeaderRow(final Sink sink) {
		sink.tableRow();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Name");
		sink.tableHeaderCell_();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Connector ID");
		sink.tableHeaderCell_();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Platform");
		sink.tableHeaderCell_();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Operating Systems");
		sink.tableHeaderCell_();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Technology/Protocols");
		sink.tableHeaderCell_();
		sink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		sink.text("Enterprise");
		sink.tableHeaderCell_();
		sink.tableRow_();
	}

	/**
	 * Builds the table of connectors.
	 *
	 * @param sink                      The sink used for generating content.
	 * @param connectors                The map of connector identifiers to their corresponding JsonNodes.
	 * @param connectorSubdirectoryName The connector subdirectory name.
	 * @param enterpriseConnectorIds    The enterprise connector identifiers.
	 * @param isTagPage                 Whether the page is a tag page.
	 */
	protected void buildConnectorsTable(
		final Sink sink,
		final Map<String, JsonNode> connectors,
		final String connectorSubdirectoryName,
		final List<String> enterpriseConnectorIds,
		final boolean isTagPage
	) {
		// Create the table
		sink.table();

		// Table header
		buildTableHeaderRow(sink);

		// A comparison function which compare connectors by display name
		final Comparator<Entry<String, JsonNode>> comparator = (e1, e2) ->
			new ConnectorJsonNodeReader(e1.getValue())
				.getDisplayName()
				.toLowerCase()
				.compareTo(new ConnectorJsonNodeReader(e2.getValue()).getDisplayName().toLowerCase());

		connectors
			.entrySet()
			.stream()
			.sorted(comparator)
			.forEach(connectorEntry -> {
				final JsonNode connector = connectorEntry.getValue();
				final String connectorId = connectorEntry.getKey();

				final ConnectorJsonNodeReader connectorJsonNodeReader = new ConnectorJsonNodeReader(connector);

				// Builds the HTML page file name corresponding to the specified connector identifier
				final String pageFilename = SinkHelper.buildPageFilename(connectorId);

				// Builds the HTML page path name corresponding to the specified connector page filename
				final String connectorPagePath = String.format(
					isTagPage ? "../../%s/%s" : "%s/%s",
					connectorSubdirectoryName,
					pageFilename
				);

				// Add a row to the table in the main page
				sink.tableRow();

				sink.tableCell();

				sink.link(connectorPagePath);
				sink.text(connectorJsonNodeReader.getDisplayName());
				sink.link_();
				sink.tableCell_();

				sink.tableCell();
				sink.link(connectorPagePath);
				sink.text(connectorId);
				sink.link_();
				sink.tableCell_();

				sink.tableCell();
				sink.text(SinkHelper.replaceCommaWithSpace(connectorJsonNodeReader.getPlatformsOrDefault("N/A")));
				sink.tableCell_();

				sink.tableCell();
				sink.text(String.join(", ", OsType.mapToDisplayNames(connectorJsonNodeReader.getAppliesTo())));
				sink.tableCell_();

				sink.tableCell();
				final Set<TechnologyType> technologies = connectorJsonNodeReader.getTechnologies();
				for (final TechnologyType technology : technologies) {
					sink.text(technology.getDisplayName());
					sink.lineBreak();
				}

				sink.tableCell_();

				SinkEventAttributes attributes = new SinkEventAttributeSet(SinkEventAttributes.ALIGN, "center");
				sink.tableCell(attributes);
				sink.text(enterpriseConnectorIds.contains(connectorId) ? "\u2713" : "");
				sink.tableCell_();

				sink.tableRow_();
			});

		sink.table_();
	}
}
