package com.philhacker.vice;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

/**
 * Created by mattdupree on 6/16/16.
 */
public class ViceProcessor extends AbstractProcessor {
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ViceFor.class.getCanonicalName());
    }
}
