package com.philhacker.vice.recordingobjects;

/**
 * Created by mattdupree on 6/22/16.
 */
public interface RecordingObjectFactory {

    RecordingObject make(Class classToClamp) throws InstantiationException, IllegalAccessException;
}
