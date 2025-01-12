package it.smartcommunitylabdhub.core.annotations.infrastructure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface FrameworkComponent {
    String framework(); // framework take care to create the component for the context
    // execution local, serve, deploy

}
