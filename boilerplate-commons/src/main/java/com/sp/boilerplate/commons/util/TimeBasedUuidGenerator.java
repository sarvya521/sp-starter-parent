package com.sp.boilerplate.commons.util;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class TimeBasedUuidGenerator {

    private TimeBasedUuidGenerator() {
        throw new AssertionError();
    }

    public static UUID get() {
        return Generators.timeBasedGenerator().generate();
    }
}
