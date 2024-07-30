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

import static org.sentrysoftware.maven.metricshub.connector.Constants.CONNECTOR_SUBDIRECTORY_NAME;
import static org.sentrysoftware.maven.metricshub.connector.Constants.TAG_SUBDIRECTORY_NAME;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.MavenReportException;
import org.sentrysoftware.maven.metricshub.connector.producer.ConnectorJsonNodeReader;
import org.sentrysoftware.maven.metricshub.connector.producer.ConnectorPageProducer;
import org.sentrysoftware.maven.metricshub.connector.producer.MainPageProducer;
import org.sentrysoftware.maven.metricshub.connector.producer.SinkHelper;
import org.sentrysoftware.maven.metricshub.connector.producer.TagPageProducer;

/**
 * This Maven report goal builds an HTML Page for the Connectors Directory.
 *
 * It is invoked during the Maven site generation process.<br>
 *
 * It takes the source code of the connectors as input then generates a report that describes the
 * connectors in detail.
 * <p>
 * This plugin goal is a report goal that works in the <em>site</em> build lifecycle. It
 * simply needs to be declared in the report section of the pom.xml.
 * </p>
 * <p>
 * The goal actually reads the .yaml files (Connectors) located in ${sourceDirectory} and
 * produces HTML Web pages that are decorated using the specified Maven skin in
 * site.xml.
 * </p>
 */
@Mojo(
	name = "connectors-directory-report",
	aggregator = false,
	defaultPhase = LifecyclePhase.SITE,
	requiresDependencyResolution = ResolutionScope.RUNTIME,
	requiresOnline = false,
	requiresProject = true,
	threadSafe = true
)
public class ConnectorsDirectoryReport extends AbstractConnectorReport {

	/**
	 * Connector directory output name
	 */
	public static final String CONNECTORS_DIRECTORY_OUTPUT_NAME = "metricshub-connectors-directory";

	@Override
	protected void doReport() throws MavenReportException {
		// Subdirectory where we're going to store the pages for each connector.
		final File connectorSubdirectory = new File(outputDirectory, CONNECTOR_SUBDIRECTORY_NAME);
		if (!connectorSubdirectory.exists() && !connectorSubdirectory.mkdirs()) {
			final String message = "Could not create connectorSubdirectory: " + connectorSubdirectory.getAbsolutePath();
			logger.error(message);
			throw new MavenReportException(message);
		}

		// Retrieve tags
		final Map<String, Map<String, JsonNode>> tags = determineTags();

		// Main page
		produceMainPage(tags.keySet());

		// Connector pages
		produceConnectorPages(connectorSubdirectory, buildSupersededMap());

		// Subdirectory whithin connector subdirectory where we store the pages for each tag.
		final File tagSubdirectory = new File(
			String.format("%s/%s", outputDirectory, connectorSubdirectory.getName()),
			TAG_SUBDIRECTORY_NAME
		);
		if (!tagSubdirectory.exists() && !tagSubdirectory.mkdirs()) {
			final String message = "Could not create tagSubdirectory: " + tagSubdirectory.getAbsolutePath();
			logger.error(message);
			throw new MavenReportException(message);
		}

		// Tag pages
		produceTagPages(tagSubdirectory, tags);
	}

	/**
	 * Builds a map representing the superseded relationships between connectors.
	 *
	 * @return A map where each key is a connector ID that is superseded by one or more connectors,
	 *         and the associated value is a list of connectors that supersede it.
	 */
	private Map<String, List<String>> buildSupersededMap() {
		final Map<String, List<String>> supersededMap = new HashMap<>();

		connectors.forEach((connectorId, connector) ->
			new ConnectorJsonNodeReader(connector)
				.getSupersedes()
				.forEach(supersededConnectorId ->
					supersededMap.computeIfAbsent(supersededConnectorId, k -> new ArrayList<>()).add(connectorId)
				)
		);

		return supersededMap;
	}

	/**
	 * Produces individual connector pages for the Maven report
	 *
	 * @param connectorSubdirectory The subdirectory where individual connector pages are located.
	 * @param supersededMap         A map representing the superseded relationships among connectors.
	 * @throws MavenReportException If an error occurs while producing the connector pages.
	 */
	private void produceConnectorPages(final File connectorSubdirectory, final Map<String, List<String>> supersededMap)
		throws MavenReportException {
		for (Entry<String, JsonNode> connectorEntry : connectors.entrySet()) {
			final String connectorId = connectorEntry.getKey();
			// Create a new sink!
			final Sink sink;
			try {
				sink = getSinkFactory().createSink(connectorSubdirectory, SinkHelper.buildPageFilename(connectorId));
			} catch (IOException e) {
				final String message = String.format("Could not create sink for %s in %s", connectorId, connectorSubdirectory);
				logger.error(message, e);
				throw new MavenReportException(message, e);
			}

			ConnectorPageProducer
				.builder()
				.withConnectorId(connectorId)
				.withConnector(connectorEntry.getValue())
				.withLogger(logger)
				.build()
				.produce(sink, supersededMap, enterpriseConnectorIds);
		}
	}

	/**
	 * Generates individual tag pages for the Maven report.
	 *
	 * @param tagSubdirectory The subdirectory where the individual tag pages will be created.
	 * @param tags            A map with tags as keys and corresponding connector maps as values.
	 * @throws MavenReportException If an error occurs during the creation of tag pages.
	 */
	private void produceTagPages(final File tagSubdirectory, final Map<String, Map<String, JsonNode>> tags)
		throws MavenReportException {
		for (Entry<String, Map<String, JsonNode>> tagEntry : tags.entrySet()) {
			final String tag = tagEntry.getKey();
			final Map<String, JsonNode> connectors = tagEntry.getValue();

			// Create a new sink!
			final Sink sink;
			try {
				sink =
					getSinkFactory()
						.createSink(tagSubdirectory, SinkHelper.buildPageFilename(tag.toLowerCase().replace(' ', '-')));
			} catch (IOException e) {
				final String message = String.format("Could not create sink for %s in %s", tag, tagSubdirectory);
				logger.error(message, e);
				throw new MavenReportException(message, e);
			}

			new TagPageProducer(logger, tag).produce(sink, connectors, CONNECTOR_SUBDIRECTORY_NAME, enterpriseConnectorIds);
		}
	}

	/**
	 * Produces the main page for the Maven report
	 */
	private void produceMainPage(final Set<String> tags) throws MavenReportException {
		final Sink mainSink = getMainSink();

		new MainPageProducer(logger, CONNECTOR_SUBDIRECTORY_NAME, TAG_SUBDIRECTORY_NAME)
			.produce(mainSink, connectors, enterpriseConnectorIds, tags);
	}

	@Override
	public String getDescription(final Locale locale) {
		return "Detailed description of all discovered monitors and reported metrics in each Connector in ${project.name} ${project.version}";
	}

	@Override
	public String getName(final Locale locale) {
		return "Connectors Directory Report";
	}

	@Override
	public String getOutputName() {
		return CONNECTORS_DIRECTORY_OUTPUT_NAME;
	}

	/**
	 * Constructs a map where each key is a tag name and its corresponding value is another map.
	 * The inner map contains connector IDs as keys and their associated JsonNode objects as values.
	 * <p>
	 * This method flattens the tags from the connectors map to make it easier to generate tag pages.
	 * </p>
	 *
	 * @return a map where keys are tag names and values are maps of connector IDs to JsonNode objects.
	 */
	private Map<String, Map<String, JsonNode>> determineTags() {
		return connectors
			.entrySet()
			.stream()
			.flatMap(connectorEntry -> {
				final JsonNode connector = connectorEntry.getValue();
				final ConnectorJsonNodeReader reader = new ConnectorJsonNodeReader(connector);
				return reader
					.getTags()
					.stream()
					.filter(tag -> !tag.isBlank())
					.map(tag -> new AbstractMap.SimpleEntry<>(tag, connectorEntry));
			})
			.collect(
				Collectors.groupingBy(
					Map.Entry::getKey,
					Collectors.mapping(Map.Entry::getValue, Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
				)
			);
	}
}
