# otel-cdi-faces

OpenTelemetry integration for Jakarta CDI beans and Jakarta Faces applications.

## Features
- Automatic tracing for CDI beans
- Jakarta Faces lifecycle phase tracing
- Support for both stateless and stateful (`@ViewScoped`, `@SessionScoped`) beans

## Usage

### CDI Integration
- Add this dependency to your pom.xml
```xml
<dependency>
    <groupId>net.bis5.opentelemetry</groupId>
    <artifactId>opentelemetry-cdi</artifactId>
    <version>1.0</version>
</dependency>
```
- Add one of the following annotations to your class:
  - `@Traced` for standard CDI beans
  - `@TracedSerializable` for beans that need to implement `Serializable`

### JSF (Faces) Integration
- Add this dependency to your pom.xml
```xml
<dependency>
    <groupId>net.bis5.opentelemetry</groupId>
    <artifactId>opentelemetry-faces</artifactId>
    <version>1.0</version>
</dependency>
```
- Add phase listener to your faces-config.xml
```xml
<lifecycle>
    <phase-listener>net.bis5.opentelemetry.faces.phase.TracingPhaseListener</phase-listener>
</lifecycle>
```
- Add one of the following annotations to your backing bean:
  - `@Traced` for stateless backing beans
  - `@TracedSerializable` for `@ViewScoped`, `@SessionScoped`, or other scopes requiring `Serializable` implementation

## License
Apache License, Version 2.0
