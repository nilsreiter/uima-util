[![Build Status](https://travis-ci.org/nilsreiter/uima-util.svg?branch=master)](https://travis-ci.org/nilsreiter/uima-util)

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
  <version>0.6.3</version>
</dependency>
```

## Components
- `ClearAnnotation`: Removes all annotations of a given type
- `MapAnnotations`: Creates new annotations with the same span as existing annotations, optionally deleting the existing ones
- `SetDocumentId`: Sets the document id
- `SetJCasLanguage`: Sets the document language
- `WindowAnnotator`: Adds an annotation over `n` base annotations (e.g., tokens)
- `WordListTagger`: Tags all occurrences of words provided in a list
- `WordTagger`: Tag all occurrences of a single string
- `NGramTagger`: Tags all occurrences of a list of n-grams
- `ConvertBoundaryToSegmentAnnotation`: Converts boundary annotation to segment annotation
- `ConvertSegmentToBoundaryAnnotation`: Vice versa
- `AnnotationUtil`
    - `trim()`: Various methods to trim annotations (removing whitespace at the front and end).
- `WindowAnnotator`: Creates arbitrary annotations in UIMA documents, based on the length (measured in arbitrary annotation types) of the windows.
- `FixedNumberWindowAnnotator`: Creates arbitrary annotations, distributed uniformly over all base annotations (type can be specified)
- `CoNLLStyleExporter`: A component to create configurable CSV-files from annotations. Using a configuration file, it can be specified which features are to be included as columns in the output. You can also include covered annotations (e.g., tokens in a sentence) and select their respective features as well. The feature values of the covering annotation (the sentence) are repeated for every token then.

## Documentation
- [javadoc](http://nilsreiter.github.io/uima-util/)

## Build
- `mvn -DperformRelease=true deploy` to deploy to maven central.
- `mvn clean javadoc:javadoc scm-publish:publish-scm` publish javadoc to github
