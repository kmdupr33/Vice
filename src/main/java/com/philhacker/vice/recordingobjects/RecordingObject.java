package com.philhacker.vice.recordingobjects;

import com.philhacker.vice.Triple;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by mattdupree on 6/22/16.
 */
public interface RecordingObject {
    Object getRawObject();
    List<Triple<Method, List<Object>, Object>> getArgs();
}
