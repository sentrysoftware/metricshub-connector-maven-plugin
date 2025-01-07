// Verify that the files have been created properly
assert new File(basedir, "target/site/index.html").isFile()

//
// Site checks for the Otel version
//

// Main page: metricshub-connectors-full-listing.html
File htmlFile = new File(basedir, "target/site/metricshub-connectors-full-listing.html")
assert htmlFile.exists() : "Main metricshub-connectors-full-listing.html page must be created"
String htmlText = htmlFile.text
assert htmlText.indexOf("MIB2Switch") > -1 : "metricshub-connectors-full-listing: MIB2Switch must be listed"
assert htmlText.indexOf("GenericSwitchEnclosure") > -1 : "metricshub-connectors-full-listing: GenericSwitchEnclosure must be listed"
assert htmlText.indexOf("GenericUPS") > -1 : "metricshub-connectors-full-listing: GenericUPS must be listed"
assert htmlText.indexOf("HyperV") > -1 : "metricshub-connectors-full-listing: HyperV must be listed"
assert htmlText.indexOf("IpmiTool") > -1 : "metricshub-connectors-full-listing: IpmiTool must be listed"
assert htmlText.indexOf("Virsh") > -1 : "metricshub-connectors-full-listing: Virsh must be listed"
assert htmlText.indexOf("LibreHardwareMonitor") > -1 : "metricshub-connectors-full-listing: LibreHardwareMonitor must be listed"
assert htmlText.indexOf("LinuxMultipath") > -1 : "metricshub-connectors-full-listing: LinuxMultipath must be listed"
assert htmlText.indexOf("LinuxIfConfigNetwork") > -1 : "metricshub-connectors-full-listing: LinuxIfConfigNetwork must be listed"
assert htmlText.indexOf("LinuxIPNetwork") > -1 : "metricshub-connectors-full-listing: LinuxIPNetwork must be listed"
assert htmlText.indexOf("lmsensors") > -1 : "metricshub-connectors-full-listing: lmsensors must be listed"
assert htmlText.indexOf("MIB2") > -1 : "metricshub-connectors-full-listing: MIB2 must be listed"
assert htmlText.indexOf("MIB2Linux") > -1 : "metricshub-connectors-full-listing: MIB2Linux must be listed"
assert htmlText.indexOf("MIB2NT") > -1 : "metricshub-connectors-full-listing: MIB2NT must be listed"
assert htmlText.indexOf("NvidiaSmi") > -1 : "metricshub-connectors-full-listing: NvidiaSmi must be listed"
assert htmlText.indexOf("DiskPart") > -1 : "metricshub-connectors-full-listing: DiskPart must be listed"
assert htmlText.indexOf("WinStorageSpaces") > -1 : "metricshub-connectors-full-listing: WinStorageSpaces must be listed"
assert htmlText.indexOf("GenBatteryNT") > -1 : "metricshub-connectors-full-listing: GenBatteryNT must be listed"
assert htmlText.indexOf("WBEMGenDiskNT") > -1 : "metricshub-connectors-full-listing: WBEMGenDiskNT must be listed"
assert htmlText.indexOf("WBEMGenHBA") > -1 : "metricshub-connectors-full-listing: WBEMGenHBA must be listed"
assert htmlText.indexOf("WBEMGenLUN") > -1 : "metricshub-connectors-full-listing: WBEMGenLUN must be listed"
assert htmlText.indexOf("WBEMGenNetwork") > -1 : "metricshub-connectors-full-listing: WBEMGenNetwork must be listed"
assert htmlText.indexOf("MySQL") > -1 : "metricshub-connectors-full-listing: MySQL must be listed"

// Check generated reference files
String directoryPath = 'target/site/connectors'
String [] fileNamesToCheck = [
    'diskpart.html',
    'genbatterynt.html',
    'genericswitchenclosure.html',
    'genericups.html',
    'hyperv.html',
    'ipmitool.html',
    'librehardwaremonitor.html',
    'linuxifconfignetwork.html',
    'linuxipnetwork.html',
    'linuxmultipath.html',
    'lmsensors.html',
    'mib2.html',
    'mib2linux.html',
    'mib2nt.html',
    'mib2switch.html',
    'nvidiasmi.html',
    'virsh.html',
    'wbemgendisknt.html',
    'wbemgenhba.html',
    'wbemgenlun.html',
    'wbemgennetwork.html',
    'winstoragespaces.html',
    'mysql.html'
]

fileNamesToCheck.each { fileName ->
    File file = new File(basedir,  "$directoryPath/$fileName")

    assert file.exists() : "File $fileName does not exist in the $directoryPath directory"
    assert htmlText.indexOf("href=\"connectors/$fileName\"") > -1 : "metricshub-connectors-full-listing: href=connectors/$fileName must be listed"
}

// Check generated reference files
String tagsDirectoryPath = 'target/site/connectors/tags'
String [] tagsFileNamesToCheck = [
    'hardware.html',
    'nvidia.html',
    'vm.html',
    'hyper-v.html',
    'hardware.html',
    'database.html',
]

String [] hardwareConnectors = [
    'HyperV',
    'IpmiTool',
    'LibreHardwareMonitor',
    'NvidiaSmi',
]

tagsFileNamesToCheck.each { fileName ->
    File file = new File(basedir,  "$tagsDirectoryPath/$fileName")

    assert file.exists() : "File $fileName does not exist in the $tagsDirectoryPath directory"
    assert htmlText.indexOf("href=\"connectors/tags/$fileName\"") > -1 : "metricshub connectors tag: href=connectors/tags/$fileName must be listed"

    // Check the hardware connectors
    if (fileName == 'hardware.html') {
        hardwareConnectors.each { connectorId -> 
            assert htmlText.indexOf("$connectorId") > -1 : "metricshub connectors tag: $connectorId must be listed"
        }
    }

}

// IpmiTool
htmlText = new File(basedir, "target/site/connectors/ipmitool.html").text
assert htmlText.indexOf("Typical platform:") > - 1 : "IPMITool: 'Typical platform:' must be present"
assert htmlText.indexOf("IPMI") > - 1 : "IPMITool: typical platform text must be present"
assert htmlText.indexOf("<a href=\"platforms/ipmi.html\">IPMI</a>") > - 1 : "IPMITool: platforms/ipmi.html link text must be present"
assert htmlText.indexOf("ipmi:") > -1 && htmlText.indexOf("wmi:") > -1 && htmlText.indexOf("ssh:") > -1 : "IpmiTool: Examples must list ipmi, wmi and ssh"
assert htmlText =~ /metricshub.*-t management.*-c \+IpmiTool.*--ipmi/ : "IpmiTool: CLI must specify -t management -c +IpmiTool --ipmi"

// IpmiTool Enclosure
assert htmlText.indexOf('enclosure') > -1 : 'IpmiTool: the enclosure monitor must be listed'
assert htmlText.indexOf('hw.enclosure.energy') > -1 : 'IpmiTool: the hw.enclosure.energy metric must be listed for the enclosure monitor'
assert htmlText.indexOf('hw.enclosure.power') > -1 : 'IpmiTool: the hw.enclosure.power metric must be listed for the enclosure monitor'
assert htmlText.indexOf('hw.status{hw.type="enclosure", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="enclosure", state="degraded|failed|ok"} metric must be listed for the enclosure monitor'
assert htmlText.indexOf('hw.status{hw.type="enclosure", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="enclosure", state="present"} metric must be listed for the enclosure monitor'

// IpmiTool Fan
assert htmlText.indexOf('fan') > -1 : 'IpmiTool: the fan monitor must be listed'
assert htmlText.indexOf('hw.fan.speed') > -1 : 'IpmiTool: the hw.fan.speed metric must be listed for the fan monitor'
assert htmlText.indexOf('hw.fan.speed.limit{limit_type="low.critical"}') > -1 : 'IpmiTool: the hw.fan.speed.limit{limit_type="low.critical"} metric must be listed for the fan monitor'
assert htmlText.indexOf('hw.fan.speed.limit{limit_type="low.degraded"}') > -1 : 'IpmiTool: the hw.fan.speed.limit{limit_type="low.degraded"} metric must be listed for the fan monitor'
assert htmlText.indexOf('hw.status{hw.type="fan", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="fan", state="degraded|failed|ok"} metric must be listed for the fan monitor'
assert htmlText.indexOf('hw.status{hw.type="fan", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="fan", state="present"} metric must be listed for the fan monitor'

// IpmiTool Temperature
assert htmlText.indexOf('temperature') > -1 : 'IpmiTool: the temperature monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="temperature", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="temperature", state="present"} metric must be listed for the temperature monitor'
assert htmlText.indexOf('hw.temperature') > -1 : 'IpmiTool: the hw.temperature metric must be listed for the temperature monitor'
assert htmlText.indexOf('hw.temperature.limit{limit_type="high.critical"}') > -1 : 'IpmiTool: the hw.temperature.limit{limit_type="high.critical"} metric must be listed for the temperature monitor'
assert htmlText.indexOf('hw.temperature.limit{limit_type="high.degraded"}') > -1 : 'IpmiTool: the hw.temperature.limit{limit_type="high.degraded"} metric must be listed for the temperature monitor'

// IpmiTool Voltage
assert htmlText.indexOf('voltage') > -1 : 'IpmiTool: the voltage monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="voltage", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="voltage", state="present"} metric must be listed for the voltage monitor'
assert htmlText.indexOf('hw.voltage') > -1 : 'IpmiTool: the hw.voltage metric must be listed for the voltage monitor'
assert htmlText.indexOf('hw.voltage.limit{limit_type="high.degraded"}') > -1 : 'IpmiTool: the hw.voltage.limit{limit_type="high.degraded"} metric must be listed for the voltage monitor'
assert htmlText.indexOf('hw.voltage.limit{limit_type="low.critical"}') > -1 : 'IpmiTool: the hw.voltage.limit{limit_type="low.critical"} metric must be listed for the voltage monitor'

// IpmiTool Power Supply
assert htmlText.indexOf('power_supply') > -1 : 'IpmiTool: the power_supply monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="power_supply", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="power_supply", state="degraded|failed|ok"} metric must be listed for the Power Supply monitor'
assert htmlText.indexOf('hw.status{hw.type="power_supply", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="power_supply", state="present"} metric must be listed for the Power Supply monitor'

// IpmiTool CPU
assert htmlText.indexOf('cpu') > -1 : 'IpmiTool: the cpu monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="cpu", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="cpu", state="degraded|failed|ok"} metric must be listed for the CPU monitor'
assert htmlText.indexOf('hw.status{hw.type="cpu", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="cpu", state="present"} metric must be listed for the CPU monitor'

// IpmiTool Memory
assert htmlText.indexOf('memory') > -1 : 'IpmiTool: the memory monitor must be listed'
assert htmlText.indexOf('hw.memory.limit') > -1 : 'IpmiTool: the hw.memory.limit metric must be listed for the Memory monitor'
assert htmlText.indexOf('hw.status{hw.type="memory", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="memory", state="degraded|failed|ok"} metric must be listed for the Memory monitor'
assert htmlText.indexOf('hw.status{hw.type="memory", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="memory", state="present"} metric must be listed for the Memory monitor'

// IpmiTool Physical Disk
assert htmlText.indexOf('physical_disk') > -1 : 'IpmiTool: the physical_disk monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="physical_disk", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="physical_disk", state="degraded|failed|ok"} metric must be listed for the Physical Disk monitor'
assert htmlText.indexOf('hw.status{hw.type="physical_disk", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="physical_disk", state="present"} metric must be listed for the Physical Disk monitor'

// IpmiTool LED
assert htmlText.indexOf('led') > -1 : 'IpmiTool: the led monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="led", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="led", state="degraded|failed|ok"} metric must be listed for the LED monitor'
assert htmlText.indexOf('hw.status{hw.type="led", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="led", state="present"} metric must be listed for the LED monitor'

// IpmiTool Other Device
assert htmlText.indexOf('other_device') > -1 : 'IpmiTool: the other_device monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="other_device", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="other_device", state="degraded|failed|ok"} metric must be listed for the Other Device monitor'
assert htmlText.indexOf('hw.status{hw.type="other_device", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="other_device", state="present"} metric must be listed for the Other Device monitor'

// IpmiTool Blade
assert htmlText.indexOf('blade') > -1 : 'IpmiTool: the blade monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="blade", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="blade", state="degraded|failed|ok"} metric must be listed for the Blade monitor'
assert htmlText.indexOf('hw.status{hw.type="blade", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="blade", state="present"} metric must be listed for the Blade monitor'

// IpmiTool Battery
assert htmlText.indexOf('battery') > -1 : 'IpmiTool: the battery monitor must be listed'
assert htmlText.indexOf('hw.status{hw.type="battery", state="degraded|failed|ok"}') > -1 : 'IpmiTool: the hw.status{hw.type="battery", state="degraded|failed|ok"} metric must be listed for the Battery monitor'
assert htmlText.indexOf('hw.status{hw.type="battery", state="present"}') > -1 : 'IpmiTool: the hw.status{hw.type="battery", state="present"} metric must be listed for the Battery monitor'

// MIB2NT
htmlText = new File(basedir, "target/site/connectors/mib2nt.html").text
assert htmlText.indexOf("This connector supersedes") > - 1 : "MIB2NT: Page must indicate which connectors are superseded"
assert htmlText.indexOf('href="wbemgennetwork.html"') > -1 : "MIB2NT: Path to superseded connector page must be listed"
assert htmlText.indexOf("SNMP") > -1 : "MIB2NT: SNMP Technology must be listed"
assert htmlText.indexOf("Microsoft Windows") > - 1 : "MIB2NT: OS type must have been properly translated"
assert htmlText.indexOf("<li></li>") == -1 : "MIB2NT: No empty list item must be found"
assert htmlText.indexOf("Metrics") > -1 : "MIB2NT: The Metrics section must be listed"

// WBEMGenNetwork
htmlText = new File(basedir, "target/site/connectors/wbemgennetwork.html").text
assert htmlText.indexOf("This connector is superseded by") > - 1 : "WBEMGenNetwork: Page must indicate this connector is superseded by another one"
assert htmlText.indexOf('href="mib2nt.html"') > -1 : "WBEMGenNetwork: Path to superseding connector page must be present"
assert htmlText.indexOf("wmi:") > -1 : "WBEMGenNetwork: Example must list wmi"
assert htmlText =~ /metricshub.*-c \+WBEMGenNetwork.*--wmi/ : "WBEMGetNetwork: CLI must specify WBEMGenNetwork and --wmi"

// lmsensors
htmlText = new File(basedir, "target/site/connectors/lmsensors.html").text
assert htmlText =~ /metricshub.*-t linux.*--ssh.*--sudo-command-list/ : "lmsensors: CLI must specify linux and ssh and use --sudo-command-list"

// MIB2
htmlText = new File(basedir, "target/site/connectors/mib2.html").text
assert htmlText.indexOf('<h3 id="description">Description</h3>') > - 1 : "MIB2: Page must indicate 'Description' as HTML H3 element"
assert htmlText.indexOf("This connector discovers the enclosure and Ethernet ports of a system equipped with an MIB-2 standard SNMP Agent.") > - 1 : "MIB2: Page must indicate a description"
assert htmlText.indexOf('<h3 id="target">Target</h3>') > - 1 : "MIB2: Page must indicate 'Target' as HTML H3 element"
assert htmlText.indexOf("Typical platform:") > - 1 : "MIB2: 'Typical platform:' must be present"
assert htmlText.indexOf("SNMP") > - 1 : "MIB2: typical platform text must be present"
assert htmlText.indexOf("Operating systems:") > - 1 : "MIB2 'Operating systems:' must be present"
assert htmlText.indexOf("Network Device") > -1 : "MIB2: operating system 'Network Device' must be present"
assert htmlText.indexOf("Out-Of-Band") > -1 : "MIB2: operating system 'Out-Of-Band' must be present"
assert htmlText.indexOf("HP-UX") > -1 : "MIB2: operating system 'HP-UX' must be present"
assert htmlText.indexOf("Storage System") > -1 : "MIB2: operating system 'Storage System' must be present"
assert htmlText.indexOf("HP OpenVMS") > -1 : "MIB2: operating system 'HP OpenVMS' must be present"
assert htmlText.indexOf("HP Tru64") > -1 : "MIB2: operating system 'HP Tru64' must be present"
assert htmlText.indexOf('<h3 id="prerequisites">Prerequisites</h3>') > - 1 : "MIB2: Page must indicate 'Prerequisites' as HTML H3 element"
assert htmlText.indexOf("Leverages:") > - 1 : "MIB2 'Leverages:' must be present"
assert htmlText.indexOf("MIB-2 Standard SNMP Agent") > - 1 : "MIB2 leverages text must be present"
assert htmlText.indexOf("Technology and protocols:") > - 1 : "MIB2 'Technology and protocols:' must be present"
assert htmlText.indexOf("SNMP") > - 1 : "MIB2 SNMP protocol must be present"
assert htmlText.indexOf('<h3 id="examples">Examples</h3>') > - 1 : "MIB2: Page must indicate 'Examples' as HTML H3 element"
assert htmlText.indexOf('<h4 id="cli">CLI</h4>') > - 1 : "MIB2: Page must indicate 'CLI' as HTML H4 element"
assert htmlText.indexOf("metricshub HOSTNAME -t network -c +MIB2 --snmp v2c --community public") > - 1 : "MIB2: Page must indicate the expected CLI example"
assert htmlText.indexOf('<h4 id="metricshub-yaml">metricshub.yaml</h4>') > - 1 : "MIB2: Page must indicate 'metricshub.yaml' as HTML H4 element"
assert htmlText.indexOf("snmp:") > - 1 : "MIB2: 'snmp:' yaml section must be present"
assert htmlText.indexOf("v2c") > - 1 : "MIB2: version 'v2c' must be present in the yaml configuration example"
assert htmlText.indexOf('<h3 id="connector-activation-criteria">Connector Activation Criteria</h3>') > - 1 : "MIB2: Page must indicate 'Connector Activation Criteria' as HTML H3 element"
assert htmlText.indexOf("1.3.6.1.2.1.2.2.1") > - 1 : "MIB2: Page must indicate OID '1.3.6.1.2.1.2.2.1'"
assert htmlText.indexOf('<h3 id="metrics">Metrics</h3>') > - 1 : "MIB2: Page must indicate 'Metrics' as HTML H3 element"

// MIB2 Network Metrics
assert htmlText.indexOf('hw.errors{hw.type="network"}') > -1 : 'MIB2: the hw.errors{hw.type="network"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.network.bandwidth.limit') > -1 : "MIB2: the hw.network.bandwidth.limit metric must be listed for the Network monitor"
assert htmlText.indexOf('hw.network.io{direction="receive"}') > -1 : 'MIB2: the hw.network.io{direction="receive"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.network.io{direction="transmit"}') > -1 : 'MIB2: the hw.network.io{direction="transmit"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.network.packets{direction="receive"}') > -1 : 'MIB2: the hw.network.packets{direction="receive"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.network.packets{direction="transmit"}') > -1 : 'MIB2: the hw.network.packets{direction="transmit"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.network.up') > -1 : "MIB2: the hw.network.up metric must be listed for the Network monitor"
assert htmlText.indexOf('hw.status{hw.type="network", state="degraded|failed|ok"}') > -1 : 'MIB2: the hw.status{hw.type="network", state="degraded|failed|ok"} metric must be listed for the Network monitor'
assert htmlText.indexOf('hw.status{hw.type="network", state="present"}') > -1 : 'MIB2: the hw.status{hw.type="network", state="present"} metric must be listed for the Network monitor'

// MIB2 Network Attributes
assert htmlText.indexOf('device_type') > -1 : "MIB2: the 'device_type' attribute must be listed for the Network monitor"
assert htmlText.indexOf('hw.parent.type') > -1 : "MIB2: the 'hw.parent.type' attribute must be listed for the Network monitor"
assert htmlText.indexOf('id') > -1 : "MIB2: the 'id' attribute must be listed for the Network monitor"
assert htmlText.indexOf('name') > -1 : "MIB2: the 'name' attribute must be listed for the Network monitor"
assert htmlText.indexOf('physical_address') > -1 : "MIB2: the 'physical_address' attribute must be listed for the Network monitor"
assert htmlText.indexOf('physical_address_type') > -1 : "MIB2: the 'physical_address_type' attribute must be listed for the Network monitor"

// Nvidia-Smi
htmlText = new File(basedir, "target/site/connectors/nvidiasmi.html").text
assert htmlText.indexOf("Nvidia") > -1 : "NvidiaSmi: Unexpected Typical platform"
assert htmlText.indexOf("Microsoft Windows, Linux") > -1 : "NvidiaSmi: Unexpected Operating Systems"
assert htmlText.indexOf("NVIDIA drivers with NVIDIA-SMI support") > -1 : "NvidiaSmi: Unexpected Leverages"
assert htmlText.indexOf("Command Lines") > -1 : "NvidiaSmi: Unexpected Technology and protocols"
assert htmlText.indexOf("<code>nvidia-smi</code>") > -1 : "NvidiaSmi: Unexpected criterion command line"

// WinStoreSpaces
htmlText = new File(basedir, "target/site/connectors/winstoragespaces.html").text
assert htmlText.indexOf("Microsoft Windows") > -1 : "WinStoreSpaces: Unexpected Typical platform"
assert htmlText.indexOf("Storage System, Microsoft Windows") > -1 : "WinStoreSpaces: Unexpected Operating Systems"
assert htmlText.indexOf("Windows Storage Spaces") > -1 : "WinStoreSpaces: Unexpected Leverages"
assert htmlText.indexOf("WMI/WinRM") > -1 : "WinStoreSpaces: Unexpected Technology and protocols"
assert htmlText.indexOf("<code>root\\Microsoft\\Windows\\Storage</code>") > -1 : "WinStoreSpaces: Unexpected namespaces"
assert htmlText.indexOf("metricshub HOSTNAME -t storage -c +WinStorageSpaces --wmi -u USER") > -1 : "WinStoreSpaces: Page must indicate the expected CLI example."

// MySQL
htmlText = new File(basedir, "target/site/connectors/mysql.html").text
assert htmlText.indexOf("MySQL") > -1 : "MySQL: Unexpected Typical platform"
assert htmlText.indexOf("Microsoft Windows, Linux") > -1 : "MySQL: Unexpected Operating Systems"
assert htmlText.indexOf("MySQL Database") > -1 : "MySQL: Unexpected Leverages"
assert htmlText.indexOf("SQL/JDBC") > -1 : "MySQL: Unexpected Technology and protocols"
assert htmlText.indexOf("metricshub HOSTNAME -t win -c +MySQL --jdbc -u USER --jdbc-url URL") > -1 : "MySQL: Page must indicate the expected CLI example."
assert htmlText.indexOf("<code>SELECT @@version_comment REGEXP 'mysql' AS is_mysql;</code>") > -1 : "MySQL: Page must indicate the activation criterion."
assert htmlText.indexOf("Expected Result:") > -1 : "MySQL: Page must indicate the Expected Result message."
assert htmlText.indexOf("<code>1</code>") > -1 : "MySQL: Page must indicate the expected result value."
assert htmlText.indexOf('<h3 id="metrics">Metrics</h3>') > - 1 : "MySQL: Page must indicate 'Metrics' as HTML H3 element"

// Verify that the metricshub-connectors-directory.html file has been created
File directoryHtmlFile = new File(basedir, "target/site/metricshub-connectors-directory.html")
assert directoryHtmlFile.exists() : "Main metricshub-connectors-directory.html page must be created"

// Read the file content
String directoryHtmlText = directoryHtmlFile.text

// Define platforms to check
def platforms = [
    [name: "IPMI", link: "connectors/platforms/ipmi.html", icon: "ipmi"],
    [name: "Ethernet Switch", link: "connectors/platforms/ethernet-switch.html", icon: "ethernet-switch"],
    [name: "Hyper-V", link: "connectors/platforms/hyper-v.html", icon: "hyper-v"],
    [name: "Hypervisors", link: "connectors/platforms/hypervisors.html", icon: "hypervisors"],
    [name: "KVM", link: "connectors/platforms/kvm.html", icon: "kvm"],
    [name: "Linux", link: "connectors/platforms/linux.html", icon: "linux"],
    [name: "Microsoft Windows", link: "connectors/platforms/microsoft-windows.html", icon: "microsoft-windows"],
    [name: "MySQL", link: "connectors/platforms/my-sql.html", icon: "my-sql"],
    [name: "Nvidia", link: "connectors/platforms/nvidia.html", icon: "nvidia"],
    [name: "QEMU", link: "connectors/platforms/qemu.html", icon: "qemu"],
    [name: "System with SNMP", link: "connectors/platforms/system-with-snmp.html", icon: "system-with-snmp"],
    [name: "UPS", link: "connectors/platforms/ups.html", icon: "ups"],
    [name: "Xen", link: "connectors/platforms/xen.html", icon: "xen"]
]

// Check each platform
platforms.each { platform ->
    // Check platform title
    assert directoryHtmlText.indexOf(platform.name) > -1 : "Platform '${platform.name}' must be listed in metricshub-connectors-directory.html"
    
    // Check platform link
    assert directoryHtmlText.indexOf("href=\"${platform.link}\"") > -1 : "Platform '${platform.name}' must link to '${platform.link}'"

    // Check platform icon
    assert directoryHtmlText.indexOf("src=\"images/platforms/${platform.icon}.png\"") > -1 : "Platform '${platform.name}' must have the correct icon '${platform.icon}.png'"
}

// Additional checks classes
assert directoryHtmlText.indexOf("class=\"connectors-badge badge\"") > -1 : "connectors-badge class must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("class=\"technology-badge badge\"") > -1 : "technology-badge class must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("class=\"platform-tile-container\"") > -1 : "platform-tile-container class must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("class=\"platform-tile\"") > -1 : "platform-tile class must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("class=\"platform-title\"") > -1 : "platform-title class must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("class=\"platform-icon\"") > -1 : "platform-icon must be present in metricshub-connectors-directory.html"
assert directoryHtmlText.indexOf("alt=\"inline\"") > -1 : "alt=\"inline\" attribute must be present in metricshub-connectors-directory.html"

// Define regex patterns to check
def regexps = [
    /class="connectors-badge badge"/,
    /class="platform-tile"/,
    /class="platform-title"/,
    /class="platform-icon"/,
    /alt="inline"/
]

// Get the number of platforms
def expectedCount = platforms.size()

// Check occurrences of each regex pattern
regexps.each { pattern ->
    def count = (directoryHtmlText =~ pattern).count // Count matches for the pattern
    assert count == expectedCount : "Expected $expectedCount occurrences of $pattern, but found $count"
}

def countContainer = (directoryHtmlText =~ /class="platform-tile-container"/).count
assert countContainer == 1 : "Expected 1 platform-tile-container class, but found $countContainer"
