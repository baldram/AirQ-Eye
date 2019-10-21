# Air Quality Eye [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4444dc7f4e664f8b9ae6e0f3dcab539c)](https://www.codacy.com/manual/baldram/AirQ-Eye?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=baldram/AirQ-Eye&amp;utm_campaign=Badge_Grade)
Gather and visualize air quality data from external sources on a map for Poland (including recent sensor data from https://luftdaten.info/en/home-en/)

# Requirements

In order to use IntelliJ IDEA, please install the [Lombok plugin](https://github.com/mplushnikov/lombok-intellij-plugin).
After this you have to ensure that "Enable annotation processing" checkbox is ticked under:
Preferences → Compiler → Annotation Processors, as it is described https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and-intellij-cannot-find-symbol-log .

# Run and test backend 

```
$ mvnw install
$ mvnw spring-boot:run -pl backend
```

Further open in your browser to see JSON data:
http://localhost:9000/measurements


