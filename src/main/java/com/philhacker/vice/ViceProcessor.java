package com.philhacker.vice;


import com.philhacker.vice.annotations.Clamp;
import com.philhacker.vice.annotations.ViceFor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mattdupree on 6/16/16.
 */
public class ViceProcessor extends AbstractProcessor {


    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Set<? extends Element> viceAnnotatedElements = roundEnv.getElementsAnnotatedWith(ViceFor.class);
        final Set<? extends Element> clamps = roundEnv.getElementsAnnotatedWith(Clamp.class);


        for (Element element : viceAnnotatedElements) {
            final ElementKind kind = element.getKind();
            if (kind != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ViceFor can only be applied to classes");
                return true;
            }
            TypeElement typeElement = (TypeElement) element;
            try {
                final String className = String.valueOf(typeElement.getQualifiedName());
                final Object viceMaker = Class.forName(className).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(ViceFor.class.getCanonicalName(),
                                                 Clamp.class.getCanonicalName()));
    }
}
