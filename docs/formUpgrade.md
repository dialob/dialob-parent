**DRAFT**

# Form data upgrade

## Problem

Form data format has been evolving. Quite a lot of projects already use forms that are under control of customers. We need to have upgrade path to those forms. There are two kinds of upgrades that might be needed:

* Upgrade of Dialob API version
* Application specific upgrades in case of contracts about using custom metadata / props etc. is changing

## Ideas

* Have an API version number (running number is enough) in form data
* Backend has also version number that it supports internally
* Backend has a number of cumulative conversion scripts/operations somewhat like Flyway
* Whenever a form gets requested from forms service, version numbers are compared and necessary scripts run to convert form data into supported version on the fly. As form data can come from a version tag, conversion result is not persisted at this point.
* Conversion result is persisted when form is stored through API in normal way (through Composer etc.)

The idea behind this logic is that we don't need to do "all at once" batch conversion that is a maintenance chore and can cause full service outage if some form conversion fails for some reason. Also we avoid touching tagged versions of forms. Additionally, any issues with conversion can be easily fixed. Conversion scripts are fast and results are cached by service, so performance penalty is expected to be minimal

## To think 

### Implementation

Logically, JS should be the best option for processing JSON data. Running javascript in JVM context (as Dialob backend / forms service in Java) can be problematic. Java 8 includes Nashorn JS engine which is quite old ES standard and also deprecated in Java 11, likely removed in the near future. Going with this is probably not a good idea. Official replacement for this seems to be GraalVM-based JS runtime. Going to GraalVM is a bigger decision which is likely out of scope for the time being but might be a good idea in the long run for other reasons.

Find a JVM library (Java / Kotlin / Groovy / whatever) that could help with raw (schemaless) JSON processing. Working directly with JSONObject is probably too cumbersome.

**Look into these:**

https://jsoniter.com/

https://bolerio.github.io/mjson/

https://github.com/json-path/JsonPath


Also options for implementing this (conversion or the whole forms service) outside of JVM could also be considered, but this wouldn't be a quick solution and has its own risks.

### Other questions

How to handle application specific form updates? These are independent from API version upgrades and might concern only for 'LATEST' version of forms, not necessarily tagged versions. Examples cases:
* Contract how style classes, item props, view types, context variables etc. is changed
* Some additional structure need to be added to forms, for example comment fields for all groups

If the changes are minor, it can be told to customers to do those manually in composer or do it ourselves as a maintenance work. But there are existing cases where large amount additional fields need to be added in controlled way to several production forms where we don't even have direct access ourselves.
