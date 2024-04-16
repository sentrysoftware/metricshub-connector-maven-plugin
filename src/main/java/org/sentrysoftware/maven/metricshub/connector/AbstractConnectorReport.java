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
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.sentrysoftware.maven.metricshub.connector.parser.ConnectorLibraryParser;

/**
 * An abstract base class for Maven reports related to connectors.
 */
public abstract class AbstractConnectorReport extends AbstractMavenReport {

	/**
	 * Where all the .yaml files are located. Only the .yaml files will actually be parsed.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/main/connector", property = "sourceDirectory", required = true)
	protected File sourceDirectory;

	protected Log logger;

	protected Map<String, JsonNode> connectors;

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		// Get and set the logger
		logger = getLog();

		// Is this an existing directory?
		if (!sourceDirectory.exists()) {
			final String message = String.format("sourceDirectory '%s' does not exist", sourceDirectory);
			logger.error(message);
			throw new MavenReportException(message);
		}

		if (!sourceDirectory.isDirectory()) {
			final String message = String.format("sourceDirectory '%s' is not a directory", sourceDirectory);
			logger.error(message);
			throw new MavenReportException(message);
		}

		// Need to create outputDirectory?
		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			final String message = "Could not create outputDirectory: " + outputDirectory.getAbsolutePath();
			logger.error(message);
			throw new MavenReportException(message);
		}

		// Parse the connector library
		connectors = parseConnectors();

		// Produce the report
		doReport();
	}

	/**
	 * Performs the main logic of generating the report. Subclasses should implement this method to define
	 * the specific report generation logic.
	 */
	protected abstract void doReport() throws MavenReportException;

	/**
	 * Parses the connector library located at the specified source directory and
	 * returns a mapping of connector identifiers to their corresponding JsonNodes.
	 *
	 * @return A {@code Map} containing connector names as keys and their associated
	 *         {@link JsonNode} objects as values.
	 * @throws MavenReportException If an error occurs during the parsing process,
	 *                              including IO errors or parsing failures.
	 */
	protected Map<String, JsonNode> parseConnectors() throws MavenReportException {
		try {
			return new ConnectorLibraryParser().parse(sourceDirectory.toPath());
		} catch (IOException e) {
			final String message = String.format(
				"An error occurred during the parsing of the connector library at %s. Details: %s",
				sourceDirectory.getAbsolutePath(),
				e.getMessage()
			);
			logger.error(message);
			throw new MavenReportException(message, e);
		}
	}

	/**
	 * Retrieves the main Doxia sink.
	 *
	 * @return The main Doxia sink.
	 * @throws MavenReportException If an error occurs while retrieving the sink.
	 */
	protected Sink getMainSink() throws MavenReportException {
		final Sink mainSink = getSink();
		if (mainSink == null) {
			logger.error("Could not get the Doxia sink");
			throw new MavenReportException("Could not get the Doxia sink");
		}
		return mainSink;
	}
}
