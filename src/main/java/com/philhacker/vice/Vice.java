package com.philhacker.vice;

import com.philhacker.vice.annotations.Clamp;
import com.philhacker.vice.annotations.ViceFor;
import com.philhacker.vice.recordingobjects.RecordingObject;
import com.philhacker.vice.recordingobjects.RecordingObjectFactory;
import com.philhacker.vice.regressiontestwriter.RegressionTestWriter;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates regression tests.
 *
 * Created by mattdupree on 6/21/16.
 */
@SuppressWarnings("WeakerAccess")
public class Vice {

    private final RegressionTestWriter regressionTestWriter;
    private final RecordingObjectFactory recordingObjectFactory;

    public Vice(RegressionTestWriter regressionTestWriter, RecordingObjectFactory recordingObjectFactory) {
        this.regressionTestWriter = regressionTestWriter;
        this.recordingObjectFactory = recordingObjectFactory;
    }

    public void make(Path s, Class<?>... viceMakerClasses) {
        ByteBuddyAgent.install();
        for (Class<?> viceMakerClass : viceMakerClasses) {
            final ViceFor annotation = viceMakerClass.getAnnotation(ViceFor.class);
            final Class classToClamp = annotation.value();
            final List<Method> methodsToExecute = Stream.of(viceMakerClass.getMethods())
                    .filter(method -> method.getAnnotation(Clamp.class) != null)
                    .collect(Collectors.toList());
            try {
                final Object viceMaker = viceMakerClass.newInstance();
                final RecordingObject recordingObject = recordingObjectFactory.make(classToClamp);
                for (Method method : methodsToExecute) {
                    method.invoke(viceMaker, recordingObject.getRawObject());
                    final String objectVariableName = classToClamp.getSimpleName().toLowerCase();
                    final String format = String.format("$T %s = new $T()", objectVariableName);
                    //noinspection SuspiciousMethodCalls
                    final Triple<Method, List<Object>, Object> firstMethodInvocation = recordingObject.getArgs().get(0);

                    MethodSpec characterizeReverse = regressionTestWriter.getMethodSpec(classToClamp, objectVariableName, format, firstMethodInvocation);
                    TypeSpec characterizationClass = regressionTestWriter.getTypeSpec(classToClamp, characterizeReverse);
                    regressionTestWriter.write(s, classToClamp, characterizationClass);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
