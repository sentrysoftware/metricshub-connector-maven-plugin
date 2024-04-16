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
import lombok.AllArgsConstructor;

/**
 * Abstract class representing a criterion.
 * <p>
 * This abstract class defines the contract for criterion types, providing a
 * method to accept a visitor implementing specific business logic.
 * </p>
 * <p>
 * This abstract class also exposes shared methods that are accessible to each
 * criterion.
 * </p>
 */
@AllArgsConstructor
public abstract class AbstractCriterion {

	protected JsonNode criterion;

	/**
	 * Accepts the given visitor.
	 *
	 * @param visitor The visitor class with its specific business logic.
	 */
	public abstract void accept(ICriterionVisitor visitor);

	/**
	 * Gets the expected result from the criterion, or a {@code null} if not present.
	 *
	 * @return The expected result from the criterion, or the {@code null} if not present.
	 */
	protected String getExpectedResult() {
		return nonNullTextOrDefault(criterion.get("expectedResult"), null);
	}
}
