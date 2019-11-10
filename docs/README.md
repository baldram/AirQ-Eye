# Air Quality Eye [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4444dc7f4e664f8b9ae6e0f3dcab539c)](https://www.codacy.com/manual/baldram/AirQ-Eye?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=baldram/AirQ-Eye&amp;utm_campaign=Badge_Grade)
Gather and visualize air quality data from external sources on a map 
for Poland (including recent sensor data from [Luftdaten](https://luftdaten.info/en/home-en/) and other data sources)

## Run and test backend 

The current implementation phase has backend only.

```console
$ mvnw install
$ mvnw spring-boot:run -pl backend
```

Further open the following URL in your browser to see JSON 
data: `http://localhost:9000/measurements`

## A tech stack

*   Build tool: Apache Maven,

*   Backend: Spring Boot, Lombok, JDK 13, Mapstruct,

*   Frontend: it's not yet decided (it's gonna be some popular 
and lightweight JS framework supported by GWT JsInterop or J2CL 
for transpiling Java to JavaScript). 

## Requirements

In order to use IntelliJ IDEA, please install the 
[Lombok plugin](https://github.com/mplushnikov/lombok-intellij-plugin).
After this you have to ensure that "Enable annotation processing" 
checkbox is ticked under:
Preferences → Compiler → Annotation Processors, as it is described 
[here](https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and-intellij-cannot-find-symbol-log).

## Contribution

### Coding Conventions

#### Style Guide 

The project adheres to the [Google Style Guide](https://google.github.io/styleguide/). 
The rules are intended to improve the readability of code and make it consistent.

*   [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html),
    
*   [Checkstyle Rules configuration file](style-guide/checkstyle.xml) that checks the Google coding conventions,

*   Additionally Findbugs and PDM rules to be found in [docs](style-guide) folder,

*   [Google HTML/CSS Style Guide](https://google.github.io/styleguide/htmlcssguide.xml),

*   [Google JavaScript Style Guide](https://google.github.io/styleguide/javascriptguide.xml).

#### Setting up code formatting 

Please configure code style by importing Google Code Style Scheme depending on editor you use:

*   [IntelliJ Code Style Scheme](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml) 
Please download the file. Then under File → Settings → Editor → Code Style. There in Scheme settings 
(settings icon on right side) → import schemes → Intellij IDEA code style XML. Select the XML downloaded 
in the first step: `intellij-java-google-style.xml`. 

*   [Eclipse Code Style Scheme](https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml)

#### Static Code Analysis

The SpotBugs Analyzer (formerly known as FindBugs) was installed to ensure the quality of the project.

To perform the check please run the following:

```shell script
$ cd backend\
$ mvn clean compile
$ mvn com.github.spotbugs:spotbugs-maven-plugin:3.1.12.2:check
```

The expected result is:

```shell script
[INFO] --- spotbugs-maven-plugin:3.1.12.2:check (default-cli) @ backend ---
[INFO] BugInstance size is 0
[INFO] Error size is 0
[INFO] No errors/warnings found
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

Use GUI for easier warnings analysis if there any:

```shell script
$ mvn com.github.spotbugs:spotbugs-maven-plugin:3.1.12.2:gui
``` 

More options described in [plugin SpotBugs Maven Plugin documentation](https://github.com/spotbugs/spotbugs-maven-plugin) 
and [SpotBugs Analyzer documentation](https://spotbugs.readthedocs.io/en/latest/index.html).

### Markdown Style Guide
*   please find the [remark-lint](https://github.com/remarkjs/remark-lint#using-remark-to-fix-your-markdown) 
(for Markdown syntax validation - ensuring that the project's documentation 
will be correctly rendered by all the different implementations of markdown parser).<br />
From [docs](https://github.com/baldram/AirQ-Eye/tree/master/docs/) folder, please  Execute `npm install` and further `npm run lint-md` to validate project's 
documentation.<br />Integration with IntelliJ suppose to be also [possible](https://www.jetbrains.com/help/idea/eslint.html) .
