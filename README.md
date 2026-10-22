# Playwright Java Accelerator

A Java and TestNG starter framework for browser tests, with page objects,
YAML configuration, parallel suites, retries, test data and Allure reports.
Use it as reusable infrastructure or to explore the bundled examples.
The public-site examples depend on websites this repository does not control;
the controlled smoke suite provides a first run without those dependencies.

![Framework overview](https://github.com/user-attachments/assets/dd8a014a-8002-4568-a512-b0f818905b8e)

## Quickstart

Use JDK 21 with `JAVA_HOME` set. The existing Java 17 bytecode target is retained.
Git and internet access are needed for the initial Maven and browser downloads.
Run these commands from the repository root after cloning:

```bash
git clone https://github.com/yashwant-das/qa-java-playwright.git
cd qa-java-playwright
./mvnw test-compile
./mvnw exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml -Denvironment=local
```

The smoke suite runs a real headless Chromium browser against controlled HTML.
Expect zero failures and `BUILD SUCCESS`. No credentials or running application
are needed. On Windows use `mvnw.cmd` and quote `-D` arguments in PowerShell.
See [setup](docs/development.md#setup) for JDK selection and platform details.

## Usage

```bash
# All existing examples, against public websites
./mvnw test -Dbrowser.headless=true

# Selected public-site suite, with retry and parallel listeners
./mvnw test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml -Dbrowser.headless=true

# Generate the Allure report from the most recent run
./mvnw allure:report
```

[Development](docs/development.md) covers browser selection, overrides, reports,
adding tests and troubleshooting. [Examples](docs/examples.md) lists the demos.
The YAML default is headed Chromium; install Firefox or WebKit from the same
Playwright package before selecting them with `-Dbrowser.type`.

## Verification

```bash
./mvnw verify -DsuiteXmlFile=src/test/resources/suites/unit.xml
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml -Denvironment=local
```

`verify` compiles the test-only framework, runs configuration unit tests and Java
static analysis. The smoke suite additionally proves real-browser behavior on
controlled pages. The full public-site suite and macOS/Windows CI matrix provide
separate integration evidence; a compile or discovered test count proves neither.
[Quality gates](docs/development.md#quality-gates-and-hooks) includes Markdown,
text/config checks and pre-commit setup.

## Documentation

- [Development](docs/development.md): setup, configuration, test commands, reports and troubleshooting.
- [Architecture](docs/architecture.md): layout, execution flow, shared layers and boundaries.
- [Examples](docs/examples.md): the public-site demo catalog, kept separate for quick lookup.

## License

MIT. See [LICENSE](LICENSE).
