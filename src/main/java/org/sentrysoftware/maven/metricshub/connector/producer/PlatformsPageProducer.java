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

import java.util.List;
import java.util.Objects;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.Constants;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.platform.Platform;

/**
 * Utility class for producing the platforms page of the connectors directory.
 */
public class PlatformsPageProducer {

	private final Log logger;
	private final String platformSubdirectory;

	/**
	 * Constructor for the main platforms page producer.
	 *
	 * @param logger               The logger used for logging.
	 * @param platformSubdirectory The connector subdirectory name.
	 */
	public PlatformsPageProducer(final Log logger, final String platformSubdirectory) {
		this.logger = logger;
		this.platformSubdirectory = platformSubdirectory;
	}

	/**
	 * Produces the main platforms page report that lists all the platforms.
	 *
	 * @param mainSink   The main sink used for generating content.
	 * @param platforms  The list of platforms to be listed as part of the report.
	 */
	public void produce(final Sink mainSink, final List<Platform> platforms) {
		Objects.requireNonNull(platformSubdirectory, () -> "platformSubdirectory cannot be null.");
		Objects.requireNonNull(logger, () -> "logger cannot be null.");
		Objects.requireNonNull(platforms, () -> "platforms cannot be null.");
		Objects.requireNonNull(mainSink, () -> "mainSink cannot be null.");

		logger.debug(
			String.format("Generating the main platforms page %s", Constants.CONNECTORS_DIRECTORY_OUTPUT_FILE_NAME)
		);

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

		// Introduction
		mainSink.paragraph();
		mainSink.text(
			"This directory lists the platforms supported by ${project.name} ${project.version}." +
			" Each platform page lists the connectors that target that platform."
		);
		mainSink.paragraph_();

		mainSink.sectionTitle2();
		mainSink.text("Platforms");
		mainSink.sectionTitle2_();

		// Begin the tile container
		mainSink.division(SinkHelper.setClass("platform-tile-container"));

		// Produce tiles for each platform
		platforms.forEach(platform -> {
			final String platformId = platform.getId();
			final String displayName = platform.getDisplayName();
			final String iconPath = platform.getIconPath();
			final int numberOfConnectors = platform.getConnectors().size();

			// Create a tile for the platform
			mainSink.link(platformSubdirectory + "/" + platformId + ".html", SinkHelper.setClass("platform-tile"));

			mainSink.division(SinkHelper.setClass("platform-title"));

			mainSink.division(SinkHelper.setClass("platform-title-text"));
			mainSink.text(displayName);
			mainSink.division_();

			mainSink.division(SinkHelper.setClass("connectors-badge"));
			mainSink.rawText(SinkHelper.bootstrapBadge(String.valueOf(numberOfConnectors), null));
			mainSink.division_();

			mainSink.division_();

			mainSink.figure(SinkHelper.setClass("platform-icon"));
			// alt="inline" is used to prevent the icon from being displayed in the block element managed by the front framework
			mainSink.figureGraphics(iconPath, SinkHelper.setAttribute(SinkEventAttributes.ALT, "inline"));
			mainSink.figure_();

			mainSink.division(SinkHelper.setClass("platform-labels"));
			platform
				.getTechnologies()
				.stream()
				.map(TechnologyType::getDisplayName)
				.forEach(technology -> mainSink.rawText(SinkHelper.bootstrapLabel(technology, "technology-label")));
			mainSink.division_();

			mainSink.link_();
		});

		// Close the tile container
		mainSink.division_();

		mainSink.section1_();

		mainSink.body_();

		mainSink.close();
	}
}
