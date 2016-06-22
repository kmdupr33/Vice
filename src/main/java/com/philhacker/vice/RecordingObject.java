package com.philhacker.vice;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by mattdupree on 6/22/16.
 */
interface RecordingObject {
    Object getRawObject();
    List<Triple<Method, List<Object>, Object>> getArgs();
}
