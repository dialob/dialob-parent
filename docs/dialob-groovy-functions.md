# Dialob groovy functions setup

## About

This document gives overview how to set up groovy implementation of dialob DEL functions. This allows to create functions and add them at start-up time into dialob.

## Configuration properties

Location of groovy functions can be provided by following dialob property: `dialob.function.groovy.locations` which accepts list of locations. Locations can use [Spring resource](https://docs.spring.io/spring-framework/reference/core/resources.html) notation to specify location of groovy files. 

## Docker-compose configuration example

### Create groovy implementation file

E.g. `dialob-functions/Mapping.groovy` with following content:

```
import io.dialob.groovy.DialobDDRLFunction

class Mapping {
  
  @DialobDDRLFunction
  static String mappingFunction(String val) {
    switch(val) {
      case 'value1': return "4";
      case 'value2': return "10";
      case 'value3': return "20";
      case 'value4': return "30";
      default: return "10"
    }
  }
}
```

### Map implementation file directory to dialob in docker-compose file and add environment entry

```
  dialob-api:
    image: @resys/dialob-boot
    environment:
      ...
      - DIALOB_FUNCTION_GROOVY_LOCATIONS_0_=file:/java/functions/Mapping.groovy
    volumes:
      - ./dialob-functions:/java/functions/
```

Same should be done for both dialob and session services.

## Building functions into docker image

To publish functions along with dialob image the function files can be added to layer on top of dialob image, thus avoiding need to provide functions separately.

E.g. create following Docker build file `Dockerfile`:

```
FROM resys/dialob-boot

WORKDIR /java/functions
COPY ./dialob-functions ./
WORKDIR /java
ENV DIALOB_FUNCTION_GROOVY_LOCATIONS_0_=file:/java/functions/Mapping.groovy
```
