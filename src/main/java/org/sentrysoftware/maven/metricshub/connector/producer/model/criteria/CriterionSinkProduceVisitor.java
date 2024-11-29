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

import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.maven.doxia.sink.Sink;
import org.sentrysoftware.maven.metricshub.connector.producer.SinkHelper;

/**
 * A visitor implementation for producing a criterion sink using the provided
 * criteria.
 *
 * <p>
 * This class implements the {@link ICriterionVisitor} interface, allowing it to
 * visit different types of criteria and produce a criterion sink based on the
 * visited criteria.
 * </p>
 *
 * @see ICriterionVisitor
 */
@AllArgsConstructor
public class CriterionSinkProduceVisitor implements ICriterionVisitor {

	private static final String CODE_FORMAT = "<code>%s</code>";

	private final Sink sink;

	@Override
	public void visit(DeviceTypeCriterion deviceTypeCriterion) {
		// Operating System
		sink.listItem();
		final List<String> keptOsList = deviceTypeCriterion.getKeptOsList();
		if (keptOsList != null && !keptOsList.isEmpty()) {
			sink.rawText("Operating System is <b>" + String.join("</b> or <b>", keptOsList) + "</b>");
		}
		final List<String> excludedOsList = deviceTypeCriterion.getExcludedOsList();
		if (excludedOsList != null && !excludedOsList.isEmpty()) {
			sink.rawText("Operating System is <b>NOT " + String.join("</b> and <b>NOT ", excludedOsList) + "</b>");
		}
		sink.listItem_();
	}

	@Override
	public void visit(HttpCriterion httpCriterion) {
		// HTTP
		sink.listItem();
		sink.text("The ");
		sink.bold();
		sink.text("HTTP Request");
		sink.bold_();
		sink.text(" below to the managed host succeeds:");
		sink.list();
		sink.listItem();
		// Retrieve URL and Path Fields values
		String urlField = httpCriterion.getUrl();
		String pathField = httpCriterion.getPath();
		// Initialize the final URL value
		String url = "";
		// If both URL and Path fields aren't null, concatenate them
		if (urlField != null && pathField != null) {
			url =
				String.format(
					"%s%s%s",
					urlField,
					urlField.endsWith("/") || pathField.startsWith("/") ? "" : "/",
					urlField.endsWith("/") && pathField.startsWith("/") ? pathField.substring(1) : pathField
				);
			// if Only URL field value is found, use it
		} else if (urlField != null) {
			url = urlField;
			// if Only Path field value is found, use it
		} else if (pathField != null) {
			url = pathField;
		}
		sink.rawText(
			String.format(
				"<b>%s</b> <code>%s</code>",
				httpCriterion.getMethodOrDefault("GET"),
				SinkHelper.replaceWithHtmlCode(url)
			)
		);
		sink.listItem_();

		final String httpHeader = httpCriterion.getHeader();
		if (httpHeader != null) {
			sink.listItem();
			sink.text("Request Header:");
			sink.lineBreak();
			sink.rawText(String.format(CODE_FORMAT, SinkHelper.replaceWithHtmlCode(httpHeader)));
			sink.listItem_();
		}

		final String httpBody = httpCriterion.getBody();
		if (httpBody != null) {
			sink.listItem();
			sink.text("Request Body:");
			sink.lineBreak();
			sink.rawText(String.format(CODE_FORMAT, SinkHelper.replaceWithHtmlCode(httpBody)));
			sink.listItem_();
		}

		String expectedResult = httpCriterion.getExpectedResult();
		if (expectedResult != null) {
			expectedResult = SinkHelper.replaceWithHtmlCode(expectedResult);
			final String resultContent = httpCriterion.getResultContentOrDefault("body").toLowerCase();
			sink.listItem();
			if ("body".equals(resultContent)) {
				sink.rawText(String.format("The response body contains: <code>%s</code> (regex)", expectedResult));
			} else if ("header".equals(resultContent)) {
				sink.rawText(String.format("The response header contains: <code>%s</code> (regex)", expectedResult));
			} else if ("httpstatus".equals(resultContent)) {
				sink.rawText(String.format("The HTTP response status code contains: <code>%s</code> (regex)", expectedResult));
			} else {
				sink.rawText(
					String.format("The entire response (header + body) contains: <code>%s</code> (regex)", expectedResult)
				);
			}
			sink.listItem_();
		}
		sink.list_();
		sink.listItem_();
	}

	@Override
	public void visit(IpmiCriterion ipmiCriterion) {
		sink.listItem();
		sink.rawText("The IPMI-related WMI classes are populated on Windows,");
		sink.listItem_();
		sink.listItem();
		sink.bold();
		sink.text("OR");
		sink.bold_();
		sink.rawText(" <code>ipmitool</code> works properly with the local IPMI driver on Linux and Solaris, ");
		sink.listItem_();
		sink.listItem();
		sink.bold();
		sink.text("OR");
		sink.bold_();
		sink.text(" IPMI-over-LAN has been enabled as an out-of-band interface");
		sink.listItem_();
	}

	@Override
	public void visit(CommandLineCriterion commandLineCriterion) {
		// Command Line
		String commandLine = commandLineCriterion.getCommandLineOrDefault("N/A");

		// Remove mentions to sudo
		commandLine = commandLine.replaceAll("%\\{SUDO:[a-zA-Z\\d/\\-_]+\\}", "");

		sink.listItem();
		sink.text("The command below succeeds on the ");
		if (commandLineCriterion.isExecuteLocallyOrDefault(false)) {
			sink.bold();
			sink.text("agent host");
			sink.bold_();
		} else {
			sink.text("monitored host");
		}
		sink.list();
		sink.listItem();
		sink.rawText(String.format("Command: <code>%s</code>", SinkHelper.replaceWithHtmlCode(commandLine)));
		sink.listItem_();
		final String expectedResult = commandLineCriterion.getExpectedResult();
		if (expectedResult != null) {
			sink.listItem();
			sink.rawText(
				String.format("Output contains: <code>%s</code> (regex)", SinkHelper.replaceWithHtmlCode(expectedResult))
			);
			sink.listItem_();
		}
		sink.list_();
		sink.listItem_();
	}

	@Override
	public void visit(ProcessCriterion processCriterion) {
		// Process
		sink.listItem();
		sink.text("At least one process for which the command line matches with the regular expression below is running:");
		sink.lineBreak();
		sink.rawText(String.format(CODE_FORMAT, processCriterion.getCommandLine()));
		sink.listItem_();
	}

	@Override
	public void visit(ProductRequirementsCriterion productRequirementsCriterion) {
		final String version = productRequirementsCriterion.getEngineVersion();
		if (version != null) {
			sink.listItem();
			sink.text("The MetricsHub is in version ");
			sink.bold();
			sink.text(version);
			sink.bold_();
			sink.text(" or greater");
			sink.listItem_();
		}
	}

	@Override
	public void visit(ServiceCriterion serviceCriterion) {
		// Windows Service
		sink.listItem();
		sink.text("The Windows service ");
		sink.bold();
		sink.text(serviceCriterion.getName());
		sink.bold_();
		sink.text(" is running");
		sink.listItem_();
	}

	@Override
	public void visit(SnmpGetCriterion snmpGetCriterion) {
		buildSnmpSink(snmpGetCriterion, " a value that matches with the ", " a non-empty value");
	}

	/**
	 * Builds the sink for SNMP-related information based on the provided SNMP criterion.
	 *
	 * @param abstractSnmpCriterion   The SNMP criterion to extract information from.
	 * @param expectedResultStartMsg  Start message to use if the expected result directive is present.
	 * @param nonExpectedResultEndMsg End message to use if the expected result directive is not present.
	 */
	private void buildSnmpSink(
		final AbstractSnmpCriterion abstractSnmpCriterion,
		final String expectedResultStartMsg,
		final String nonExpectedResultEndMsg
	) {
		final String type = abstractSnmpCriterion.getType();

		// SNMP
		sink.listItem();
		final String oid = abstractSnmpCriterion.getOid();
		// SNMP Get
		sink.text("An ");
		sink.bold();
		sink.text(String.format("SNMP %s", type));
		sink.bold_();
		sink.rawText(String.format(" on the OID <code>%s</code>", SinkHelper.replaceWithHtmlCode(oid)));
		sink.text(" must return ");
		final String expectedResult = abstractSnmpCriterion.getExpectedResult();
		if (expectedResult != null) {
			sink.text(expectedResultStartMsg);
			sink.rawText(String.format("<code>%s</code> (regular expression)", expectedResult));
		} else {
			sink.text(nonExpectedResultEndMsg);
		}
		sink.listItem_();
	}

	@Override
	public void visit(SnmpGetNextCriterion snmpGetNextCriterion) {
		buildSnmpSink(snmpGetNextCriterion, " a value in the same subtree and contains ", " a value in the same subtree");
	}

	@Override
	public void visit(WbemCriterion wbemCriterion) {
		buildWqlSink(wbemCriterion);
	}

	/**
	 * Builds the sink for WQL-related information based on the provided WQL criterion.
	 *
	 * @param abstractWqlCriterion The WQL criterion to extract information from.
	 */
	private void buildWqlSink(AbstractWqlCriterion abstractWqlCriterion) {
		// WQL
		sink.listItem();
		sink.text("The ");
		sink.bold();
		sink.text(String.format("%s query", abstractWqlCriterion.getType()));
		sink.bold_();
		sink.text(" below to the managed host succeeds:");
		sink.list();
		sink.listItem();
		sink.rawText(
			String.format(
				"Namespace: <code>%s</code>",
				SinkHelper.replaceWithHtmlCode(abstractWqlCriterion.getNamespaceOrDefault("root/cimv2"))
			)
		);
		sink.listItem_();
		sink.listItem();
		sink.rawText(
			String.format("WQL Query: <code>%s</code>", SinkHelper.replaceWithHtmlCode(abstractWqlCriterion.getQuery()))
		);
		sink.listItem_();
		final String expectedResult = abstractWqlCriterion.getExpectedResult();
		if (expectedResult != null) {
			sink.listItem();
			sink.rawText(
				String.format("Result contains: <code>%s</code> (regex)", SinkHelper.replaceWithHtmlCode(expectedResult))
			);
			sink.listItem_();
		}
		sink.list_();
		sink.listItem_();
	}

	@Override
	public void visit(WmiCriterion wmiCriterion) {
		buildWqlSink(wmiCriterion);
	}

	@Override
	public void visit(final SqlCriterion sqlCriterion) {
		// SQL
		sink.listItem();
		sink.text("The ");
		sink.bold();
		sink.text("SQL query");
		sink.bold_();
		sink.text(" below succeeds on the monitored database:");
		sink.list();
		sink.list();
		sink.listItem();
		sink.rawText(String.format("SQL Query: <code>%s</code>", SinkHelper.replaceWithHtmlCode(sqlCriterion.getQuery())));
		sink.listItem_();

		final String expectedResult = sqlCriterion.getExpectedResult();
		if (expectedResult != null) {
			sink.listItem();
			sink.rawText(String.format("Expected Result: <code>%s</code>", SinkHelper.replaceWithHtmlCode(expectedResult)));
			sink.listItem_();
		}

		// End the SQL criteria list
		sink.list_();
		sink.listItem_();
	}
}
