# MetricsHub Connector Maven Plugin

This is a Maven Report plugin, which is invoked by Maven's site plugin in the `site` lifecycle.

This plugin is designed to be used with the [MetricsHub Community Connectors](https://github.com/sentrysoftware/metricshub-community-connectors).

It reads the connector files from a source directory (E.g. `./src/main/connector`), parses the `.yaml` files and produces the corresponding Reference Guide, as a set of HTML documents (through Doxia's Sink API), which is integrated into the project's documentation.

## How to Use

Add the plugin to the `<reporting>` element in the `pom.xml` file of the **MetricsHub Community Connectors** project:

```xml
<project>
	...
	<reporting>
		<plugins>
			<!-- MetricsHub Connector Maven Plugin -->
			<plugin>
				<groupId>${project.groupId}</groupId>
				<artifactId>${project.artifactId}</artifactId>
				<version>${project.version}</version>
			</plugin>

	</reporting>
	...
</project>
```

To ensure that the **MetricsHub Community Connectors** project's documentation incorporates the [Sentry Maven Skin](https://sentrysoftware.github.io/sentry-maven-skin/), make sure to include a `./src/site/site.xml` file with the following configuration:

```xml
<project name="\${project.name}">

	<skin>
		<groupId>org.sentrysoftware.maven</groupId>
		<artifactId>sentry-maven-skin</artifactId>
		<version>6.2.00</version>
	</skin>

	<body>

		<menu name="MetricsHub Community Connectors">
			<item name="Supported Platforms" href="platform-requirements.html " />
		</menu>

		<menu name="Reference">
			<item name="Reference" href="metricshub-connector-reference.html"/>
		</menu>

	</body>

</project>
```

## Help

As any Maven plugin, the online help provides you with all the necessary information about it:

```sh
mvn ${project.groupId}:${project.artifactId}:help
```

## How to Build

You can build the plugin with the usual command:
```sh
$ mvn verify
```

## How to Run

In order to run your plugin, you will need to setup the **MetricsHub Community Connectors** project as described above.

First, from the root directory of your `${project.artifactId}` project, install the plugin in your local repository with:
```sh
$ mvn install
```

Then, from the root directory of the **MetricsHub Community Connectors** project, generate the project's documentation with:
```sh
$ mvn clean site -o
```

The `-o` flag is to make sure Maven uses the version of the `${project.artifactId}` that you just installed in your local repository (and not the one from a remote repository).
