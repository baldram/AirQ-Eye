# Air Quality Eye [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4444dc7f4e664f8b9ae6e0f3dcab539c)](https://www.codacy.com/manual/baldram/AirQ-Eye?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=baldram/AirQ-Eye&amp;utm_campaign=Badge_Grade)
Gather and visualize air quality data from external sources on a map 
for Poland (including recent sensor data from [Luftdaten](https://luftdaten.info/en/home-en/) and other data sources)

## Requirements

JDK13, Apache Maven.

## Run and test backend

The current implementation phase has backend only.

```console
$ mvnw install
$ mvnw spring-boot:run -pl backend
```

Further open the following URL in your browser to see JSON 
data: `http://localhost:9000/measurements`

## Detailed documentation

More details and contribution guide to be found in the [Detailed Documentation](/docs) section.
