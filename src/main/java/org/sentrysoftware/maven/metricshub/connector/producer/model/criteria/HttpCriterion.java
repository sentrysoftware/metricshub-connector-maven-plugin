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
import lombok.Builder;

/**
 * Represents a criterion for filtering based on an HTTP request.
 *
 * @see AbstractCriterion
 */
public class HttpCriterion extends AbstractCriterion {

	/**
	 * Constructs HttpCriterion with the specified JSON criterion.
	 *
	 * @param criterion The JSON criterion for HTTP.
	 */
	@Builder
	public HttpCriterion(final JsonNode criterion) {
		super(criterion);
	}

	@Override
	public void accept(ICriterionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Gets the method from the criterion, or a default value if not present.
	 *
	 * @param defaultValue The default value to return if the method is not present.
	 * @return The method from the criterion, or the default value if not present.
	 */
	public String getMethodOrDefault(final String defaultValue) {
		return nonNullTextOrDefault(criterion.get("method"), defaultValue);
	}

	/**
	 * Gets the URL from the criterion, or {@code null} if not present.
	 *
	 * @return The URL from the criterion, or {@code null} if not present.
	 */
	public String getUrl() {
		return nonNullTextOrDefault(criterion.get("url"), null);
	}

	/**
	 * Gets the header from the criterion, or {@code null} if not present.
	 *
	 * @return The header from the criterion, or {@code null} if not present.
	 */
	public String getHeader() {
		return nonNullTextOrDefault(criterion.get("header"), null);
	}

	/**
	 * Gets the body from the criterion, or {@code null} if not present.
	 *
	 * @return The body from the criterion, or {@code null} if not present.
	 */
	public String getBody() {
		return nonNullTextOrDefault(criterion.get("body"), null);
	}

	/**
	 * Gets the result content from the criterion, or a default value if not present.
	 *
	 * @param defaultValue The default value to return if the result content is not present.
	 * @return The result content from the criterion, or the default value if not present.
	 */
	public String getResultContentOrDefault(final String defaultValue) {
		return nonNullTextOrDefault(criterion.get("resultContent"), defaultValue);
	}
}
