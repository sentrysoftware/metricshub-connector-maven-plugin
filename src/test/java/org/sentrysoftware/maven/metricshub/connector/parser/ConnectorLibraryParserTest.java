package org.sentrysoftware.maven.metricshub.connector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

class ConnectorLibraryParserTest {

	@Test
	void testParse() throws IOException {
		final ConnectorLibraryParser connectorLibraryParser = new ConnectorLibraryParser();
		final Map<String, JsonNode> connectorMap = connectorLibraryParser.parse(
			Path.of("src", "test", "resources", "connector")
		);
		assertEquals(1, connectorMap.size());
		final JsonNode connector = connectorMap.get("MIB2");
		assertNotNull(connector);

		// Test the ConstantsProcessor
		verifyConstantsProcessorBehavior(connector);

		// Test the ExtendsProcessor
		verifyExtendsProcessorBehavior(connector);
	}

	/**
	 * Verifies the behavior of the {@link ConstantsProcessor} which has already processed this connector.
	 * The method checks if the "_OID" constant is resolved correctly in the detection criteria.
	 *
	 * @param connector The JsonNode representing the connector.
	 */
	private void verifyConstantsProcessorBehavior(final JsonNode connector) {
		// The _OID constant is resolved correctly
		final ArrayNode criteria = (ArrayNode) connector.get("connector").get("detection").get("criteria");
		assertNotNull(criteria);
		assertEquals("1.3.6.1.2.1.2.2.1", criteria.get(0).get("oid").asText());
	}

	/**
	 * Verifies the behavior of the ExtendsProcessor which has already processed this connector.
	 * The method checks if the metrics node from Hardware.yaml is available in the MIB2 connector.
	 * It also tests translations and the mapping section of the discovery and collect jobs.
	 *
	 * @param connector The JsonNode representing the connector.
	 */
	private void verifyExtendsProcessorBehavior(final JsonNode connector) {
		// The metrics node that comes from Hardware.yaml is available in the MIB2 connector
		final JsonNode metricsDefinition = connector.get("metrics");
		assertNotNull(metricsDefinition);

		// Test the translations
		final JsonNode translationTable1 = connector.get("translations").get("PortTypeTranslationTable");
		assertNotNull(translationTable1);
		assertEquals("Ethernet", translationTable1.get("7").asText());
		assertEquals("FC Port", translationTable1.get("56").asText());

		final JsonNode translationTable2 = connector.get("translations").get("PortStatusTranslationTable");
		assertNotNull(translationTable2);
		assertEquals("ok", translationTable2.get("1").asText());
		assertEquals("degraded", translationTable2.get("3").asText());
		assertEquals("failed", translationTable2.get("7").asText());

		// Test the mapping section of the discovery job
		final JsonNode discoveryMapping = connector.get("monitors").get("network").get("discovery").get("mapping");
		assertNotNull(discoveryMapping);

		// Test attributes
		final JsonNode discoveryAttributes = discoveryMapping.get("attributes");
		assertNotNull(discoveryAttributes);

		// Test individual attributes
		assertEquals("$1", discoveryAttributes.get("id").asText());
		assertEquals("$7", discoveryAttributes.get("__display_id").asText());
		assertEquals("$4", discoveryAttributes.get("physical_address").asText());
		assertEquals("MAC", discoveryAttributes.get("physical_address_type").asText());
		assertEquals("$3", discoveryAttributes.get("device_type").asText());
		assertEquals("enclosure", discoveryAttributes.get("hw.parent.type").asText());
		assertEquals("${awk::sprintf(\"%s (%s)\", $7, $3)}", discoveryAttributes.get("name").asText());

		// Test the mapping section of the collect job
		final JsonNode collectMapping = connector.get("monitors").get("network").get("collect").get("mapping");
		assertNotNull(collectMapping);

		// Test attributes and metrics
		final JsonNode collectAttributes = collectMapping.get("attributes");
		assertNotNull(collectAttributes);
		assertEquals("$1", collectAttributes.get("id").asText());

		final JsonNode collectMetrics = collectMapping.get("metrics");
		assertNotNull(collectMetrics);
		assertEquals("$4", collectMetrics.get("hw.status{hw.type=\"network\"}").asText());
		assertEquals("legacyLinkStatus($6)", collectMetrics.get("hw.network.up").asText());
		assertEquals("megaBit2Bit($16)", collectMetrics.get("hw.network.bandwidth.limit").asText());
		assertEquals("$10", collectMetrics.get("hw.errors{hw.type=\"network\"}").asText());
		assertEquals("$8", collectMetrics.get("hw.network.packets{direction=\"receive\"}").asText());
		assertEquals("$12", collectMetrics.get("hw.network.packets{direction=\"transmit\"}").asText());
		assertEquals("$7", collectMetrics.get("hw.network.io{direction=\"receive\"}").asText());
		assertEquals("$11", collectMetrics.get("hw.network.io{direction=\"transmit\"}").asText());
	}
}
