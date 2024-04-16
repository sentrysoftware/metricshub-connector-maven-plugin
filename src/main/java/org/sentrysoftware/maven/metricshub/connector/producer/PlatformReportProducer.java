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

import static org.sentrysoftware.maven.metricshub.connector.Constants.CONNECTOR_SUBDIRECTORY_NAME;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.producer.model.platform.Platform;

/**
 * Utility class for producing platforms and requirements report page.
 */
@AllArgsConstructor
public class PlatformReportProducer {

	private final Log logger;

	/**
	 * Produces the Platforms and Requirements page.
	 *
	 * @param mainSink      The main sink used for generating content.
	 * @param osPlatforms   Map of OS to supported platforms.
	 */
	public void produce(final Sink mainSink, final Map<String, List<Platform>> osPlatforms) {
		Objects.requireNonNull(mainSink, () -> "mainSink cannot be null.");
		Objects.requireNonNull(logger, () -> "logger cannot be null.");
		Objects.requireNonNull(osPlatforms, () -> "osPlatforms cannot be null.");

		final String pageFilename = "platform-requirements.html";
		logger.debug("Generating " + pageFilename);

		mainSink.head();
		mainSink.title();
		mainSink.text("Supported Platforms and Requirements");
		mainSink.title_();
		mainSink.head_();

		mainSink.body();

		// Title
		mainSink.section1();
		mainSink.sectionTitle1();
		mainSink.text("Supported Platforms and Requirements");
		mainSink.sectionTitle1_();

		// Introduction
		mainSink.paragraph();
		mainSink.text(
			"This page lists all of the supported platforms, grouped by system type," +
			" and the corresponding prerequisites to ensure that the Connectors will be able" +
			" to connect and gather the required information to assess the health of the platform."
		);
		mainSink.paragraph_();

		// Sort the entries in osPlatforms
		final Set<String> osSet = osPlatforms
			.keySet()
			.stream()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		// ToC
		mainSink.list();
		for (String os : osSet) {
			mainSink.listItem();
			mainSink.link("#" + os.toLowerCase().replace(' ', '-'));
			mainSink.text(os);
			mainSink.link_();
			mainSink.listItem_();
		}
		mainSink.list_();

		// For each entry is osMap
		for (final String os : osSet) {
			// List the platforms
			final List<Platform> platformList = osPlatforms.get(os);
			Collections.sort(platformList);

			mainSink.anchor(os.toLowerCase().replace(' ', '-'));
			mainSink.anchor_();
			mainSink.section2();
			mainSink.sectionTitle2();
			mainSink.text(os);
			mainSink.sectionTitle2_();

			// Table header
			mainSink.table();
			mainSink.tableRow();
			mainSink.tableHeaderCell(SinkHelper.setClass("col-md-2"));
			mainSink.text("Platform");
			mainSink.tableHeaderCell_();
			mainSink.tableHeaderCell(SinkHelper.setClass("col-md-2"));
			mainSink.text("Technology/Protocols");
			mainSink.tableHeaderCell_();
			mainSink.tableHeaderCell(SinkHelper.setClass("col-md-4"));
			mainSink.text("Connector");
			mainSink.tableHeaderCell_();
			mainSink.tableHeaderCell(SinkHelper.setClass("col-md-4"));
			mainSink.text("Instrumentation Prerequisites");
			mainSink.tableHeaderCell_();
			mainSink.tableRow_();

			// List all of the platforms for this OS
			producePlatformRows(mainSink, platformList);

			// Close the tables
			mainSink.table_();

			// Close the section
			mainSink.section2_();
		}

		// Close the Supported Platforms page
		mainSink.section1_();
		mainSink.body_();
		mainSink.close();
	}

	/**
	 * Produces rows for the platform table.
	 *
	 * @param mainSink     The main sink used for generating content.
	 * @param platformList List of Platform objects.
	 */
	private void producePlatformRows(final Sink mainSink, final List<Platform> platformList) {
		for (Platform platform : platformList) {
			mainSink.tableRow();

			mainSink.tableCell();
			final boolean useItalic = platform.getName().toLowerCase().startsWith("any ");
			if (useItalic) {
				mainSink.italic();
			}

			final String[] platformArray = platform.getName().split(",");
			if (platformArray.length > 0) {
				mainSink.text(platformArray[0]);
				for (int i = 1; i < platformArray.length; i++) {
					mainSink.lineBreak();
					mainSink.text(platformArray[i]);
				}
			}

			if (useItalic) {
				mainSink.italic_();
			}
			mainSink.tableCell_();

			mainSink.tableCell();
			mainSink.text(platform.getTechnology());
			mainSink.tableCell_();

			mainSink.tableCell();
			final Map<String, String> connectors = platform.getConnectors();
			if (!connectors.isEmpty()) {
				final Iterator<Entry<String, String>> iterator = connectors.entrySet().iterator();
				final Entry<String, String> entry = iterator.next();
				writeConnectorLink(mainSink, entry);
				while (iterator.hasNext()) {
					final Entry<String, String> next = iterator.next();
					mainSink.lineBreak();
					writeConnectorLink(mainSink, next);
				}
			}
			mainSink.tableCell_();

			mainSink.tableCell();
			final Set<String> prerequisitesArray = platform.getPrerequisites();
			if (!prerequisitesArray.isEmpty()) {
				final Iterator<String> iterator = prerequisitesArray.iterator();
				mainSink.text(iterator.next());
				while (iterator.hasNext()) {
					mainSink.lineBreak();
					mainSink.text(iterator.next());
				}
			}
			mainSink.tableCell_();

			mainSink.tableRow_();
		}
	}

	/**
	 * Writes a link to a connector page in the main sink.
	 *
	 * @param mainSink       The main sink used for generating content.
	 * @param connectorEntry The entry containing the connector ID and its display name.
	 */
	private void writeConnectorLink(final Sink mainSink, final Entry<String, String> connectorEntry) {
		mainSink.link(CONNECTOR_SUBDIRECTORY_NAME + "/" + SinkHelper.buildPageFilename(connectorEntry.getKey()));
		mainSink.text(connectorEntry.getValue());
		mainSink.link_();
	}
}
