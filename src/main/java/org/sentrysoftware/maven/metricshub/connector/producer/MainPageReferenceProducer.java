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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.ReferenceReport;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.OsType;

/**
 * Utility class for producing main page references.
 */
@AllArgsConstructor
public class MainPageReferenceProducer {

	private final String connectorSubdirectoryName;
	private final String tagSubdirectoryName;
	private final Log logger;

	/**
	 * Produces the main page reference that lists all the connectors.
	 *
	 * @param mainSink               The main sink used for generating content.
	 * @param connectors             The map of connector identifiers to their corresponding JsonNodes.
	 * @param enterpriseConnectorIds The enterprise connector identifiers.
	 */
	public void produce(
		final Sink mainSink,
		final Map<String, JsonNode> connectors,
		final List<String> enterpriseConnectorIds,
		final Set<String> connectorTags
	) {
		Objects.requireNonNull(connectorSubdirectoryName, () -> "connectorSubdirectoryName cannot be null.");
		Objects.requireNonNull(tagSubdirectoryName, () -> "tagSubdirectoryName cannot be null.");
		Objects.requireNonNull(mainSink, () -> "mainSink cannot be null.");
		Objects.requireNonNull(logger, () -> "logger cannot be null.");
		Objects.requireNonNull(connectors, () -> "connectors cannot be null.");
		Objects.requireNonNull(connectorTags, () -> "connectorTags cannot be null.");

		logger.debug(String.format("Generating the main page %s.html", ReferenceReport.CONNECTOR_REFERENCE_OUTPUT_NAME));

		mainSink.head();
		mainSink.title();
		mainSink.text("Connectors Directory");
		mainSink.title_();
		mainSink.head_();

		mainSink.body();

		// Title
		mainSink.section1();
		mainSink.sectionTitle1();
		mainSink.text("Connectors Directory");
		mainSink.sectionTitle1_();

		// Intro
		mainSink.paragraph();
		mainSink.text(
			"This directory lists the Connectors of ${project.name} ${project.version}." +
			" Each page provides you with the details on each Connector, the targeted platform," +
			" the protocol used, the discovered components and monitored attributes."
		);
		mainSink.paragraph_();

		mainSink.sectionTitle2();
		mainSink.text("Connector Tags");
		mainSink.sectionTitle2_();

		// Sort the entries in tagsSet
		final Set<String> tagsSet = connectorTags
			.stream()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		// Tags Labels
		for (String tag : tagsSet) {
			mainSink.rawText(
				SinkHelper.bootstrapLabel(
					SinkHelper.hyperlinkRef(
						String.format(
							"%s/%s/%s.html",
							connectorSubdirectoryName,
							tagSubdirectoryName,
							tag.toLowerCase().replace(" ", "-")
						),
						tag
					),
					"label label-default"
				)
			);
		}

		mainSink.sectionTitle2();
		mainSink.text("Full Listing");
		mainSink.sectionTitle2_();

		// Table header
		mainSink.table();
		mainSink.tableRow();
		mainSink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		mainSink.text("Name");
		mainSink.tableHeaderCell_();
		mainSink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		mainSink.text("Connector ID");
		mainSink.tableHeaderCell_();
		mainSink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		mainSink.text("Platform");
		mainSink.tableHeaderCell_();
		mainSink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		mainSink.text("Operating Systems");
		mainSink.tableHeaderCell_();
		mainSink.tableHeaderCell(SinkHelper.setClass(BOOTSTRAP_MEDIUM_3_CLASS));
		mainSink.text("Enterprise");
		mainSink.tableHeaderCell_();
		mainSink.tableRow_();

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
				final String connectorPagePath = String.format("%s/%s", connectorSubdirectoryName, pageFilename);

				// Add a row to the table in the main page
				mainSink.tableRow();

				mainSink.tableCell();

				mainSink.link(connectorPagePath);
				mainSink.text(connectorJsonNodeReader.getDisplayName());
				mainSink.link_();
				mainSink.tableCell_();

				mainSink.tableCell();
				mainSink.link(connectorPagePath);
				mainSink.text(connectorId);
				mainSink.link_();
				mainSink.tableCell_();

				mainSink.tableCell();
				mainSink.text(SinkHelper.replaceCommaWithSpace(connectorJsonNodeReader.getPlatformsOrDefault("N/A")));
				mainSink.tableCell_();

				mainSink.tableCell();
				mainSink.text(String.join(", ", OsType.mapToDisplayNames(connectorJsonNodeReader.getAppliesTo())));
				mainSink.tableCell_();

				SinkEventAttributes attributes = new SinkEventAttributeSet(SinkEventAttributes.ALIGN, "center");
				mainSink.tableCell(attributes);
				mainSink.text(enterpriseConnectorIds.contains(connectorId) ? "\u2713" : "");
				mainSink.tableCell_();

				mainSink.tableRow_();
			});

		// Close the main page
		mainSink.table_();

		mainSink.section1_();

		mainSink.body_();

		mainSink.close();
	}
}
