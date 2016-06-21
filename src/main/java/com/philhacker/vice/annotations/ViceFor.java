package com.philhacker.vice.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mattdupree on 6/16/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ViceFor {
    Class value();
}
