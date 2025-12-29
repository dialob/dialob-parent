# API Migration Guide: Immutable Types to Records

> **From dialob-api version 2.3.2**

## Overview

Dialob API types have been migrated from Immutable-style classes to Java records. This change provides cleaner syntax, and improved performance while maintaining similar builder patterns for object construction.

## Key Changes

### Before: Immutable Types
```java
ImmutableQuestionnaire questionnaire = ImmutableQuestionnaire.builder()
    .id("id-1")
    .metadata(ImmutableQuestionnaire.Metadata.builder()
        .formId("formid")
        .build())
    .build();
```

### After: Record Types
```java
Questionnaire questionnaire = new Questionnaire.Builder()
    .id("id-1")
    .metadata(new Questionnaire.Metadata.Builder()
        .formId("formid")
        .build())
    .build();
```

## Migration Steps

### 1. Update Type References

Remove `Immutable` prefix from all type references:

```java
// Before
ImmutableQuestionnaire questionnaire = ...;
ImmutableForm form = ...;

// After
Questionnaire questionnaire = ...;
Form form = ...;
```

### 2. Update Builder Instantiation

Change from static `builder()` method to `new Builder()` constructor:

```java
// Before
ImmutableQuestionnaire.builder()

// After
new Questionnaire.Builder()
```

### 3. Update Nested Types

Nested types (like `Metadata`) are now inner records and follow the same pattern:

```java
// Before
ImmutableQuestionnaire.Metadata.builder()

// After
new Questionnaire.Metadata.Builder()
```

### 4. Update Temporal Types

All temporal fields have been migrated from `java.util.Date` to `java.time.Instant`:

```java
// Before
Date created = new Date();
ImmutableQuestionnaire questionnaire = ImmutableQuestionnaire.builder()
    .created(created)
    .build();

// After
Instant created = Instant.now();
Questionnaire questionnaire = new Questionnaire.Builder()
    .created(created)
    .build();
```

### 5. Update Accessor Methods

Records use direct field names as accessors instead of bean-style `get*` methods:

```java
// Before (bean-style)
String id = questionnaire.getId();
String formId = questionnaire.getMetadata().getFormId();
Instant created = questionnaire.getCreated();

// After (record-style)
String id = questionnaire.id();
String formId = questionnaire.metadata().formId();
Instant created = questionnaire.created();
```

**Note:** For backward compatibility, the old `get*` methods are still available but are **deprecated** and will be removed in a future version. Migrate to the new accessor style as soon as possible.

### 6. Update Copy Operations

The `toBuilder()` pattern has been replaced with the `from()` method on the builder:

```java
// Before
Questionnaire updated = questionnaire.toBuilder()
    .id("new-id")
    .build();

// After
Questionnaire updated = new Questionnaire.Builder().from(questionnaire)
    .id("new-id")
    .build();
```

## Benefits

- **Cleaner code**: Records provide concise, readable syntax
- **Better immutability**: Records are immutable by design
- **Improved performance**: Records are optimized by the JVM
- **Standard Java**: Uses modern Java features instead of third-party libraries
- **Better serialization**: Records work seamlessly with modern serialization frameworks

## Common Patterns

### Creating New Instances

```java
Questionnaire questionnaire = new Questionnaire.Builder()
    .id("q-123")
    .metadata(new Questionnaire.Metadata.Builder()
        .formId("form-456")
        .created(Instant.now())
        .build())
    .build();
```

### Accessing Fields

```java
// Use record-style accessors (preferred)
String id = questionnaire.id();
String formId = questionnaire.metadata().formId();
Instant created = questionnaire.metadata().created();

// Old bean-style accessors still work but are deprecated
String id = questionnaire.getId();  // deprecated, will be removed
```

### Copying with Modifications

```java
Questionnaire modified = new Questionnaire.Builder().from(original)
    .status(QuestionnaireStatus.COMPLETED)
    .build();
```

### Working with Collections

```java
Form form = new Form.Builder()
    .id("form-1")
    .metadata(new Form.Metadata.Builder()
        .label("Survey")
        .build())
    .data(List.of(
        new FormItem.Builder()
            .id("item-1")
            .type("text")
            .build()
    ))
    .build();
```

## Breaking Changes

1. **Type names**: Remove `Immutable` prefix from all types
2. **Builder creation**: Use `new Builder()` instead of static `builder()`
3. **Temporal types**: Replace `java.util.Date` with `java.time.Instant`
4. **Accessor methods**: Use `field()` instead of `getField()` (deprecated `get*` methods available temporarily)
5. **Copy pattern**: Use `new Builder().from(instance)` instead of `toBuilder()`

## Troubleshooting

### Compilation Errors

If you see errors like `cannot find symbol: class ImmutableQuestionnaire`:
- Replace `ImmutableXxx` with `Xxx`
- Update builder instantiation from `.builder()` to `new Builder()`

### Date to Instant Conversion

If you need to convert existing `Date` objects to `Instant`:

```java
// Convert Date to Instant
Date date = ...;
Instant instant = date.toInstant();

// Convert Instant to Date (if needed for legacy APIs)
Instant instant = ...;
Date date = Date.from(instant);

// Parse from timestamp
long timestamp = ...;
Instant instant = Instant.ofEpochMilli(timestamp);
```

### Runtime Serialization Issues

Records are serialized differently than traditional classes:
- Ensure your serialization framework supports Java records
- Jackson 2.12+ has native record support
- Gson requires additional configuration for records
- `Instant` serialization: Most frameworks serialize `Instant` as ISO-8601 strings by default

## Additional Resources

- Java Records documentation: [JEP 395](https://openjdk.org/jeps/395)
- Builder pattern with records
- Serialization framework compatibility guides
