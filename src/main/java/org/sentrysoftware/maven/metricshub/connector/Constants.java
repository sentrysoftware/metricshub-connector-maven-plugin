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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for the plugin.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

	/**
	 * ObjectMapper provides functionality for reading the connector (YAML file)
	 */
	public static final ObjectMapper YAML_OBJECT_MAPPER = JsonMapper.builder(new YAMLFactory()).build();

	/**
	 * Name of the subdirectory that will contain the pages for each connector
	 */
	public static final String CONNECTOR_SUBDIRECTORY_NAME = "connectors";

	/**
	 * Name of the subdirectory that will contain the pages for each tag
	 */
	public static final String TAG_SUBDIRECTORY_NAME = "tags";

	/**
	 * Name of the subdirectory that will contain the pages for each platform
	 */
	public static final String PLATFORM_SUBDIRECTORY_NAME = "platforms";

	/**
	 * Connectors directory output name
	 */
	public static final String CONNECTORS_DIRECTORY_OUTPUT_NAME = "metricshub-connectors-directory";

	/**
	 * Connectors directory output HTML file name
	 */
	public static final String CONNECTORS_DIRECTORY_OUTPUT_FILE_NAME = CONNECTORS_DIRECTORY_OUTPUT_NAME + ".html";

	/**
	 * Connectors full list file name
	 */
	public static final String CONNECTORS_FULL_LISTING_FILE_NAME = "metricshub-connectors-full-listing.html";

	/**
	 * CSS class for a medium-sized Bootstrap column with a width of 3.
	 */
	public static final String BOOTSTRAP_MEDIUM_3_CLASS = "col-md-3";
}
