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

import com.fasterxml.jackson.databind.JsonNode;
import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Builder;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.sentrysoftware.maven.metricshub.connector.ConnectorsDirectoryReport;
import org.sentrysoftware.maven.metricshub.connector.Constants;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.ConnectorDefaultVariable;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.OpenTelemetryHardwareType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.OsType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.common.TechnologyType;
import org.sentrysoftware.maven.metricshub.connector.producer.model.criteria.CriterionFactory;
import org.sentrysoftware.maven.metricshub.connector.producer.model.criteria.CriterionSinkProduceVisitor;

/**
 * Utility class for producing the connector page.
 *
 */
@Builder(setterPrefix = "with")
public class ConnectorPageProducer {

	private static final String WMI_SECTION = "          wmi:\n";
	private static final String PROTOCOLS_SECTION = "        protocols:\n";

	private final String connectorId;
	private final JsonNode connector;
	private final Log logger;
	private String connectorDirectory;

	/**
	 * Produces a report page for the current connector and generates the corresponding sink for documentation output.
	 *
	 * This method generates a table with connector information, adding a new column to indicate if a connector is an enterprise connector.
	 *
	 * @param sink                   The sink used for generating content.
	 * @param supersededMap          Map of superseded connectors.
	 * @param enterpriseConnectorIds List of IDs for enterprise connectors.
	 */
	public void produce(
		final Sink sink,
		final Map<String, List<String>> supersededMap,
		final List<String> enterpriseConnectorIds
	) {
		Objects.requireNonNull(connectorId, () -> "connectorId cannot be null.");
		Objects.requireNonNull(connector, () -> "connector cannot be null.");
		Objects.requireNonNull(supersededMap, () -> "supersededMap cannot be null.");
		Objects.requireNonNull(sink, () -> "sink cannot be null.");
		Objects.requireNonNull(logger, () -> "logger cannot be null.");

		logger.debug("Generating " + SinkHelper.buildPageFilename(connectorId));

		final ConnectorJsonNodeReader connectorJsonNodeReader = new ConnectorJsonNodeReader(connector);
		final String displayName = connectorJsonNodeReader.getDisplayName();

		// Create the head element of the page
		sink.head();
		sink.title();
		sink.text(displayName);
		sink.title_();
		sink.head_();

		sink.body();

		// Links to the main page and full listing
		backLinks(
			sink,
			String.format("../%s", Constants.CONNECTORS_DIRECTORY_OUTPUT_FILE_NAME),
			String.format("../%s", Constants.CONNECTORS_FULL_LISTING_FILE_NAME)
		);

		// Big title
		sink.section1();
		sink.sectionTitle1();
		sink.text(displayName);
		sink.sectionTitle1_();

		// Description
		sink.section2();
		sink.sectionTitle2();
		sink.text("Description");
		sink.sectionTitle2_();

		sink.paragraph();
		sink.text(connectorJsonNodeReader.getInformationOrDefault("N/A"));
		sink.paragraph_();

		produceSupersedesContent(sink, supersededMap, connectorJsonNodeReader);

		// Display the connector tags
		final List<String> connectorTags = connectorJsonNodeReader.getTags();
		sink.paragraph();
		connectorTags
			.stream()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.collect(Collectors.toCollection(LinkedHashSet::new))
			.forEach(tag ->
				sink.rawText(
					SinkHelper.bootstrapLabel(
						SinkHelper.hyperlinkRef(
							String.format("%s/%s.html", Constants.TAG_SUBDIRECTORY_NAME, tag.toLowerCase().replace(" ", "-")),
							tag
						),
						"metricshub-tag"
					)
				)
			);
		sink.paragraph_();

		// The GitHub link will be generated only for community connectors
		if (!enterpriseConnectorIds.contains(connectorId)) {
			connectorDirectory = connectorJsonNodeReader.getRelativePath().replace("\\", "/");
			// Add a link to the connector source.
			sink.paragraph();
			sink.rawText(SinkHelper.gitHubHyperlinkRef(connectorDirectory, "Source"));
			sink.paragraph_();
		}

		// End of the second heading element
		sink.section2_();

		// Target
		sink.section2();
		sink.sectionTitle2();
		sink.text("Target");
		sink.sectionTitle2_();

		// Typical platforms
		final Set<String> platforms = connectorJsonNodeReader.getPlatforms();
		final int platformsSize = platforms.size();

		sink.paragraph();
		sink.text("Typical ");
		sink.text(new ChoiceFormat("1#platform|1<platforms").format(platformsSize));
		sink.text(": ");

		sink.bold();
		int index = 0;
		for (String platform : platforms) {
			sink.link(
				"%s/%s.html".formatted(Constants.PLATFORM_SUBDIRECTORY_NAME, ConnectorsDirectoryReport.kebabCase(platform))
			);
			sink.text(platform);
			sink.link_();
			if (++index < platformsSize) {
				sink.text(", ");
			}
		}
		sink.bold_();

		sink.paragraph_();

		// OS of this connector
		final List<String> appliesTo = connectorJsonNodeReader.getAppliesTo();
		final List<String> osList = OsType.mapToDisplayNames(appliesTo);
		sink.paragraph();
		sink.text("Operating ");
		sink.text(new ChoiceFormat("1#system|1<systems").format(osList.size()));
		sink.text(": ");
		sink.bold();
		sink.text(String.join(", ", osList));
		sink.bold_();
		sink.paragraph_();

		// End of the second heading element
		sink.section2_();

		// Prerequisites
		sink.section2();
		sink.sectionTitle2();
		sink.text("Prerequisites");
		sink.sectionTitle2_();

		// Product Requirements
		final String metricsHubVersion = connectorJsonNodeReader.getRequiredMetricsHubVersion();
		if (metricsHubVersion != null) {
			sink.paragraph();
			sink.text("Requires MetricsHub Version: ");
			sink.bold();
			sink.text(metricsHubVersion);
			sink.bold_();
			sink.text(" or greater");
			sink.paragraph_();
		}

		sink.paragraph();
		sink.text("Leverages: ");
		sink.bold();
		sink.text(connectorJsonNodeReader.getReliesOnOrDefault("N/A"));
		sink.bold_();
		sink.paragraph_();

		final Set<TechnologyType> technologies = connectorJsonNodeReader.getTechnologies();
		sink.paragraph();
		sink.text("Technology and protocols: ");
		sink.bold();
		sink.text(technologies.stream().map(TechnologyType::getDisplayName).collect(Collectors.joining(", ")));
		sink.bold_();
		sink.paragraph_();

		// Displaying connector variables list
		final Set<String> connectorVariables = connectorJsonNodeReader.getVariablesNames();
		final Map<String, ConnectorDefaultVariable> connectorDefaultVariables =
			connectorJsonNodeReader.getDefaultVariables();
		if (!connectorVariables.isEmpty()) {
			sink.paragraph();
			sink.text("Variables:");
			sink.list();
			for (final String variable : connectorVariables) {
				sink.listItem();
				sink.rawText(String.format("<code>%s</code>", variable));
				final ConnectorDefaultVariable connectorDefaultVariable = connectorDefaultVariables.get(variable);
				if (connectorDefaultVariable != null) {
					produceVariableSection(sink, connectorDefaultVariables.get(variable));
				}
				sink.listItem_();
			}
			sink.list_();
			sink.paragraph_();
		}

		// Sudo Commands?
		final List<String> sudoCommands = connectorJsonNodeReader.getSudoCommands();
		produceSudoCommandsContent(sink, sudoCommands);

		// Local support = false? (default is true)
		final Set<String> connectionTypes = connectorJsonNodeReader.getConnectionTypes();
		if (!connectionTypes.contains("local")) {
			sink.paragraph();
			sink.text("This connector is not available for the local host (it is applicable to remote hosts only).");
			sink.paragraph_();
		}

		// Remote support = false? (default is false)
		if (!connectionTypes.contains("remote")) {
			sink.paragraph();
			sink.bold();
			sink.text("This connector is not available for remote hosts (it is applicable to the local host only).");
			sink.bold_();
			sink.paragraph_();
		}

		// End of the second heading element
		sink.section2_();

		// MetricsHub Example
		produceMetricsHubExamplesContent(sink, appliesTo, technologies, sudoCommands, connectorVariables);

		// Detection criteria
		sink.section2();
		sink.sectionTitle2();
		sink.text("Connector Activation Criteria");
		sink.sectionTitle2_();

		sink.paragraph();
		sink.text("The ");
		sink.bold();
		sink.text(displayName);
		sink.bold_();

		// No automatic detection
		if (connectorJsonNodeReader.isAutoDetectionDisabled()) {
			sink.text(" connector ");
			sink.bold();
			sink.text("must be selected manually");
			sink.bold_();
			sink.text("");
		} else {
			sink.text(" connector will be automatically activated");
		}

		sink.text(", and its status will be reported as OK if all the below criteria are met");

		final String onLastResort = connectorJsonNodeReader.getOnLastResort();
		if (onLastResort != null && !onLastResort.isBlank()) {
			sink.text(", and no other connector capable of discovering ");
			sink.bold();
			sink.text(onLastResort);
			sink.bold_();
			sink.text(" instances is activated");
		}
		sink.text(":");
		sink.paragraph_();

		// Detection Criteria
		sink.list();
		connectorJsonNodeReader
			.getCriteria()
			.forEach(criterionNode ->
				CriterionFactory
					.withJsonNode(criterionNode)
					.ifPresent(criterion -> criterion.accept(new CriterionSinkProduceVisitor(sink)))
			);
		sink.list_();

		// End of the second heading element
		sink.section2_();

		// Metrics
		produceMetricsTable(sink, connectorJsonNodeReader);

		// End of the first heading element
		sink.section1_();

		// End of the body element
		sink.body_();

		// Close the writer
		sink.close();
	}

	/**
	 * Produces back links to the main page and full listing.
	 *
	 * @param sink                    The sink used for generating content
	 * @param connectorsDirectoryLink The link to the connectors directory
	 * @param fullListingLink         The link to the full listing of connectors
	 */
	public static void backLinks(final Sink sink, final String connectorsDirectoryLink, final String fullListingLink) {
		// Links to the main page and full listing
		sink.paragraph(SinkHelper.setClass("small"));

		sink.rawText(SinkHelper.glyphIcon("arrow-left") + SinkHelper.NON_BREAKING_SPACE);

		sink.link(connectorsDirectoryLink);
		sink.text("Connectors directory");
		sink.link_();

		sink.rawText(
			SinkHelper.NON_BREAKING_SPACE.repeat(2) + SinkHelper.glyphIcon("arrow-left") + SinkHelper.NON_BREAKING_SPACE
		);

		sink.link(fullListingLink);
		sink.text("Full listing of connectors");
		sink.link_();

		sink.paragraph_();
	}

	/**
	 * Produces a metrics table in the specified Sink using information from the ConnectorJsonNodeReader.
	 *
	 * @param sink                    The sink used for generating content.
	 * @param connectorJsonNodeReader The ConnectorJsonNodeReader providing connector information.
	 */
	private void produceMetricsTable(final Sink sink, final ConnectorJsonNodeReader connectorJsonNodeReader) {
		sink.section2();
		sink.sectionTitle2();
		sink.text("Metrics");
		sink.sectionTitle2_();

		sink.table();
		sink.tableRow();

		sink.tableHeaderCell(SinkHelper.setClass("col-md-2"));
		sink.text("Type");
		sink.tableHeaderCell_();

		sink.tableHeaderCell(SinkHelper.setClass("col-md-6"));
		sink.text("Collected Metrics");
		sink.tableHeaderCell_();

		sink.tableHeaderCell(SinkHelper.setClass("col-md-4"));
		sink.text("Specific Attributes");
		sink.tableHeaderCell_();

		sink.tableRow_();

		// For each object, create a new row
		connectorJsonNodeReader
			.getMonitors()
			.ifPresent(monitors -> {
				final Map<String, JsonNode> sortedMonitors = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

				monitors
					.fields()
					.forEachRemaining(monitorEntry -> sortedMonitors.put(monitorEntry.getKey(), monitorEntry.getValue()));

				sortedMonitors.forEach((monitorType, monitor) -> {
					sink.tableRow();

					// Cell 1: Class
					sink.tableCell();
					sink.bold();
					sink.text(monitorType);
					sink.bold_();
					sink.tableCell_();

					// Cell 2: List of metrics
					sink.tableCell();
					sink.list();

					final List<String> metricList = new ArrayList<>();

					// The present metric is only available on hardware monitors
					if (OpenTelemetryHardwareType.isHardwareMonitorType(monitorType)) {
						metricList.add(String.format("hw.status{hw.type=\"%s\", state=\"present\"}", monitorType));
					}

					metricList.addAll(connectorJsonNodeReader.getMonitorMetrics(monitor));

					Collections.sort(metricList);

					for (String metricName : metricList) {
						sink.listItem();
						sink.rawText(String.format("<code class=\"language-js\">%s</code>", metricName));
						sink.listItem_();
					}

					sink.list_();
					sink.tableCell_();

					// Cell 3: List of attributes
					sink.tableCell();
					sink.list();

					connectorJsonNodeReader
						.getMonitorAttributes(monitor)
						.stream()
						.sorted()
						.forEach(attributeKey -> {
							sink.listItem();
							sink.rawText(String.format("<code>%s</code>", attributeKey));
							sink.listItem_();
						});

					sink.list_();
					sink.tableCell_();

					sink.tableRow_();
				});
			});

		sink.table_();
		sink.section2_();
	}

	/**
	 * Generates content to display MetricsHub examples (CLI and Agent) to execute the current connector.
	 *
	 * @param sink         The sink used for generating content.
	 * @param osTypes      OS types supported by the current connector.
	 * @param technology   Technologies and protocols used by the current connector.
	 * @param sudoCommands List of sudo commands.

	 */
	private void produceMetricsHubExamplesContent(
		final Sink sink,
		final List<String> osTypes,
		final Set<TechnologyType> technologies,
		final List<String> sudoCommands,
		final Set<String> connectorVariables
	) {
		sink.section2();
		sink.sectionTitle2();
		sink.text("Examples");
		sink.sectionTitle2_();

		// Determine a possible host type
		final String hostType = determinePossibleHostType(osTypes, technologies);

		// Build YAML config and CLI
		final StringBuilder cli = new StringBuilder("metricshub HOSTNAME -t ").append(hostType);
		cli.append(" -c +").append(connectorId);
		final StringBuilder yamlBuilder = new StringBuilder(
			"resourceGroups:\n  <RESOURCE_GROUP>:\n    resources:\n      <HOSTNAME-ID>:\n        attributes:\n"
		);
		yamlBuilder.append("          host.name: <HOSTNAME> # Change with actual host name\n");
		yamlBuilder.append("          host.type: ").append(hostType).append("\n");
		yamlBuilder
			.append("        connectors: [ +")
			.append(connectorId)
			.append(" ] # Optional, to load only this connector\n")
			.append(PROTOCOLS_SECTION);

		if (technologies.contains(TechnologyType.HTTP)) {
			cli.append(" --https --http-port 443 -u USERNAME");
			yamlBuilder.append("          http:\n");
			yamlBuilder.append("            https: true\n");
			yamlBuilder.append("            port: 443 # or probably something else\n");
			appendYamlUsernameAndPassword(yamlBuilder);
		}

		if (technologies.contains(TechnologyType.IPMI)) {
			cli.append(" --ipmi -u USER");
			yamlBuilder.append("          ipmi:\n");
			appendYamlUsernameAndPassword(yamlBuilder);
			yamlBuilder.append("\n      # IPMI on Windows is accessed through WMI:\n");
			yamlBuilder.append("      <WIN_HOSTNAME-ID>:\n        attributes:\n");
			yamlBuilder.append("          host.name: <WIN_HOSTNAME>\n");
			yamlBuilder.append("          type: win\n");
			yamlBuilder.append(PROTOCOLS_SECTION);
			yamlBuilder.append(WMI_SECTION);
			appendYamlUsernameAndPassword(yamlBuilder);
			yamlBuilder.append("\n      # IPMI on Linux is accessed through SSH:\n");
			yamlBuilder.append("      <LINUX_HOSTNAME-ID>:\n        attributes:\n");
			yamlBuilder.append("          host.name: <LINUX_HOSTNAME>\n");
			yamlBuilder.append("          type: linux\n");
			yamlBuilder.append(PROTOCOLS_SECTION);
			yamlBuilder.append("          ssh:\n");
			appendYamlUsernameAndPassword(yamlBuilder);
			yamlBuilder.append("            userSudo: true\n");
		}

		if (technologies.contains(TechnologyType.SNMP)) {
			cli.append(" --snmp v2c --community public");
			yamlBuilder.append("          snmp:\n");
			yamlBuilder.append("            version: v2c # Read documentation for v1, v2c and v3\n");
			yamlBuilder.append("            community: public # or probably something more secure");
		}

		if (technologies.contains(TechnologyType.COMMAND_LINES)) {
			if (OsType.WINDOWS.getPossibleHostType().equals(hostType)) {
				cli.append(" --wmi -u USER");
				yamlBuilder.append(WMI_SECTION);
				appendYamlUsernameAndPassword(yamlBuilder);
			} else {
				cli.append(" --ssh -u USER");
				yamlBuilder.append("          ssh:\n");
				appendYamlUsernameAndPassword(yamlBuilder);
				if (!sudoCommands.isEmpty()) {
					cli.append(" --sudo-command-list ");
					cli.append(String.join(",", sudoCommands));
					yamlBuilder.append("            useSudo: true\n");
					yamlBuilder.append("            useSudoCommands: [ \"");
					yamlBuilder.append(String.join("\", \"", sudoCommands));
					yamlBuilder.append("\" ]\n");
				}
			}
		}

		if (technologies.contains(TechnologyType.WMI)) {
			cli.append(" --wmi -u USER");
			yamlBuilder.append(WMI_SECTION);
			appendYamlUsernameAndPassword(yamlBuilder);
		}

		if (technologies.contains(TechnologyType.WBEM)) {
			cli.append(" --wbem -u USER");
			yamlBuilder.append("          wbem:\n");
			yamlBuilder.append("            protocol: https\n");
			yamlBuilder.append("            port: 5989\n");
			appendYamlUsernameAndPassword(yamlBuilder);
		}

		if (technologies.contains(TechnologyType.SQL)) {
			cli.append(" --jdbc -u USER --jdbc-url URL");
			yamlBuilder.append("          jdbc:\n");
			yamlBuilder.append("            port: <PORT>\n");
			yamlBuilder.append("            database: <DB_NAME>\n");
			yamlBuilder.append("            type: <type>\n");
			yamlBuilder.append("            url: <URL>\n");
			appendYamlUsernameAndPassword(yamlBuilder);
		}

		// Connector variable
		if (connectorVariables != null && !connectorVariables.isEmpty()) {
			yamlBuilder.append("        additionalConnectors:\n");
			yamlBuilder.append("          " + connectorId);
			yamlBuilder.append(": # Unique ID. Use 'uses' if different from the original connector ID\n");
			yamlBuilder.append("            uses: " + connectorId);
			yamlBuilder.append(" # Optional - Original ID if not in key\n");
			yamlBuilder.append("            force: true");
			yamlBuilder.append(" # Optional (default: true); false for auto-detection only\n");
			yamlBuilder.append("            variables:\n");
			connectorVariables
				.iterator()
				.forEachRemaining(variable -> {
					yamlBuilder.append(String.format("              %s: %s", variable, "<VALUE>"));
					yamlBuilder.append(" # Replace with desired value.\n");
				});
		}
		// CLI
		sink.section3();
		sink.sectionTitle3();
		sink.text("CLI");
		sink.sectionTitle3_();
		SinkHelper.insertCodeBlock(sink, "batch", cli.toString());
		sink.section3_();

		// YAML Configuration
		sink.section3();
		sink.sectionTitle3();
		sink.text("metricshub.yaml");
		sink.sectionTitle3_();
		SinkHelper.insertCodeBlock(sink, "yaml", yamlBuilder.toString());
		sink.section3_();

		sink.section2_();
	}

	/**
	 * Determines the possible host type based on the provided list of operating system types and set of technology.
	 *
	 * @param osTypes      The list of operating system types.
	 * @param technology The set of technology associated with the host.
	 * @return The determined host type.
	 */
	private String determinePossibleHostType(final List<String> osTypes, final Set<TechnologyType> technologies) {
		// Special case: for IPMI source, always use hostType = "oob"
		if (technologies.contains(TechnologyType.IPMI)) {
			return OsType.OOB.getPossibleHostType();
		}

		return osTypes
			.stream()
			.map(OsType::detect)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(OsType::getPossibleHostType)
			.findFirst()
			.orElse(OsType.LINUX.getPossibleHostType());
	}

	/**
	 * Appends a username and password configuration example to the provided YAML builder.
	 *
	 * @param yamlBuilder The {@link StringBuilder} representing the YAML configuration to which the example will be appended.
	 */
	private void appendYamlUsernameAndPassword(final StringBuilder yamlBuilder) {
		yamlBuilder
			.append("            username: <USERNAME> # Change with actual credentials\n")
			.append("            password: <PASSWORD> # Encrypted using metricshub-encrypt\n");
	}

	/**
	 * Produces content related to connector supersession information.
	 * <p>
	 * This method generates content indicating whether the current connector is superseded by other connectors or
	 * if it supersedes other connectors. If the connector is superseded, a warning message is displayed along with
	 * a list of connectors that supersede it. If the connector supersedes others, an informational message is provided
	 * along with a list of connectors that it supersedes.
	 * </p>
	 * @param sink                    The sink used for generating content.
	 * @param supersededMap           Map of superseded connectors.
	 * @param connectorJsonNodeReader The reader for connector

	 */
	private void produceSupersedesContent(
		final Sink sink,
		final Map<String, List<String>> supersededMap,
		final ConnectorJsonNodeReader connectorJsonNodeReader
	) {
		// Superseded?
		if (supersededMap.containsKey(connectorId)) {
			final String textWarningCssClassName = "text-warning";
			sink.paragraph(SinkHelper.setClass(textWarningCssClassName));
			sink.rawText(SinkHelper.glyphIcon("warning-sign"));
			sink.text(" This connector is superseded by: ");
			sink.paragraph_();
			sink.list();
			supersededMap
				.get(connectorId)
				.forEach(supersedingConnectorId -> {
					sink.listItem(SinkHelper.setClass(textWarningCssClassName));
					sink.link(SinkHelper.buildPageFilename(supersedingConnectorId), SinkHelper.setClass(textWarningCssClassName));
					sink.text(supersedingConnectorId);
					sink.link_();
					sink.listItem_();
				});
			sink.list_();
		}

		// Superseding?
		final List<String> supersedes = connectorJsonNodeReader.getSupersedes();
		if (supersedes != null && !supersedes.isEmpty()) {
			sink.paragraph();
			sink.rawText(SinkHelper.glyphIcon("info-sign"));
			sink.text(" This connector supersedes: ");
			sink.paragraph_();
			sink.list();
			supersedes
				.stream()
				.filter(Objects::nonNull)
				.filter(value -> !value.isBlank())
				.forEach(supersededConnectorId -> {
					sink.listItem();
					sink.link(SinkHelper.buildPageFilename(supersededConnectorId));
					sink.text(supersededConnectorId);
					sink.link_();
					sink.listItem_();
				});

			sink.list_();
		}
	}

	/**
	 * Produces content related to sudo commands and privilege escalation based on the information
	 * retrieved from the {@code ConnectorJsonNodeReader}.
	 *
	 * @param sink         The sink used for generating content.
	 * @param sudoCommands The list of sudo commands.
	 */
	private void produceSudoCommandsContent(final Sink sink, final List<String> sudoCommands) {
		// Requires root/sudo?
		if (!sudoCommands.isEmpty()) {
			final StringBuilder sudoersContent = new StringBuilder();

			sink.paragraph();
			String commandWord = new ChoiceFormat("1#command|1<commands").format(sudoCommands.size());
			sink.text("This connector requires advanced privileges on the managed host for the " + commandWord + " below:");
			sink.paragraph_();

			// What is the typical account for running commands?
			final String account = "metricshub";

			sink.list();
			for (String sudoCommand : sudoCommands) {
				sink.listItem();
				sink.rawText("<code>" + sudoCommand + "</code>");
				sink.listItem_();
				sudoersContent.append(account).append(" ALL=(root) NOPASSWD: ").append(sudoCommand).append("\n");
			}
			sink.list_();

			sink.paragraph();
			sink.text("This connector therefore needs to run as ");
			sink.bold();
			sink.text("root");
			sink.bold_();
			sink.text(" or you need to configure a privilege-escalation mechanism like ");
			sink.rawText("<code>sudo</code>");
			sink.text(
				String.format(" on the managed host to allow the monitoring account to run the %s listed above.", commandWord)
			);
			sink.paragraph_();

			// /etc/sudoers sample
			sink.paragraph();
			sink.text("Sample of ");
			sink.bold();
			sink.text("/etc/sudoers");
			sink.bold_();
			sink.text(String.format(" to allow the above %s to be run as ", commandWord));
			sink.bold();
			sink.text("root");
			sink.bold_();
			sink.text(" by the ");
			sink.bold();
			sink.text(account);
			sink.bold_();
			sink.text(" account:");
			sink.lineBreak();
			SinkHelper.insertCodeBlock(sink, "bash", sudoersContent.toString());
			sink.paragraph_();
		}
	}

	/**
	 * Produces a section of text for the connector variable, including its description and default value,
	 * and writes it to the provided sink.
	 *
	 * @param sink            The sink used for generating content.
	 * @param defaultVariable The variable containing the description and default value to be formatted and output.
	 */
	private void produceVariableSection(final Sink sink, ConnectorDefaultVariable defaultVariable) {
		final String variableDescription = defaultVariable.getDescription();
		final String variableDefaultValue = defaultVariable.getDefaultValue();

		final String defaultDescriptionString = variableDescription != null
			? String.format(": %s", variableDescription)
			: "";
		final String defaultValueString = variableDefaultValue != null
			? String.format("(default: <code>%s</code>)", variableDefaultValue)
			: "";
		sink.rawText(String.format("%s %s", defaultDescriptionString, defaultValueString));
	}
}
