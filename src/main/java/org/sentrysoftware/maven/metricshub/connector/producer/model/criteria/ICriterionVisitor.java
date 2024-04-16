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

/**
 * Interface defining a visitor pattern for criterion types.
 * <p>
 * This interface provides a set of methods for visiting different criterion types. Concrete implementations
 * of this interface can be used to define specific business logic for each criterion type when needed.
 * </p>
 */
public interface ICriterionVisitor {
	/**
	 * Visits the specified device type criterion.
	 *
	 * @param deviceTypeCriterion The device type criterion to visit.
	 */
	void visit(DeviceTypeCriterion deviceTypeCriterion);

	/**
	 * Visits the specified HTTP criterion.
	 *
	 * @param httpCriterion The HTTP criterion to visit.
	 */
	void visit(HttpCriterion httpCriterion);

	/**
	 * Visits the specified IPMI criterion.
	 *
	 * @param ipmiCriterion The IPMI criterion to visit.
	 */
	void visit(IpmiCriterion ipmiCriterion);

	/**
	 * Visits the specified Command Line criterion.
	 *
	 * @param commandLineCriterion The Command Line criterion to visit.
	 */
	void visit(CommandLineCriterion commandLineCriterion);

	/**
	 * Visits the specified Process criterion.
	 *
	 * @param processCriterion The Process criterion to visit.
	 */
	void visit(ProcessCriterion processCriterion);

	/**
	 * Visits the specified Product Requirements criterion.
	 *
	 * @param productRequirementsCriterion The Product Requirements criterion to visit.
	 */
	void visit(ProductRequirementsCriterion productRequirementsCriterion);

	/**
	 * Visits the specified Service criterion.
	 *
	 * @param serviceCriterion The Service criterion to visit.
	 */
	void visit(ServiceCriterion serviceCriterion);

	/**
	 * Visits the specified SNMP Get criterion.
	 *
	 * @param snmpGetCriterion The SNMP Get criterion to visit.
	 */
	void visit(SnmpGetCriterion snmpGetCriterion);

	/**
	 * Visits the specified SNMP GetNext criterion.
	 *
	 * @param snmpGetNextCriterion The SNMP GetNext criterion to visit.
	 */
	void visit(SnmpGetNextCriterion snmpGetNextCriterion);

	/**
	 * Visits the specified WBEM criterion.
	 *
	 * @param wbemCriterion The WBEM criterion to visit.
	 */
	void visit(WbemCriterion wbemCriterion);

	/**
	 * Visits the specified WMI criterion.
	 *
	 * @param wmiCriterion The WMI criterion to visit.
	 */
	void visit(WmiCriterion wmiCriterion);
}
