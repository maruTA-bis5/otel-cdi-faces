package net.bis5.opentelemetry.faces.phase;

import java.util.Map;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

/**
 * A {@link PhaseListener} that traces the Jakarta Faces lifecycle phases.
 * It creates a root span for the entire lifecycle and a child span for each phase.
 */
public class TracingPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    private final transient ThreadLocal<Span> rootSpan = new ThreadLocal<>();
    private final transient ThreadLocal<Scope> rootScope = new ThreadLocal<>();
    private final transient ThreadLocal<Span> currentSpan = new ThreadLocal<>();
    private final transient ThreadLocal<Scope> currentScope = new ThreadLocal<>();

    @Override
    public void beforePhase(PhaseEvent event) {
        Tracer tracer = getTracer();
        Span activeSpan = Span.current();
        if (activeSpan == null) {
            activeSpan = tracer.spanBuilder("Faces Lifecycle").startSpan();
            Scope root = activeSpan.makeCurrent();
            rootScope.set(root);
            rootSpan.set(activeSpan);
        }
        Span span = tracer.spanBuilder(event.getPhaseId().getName()).startSpan();
        Scope scope = span.makeCurrent();
        currentScope.set(scope);
        currentSpan.set(span);
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        currentScope.get().close();
        currentScope.remove();
        currentSpan.get().end();
        currentSpan.remove();
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE && rootSpan.get() != null) {
            rootScope.get().close();
            rootScope.remove();
            rootSpan.get().end();
            rootSpan.remove();
        }
    }

    private static final String TRACER_KEY = TracingPhaseListener.class.getName() + ".Tracer";

    private Tracer getTracer() {
        Map<String, Object> request = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        return (Tracer) request.computeIfAbsent(TRACER_KEY, ignore -> resolveTracer());
    }

    private Tracer resolveTracer() {
        Instance<Tracer> tracerInstance = CDI.current().getBeanManager().createInstance().select(Tracer.class);
        if (!tracerInstance.isUnsatisfied()) {
            return tracerInstance.get();
        }
        return null;
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

}
