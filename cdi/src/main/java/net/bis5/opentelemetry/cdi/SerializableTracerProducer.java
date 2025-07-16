package net.bis5.opentelemetry.cdi;

import io.opentelemetry.api.trace.Tracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
class SerializableTracerProducer {

    @Inject
    Tracer tracer;

    @Produces
    @SerializableTracer
    @ApplicationScoped
    public Tracer getTracer() {
        return tracer;
    }

}
