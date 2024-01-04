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

import static org.sentrysoftware.maven.metricshub.connector.producer.JsonNodeHelper.nonNullTextOrDefault;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class extends {@link AbstractCriterion} and provides common
 * functionality for WQL-related criteria.
 *
 * @see AbstractCriterion
 */
public abstract class AbstractWqlCriterion extends AbstractCriterion {

	/**
	 * Parent constructor of the WQL criterion classes with the provided criterion.
	 *
	 * @param criterion The {@link JsonNode} representing the WQL criterion.
	 */
	protected AbstractWqlCriterion(JsonNode criterion) {
		super(criterion);
	}

	/**
	 * Gets the namespace from the current WQL criterion, or default value if not present.
	 *
	 * @param defaultValue The default value to return if the value is not present.
	 * @return The namespace from the criterion, or default value if not present.
	 */
	public String getNamespaceOrDefault(final String defaultValue) {
		return nonNullTextOrDefault(criterion.get("namespace"), defaultValue);
	}

	/**
	 * Gets the query from the current WQL criterion, or {@code null} if not present.
	 *
	 * @return The query from the criterion, or {@code null} if not present.
	 */
	public String getQuery() {
		return nonNullTextOrDefault(criterion.get("query"), null);
	}

	/**
	 * Get the type of the WQL criterion.
	 *
	 * @return the type of the criterion as string.
	 */
	protected abstract String getType();
}
