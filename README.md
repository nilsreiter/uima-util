# uima-util
Utility components for Apache UIMA


## Availability

uima-util is licensed under the Apache License 2.0 and is available via Maven Central.
If you use Maven for your build environment, then you can
add uima-util as a dependency to your pom.xml file with the following:

```
<dependency>
  <groupId>de.unistuttgart.ims</groupId>
  <artifactId>uima-util</artifactId>
  <version>0.4.1</version>
</dependency>
```

## Components
- `ClearAnnotation`: Removes all annotations of a given type
- `MapAnnotations`: Creates new annotations with the same span as existing annotations, optionally deleting the existing ones
- `SetDocumentId`: Sets the document id
- `SetJCasLanguage`: Sets the document language
- `AnnotationUtil.trim()`: Various methods to trim annotations (removing whitespace at the front and end).
