package org.sentrysoftware.maven.metricshub.connector.producer.model.criteria;

import static org.sentrysoftware.maven.metricshub.connector.producer.JsonNodeHelper.nonNullTextOrDefault;

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
import lombok.Builder;

/**
 * Represents a criterion for filtering based on a SQL query.
 *
 * @see AbstractCriterion
 */
public class SqlCriterion extends AbstractCriterion {

	/**
	 * Constructs SqlCriterion with the specified JSON criterion.
	 *
	 * @param criterion The JSON criterion for SQL check.
	 */
	@Builder
	public SqlCriterion(final JsonNode criterion) {
		super(criterion);
	}

	/**
	 * Gets the query from the current SQL criterion, or {@code null} if not present.
	 *
	 * @return The query from the criterion, or {@code null} if not present.
	 */
	public String getQuery() {
		return nonNullTextOrDefault(criterion.get("query"), null);
	}

	@Override
	public void accept(ICriterionVisitor visitor) {
		visitor.visit(this);
	}

}
