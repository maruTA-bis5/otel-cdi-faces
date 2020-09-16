# opentracing-faces

## Usage
- Add these entry to your pom.xml
```xml
<depencency>
    <groupId>net.bis5.opentracing</groupId>
    <artifactId>opentracing-faces</artifactId>
    <version>1.0</version>
</dependency>
````
- Add phase listener to your faces-config.xml
```xml
<lifecycle>
    <phase-listener>net.bis5.opentracing.faces.phase.TracingPhaseListener</phase-listener>
</lifecycle>
- Add `@org.eclipse.microprofile.opentracing.Traced` annotation to your backing bean
    - For `@ViewScoped`, `@SessionScoped`, or something else to need `Serializable` scopes, you can use `@net.bis5.opentracing.faces.interceptor.TracedSerializable` annotation instead.

## License
Apache License, Version 2.0
