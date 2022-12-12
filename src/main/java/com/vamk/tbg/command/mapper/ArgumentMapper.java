package com.vamk.tbg.command.mapper;

/**
 * Argument mappers are responsible to convert
 * a string argument into some other type.
 */
public interface ArgumentMapper<T> {

    /**
     * Determines the type this converter can
     * convert string into.
     */
    Class<T> type();

    /**
     * Convert (or map) a string argument
     * into object of type T.
     *
     * @param arg The string argument
     * @throws ArgumentException if the conversion fails
     */
    T map(String arg) throws ArgumentException;
}
