package com.philhacker.vice;

/**
 * Created by mattdupree on 6/22/16.
 */
interface RecordingObjectFactory {

    RecordingObject make(Class classToClamp) throws InstantiationException, IllegalAccessException;
}
