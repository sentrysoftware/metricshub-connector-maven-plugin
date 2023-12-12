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
 * An abstract base class for SNMP (Simple Network Management Protocol)
 * criteria.
 *
 * <p>
 * This class extends {@link AbstractCriterion} and provides common
 * functionality for SNMP-related criteria.
 * </p>
 *
 * @see AbstractCriterion
 */
public abstract class AbstractSnmpCriterion extends AbstractCriterion {

	/**
	 * Parent constructor of the SNMP criterion classes with the provided criterion.
	 *
	 * @param criterion The {@link JsonNode} representing the SNMP criterion.
	 */
	protected AbstractSnmpCriterion(JsonNode criterion) {
		super(criterion);
	}

	/**
	 * Gets the OID from the current SNMP criterion, or {@code null} if not present.
	 *
	 * @return The OID from the criterion, or {@code null} if not present.
	 */
	public String getOid() {
		return nonNullTextOrDefault(criterion.get("oid"), null);
	}

	/**
	 * Get the type of the SNMP criterion.
	 *
	 * @return the type of the criterion as string.
	 */
	protected abstract String getType();
}
