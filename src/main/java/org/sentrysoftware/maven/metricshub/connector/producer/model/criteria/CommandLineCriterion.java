package org.sentrysoftware.maven.metricshub.connector.producer.model.criteria;

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

import static org.sentrysoftware.maven.metricshub.connector.producer.JsonNodeHelper.nonNullBooleanOrDefault;
import static org.sentrysoftware.maven.metricshub.connector.producer.JsonNodeHelper.nonNullTextOrDefault;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

/**
 * Represents a criterion for filtering based on a command line.
 *
 * @see AbstractCriterion
 */
public class CommandLineCriterion extends AbstractCriterion {

	/**
	 * Constructs CommandLineCriterion with the specified JSON criterion.
	 *
	 * @param criterion The JSON criterion for command line.
	 */
	@Builder
	public CommandLineCriterion(final JsonNode criterion) {
		super(criterion);
	}

	@Override
	public void accept(ICriterionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Checks if the execution should be performed locally.
	 *
	 * @param defaultValue The default value to return if the executeLocally node is not present.
	 * @return {@code true} if execution should be performed locally, {@code false} otherwise.
	 */
	public boolean isExecuteLocallyOrDefault(final boolean defaultValue) {
		return nonNullBooleanOrDefault(criterion.get("executeLocally"), defaultValue);
	}

	/**
	 * Gets the command line from the criterion, or a default value if not present.
	 *
	 * @param defaultValue The default value to return if the command line is not present.
	 * @return The command line from the criterion, or the default value if not present.
	 */
	public String getCommandLineOrDefault(final String defaultValue) {
		return nonNullTextOrDefault(criterion.get("commandLine"), defaultValue);
	}
}
