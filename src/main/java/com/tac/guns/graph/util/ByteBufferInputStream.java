package com.tac.guns.graph.util;

/*
 * The following code is from project JgLTF, using MIT license
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 */
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Implementation of an input stream that reads from a byte buffer
 */
class ByteBufferInputStream extends InputStream
{
    /**
     * The byte buffer from which this stream is reading
     */
    private final ByteBuffer byteBuffer;

    /**
     * Creates a new instance that read from the given byte buffer.
     * Reading from the stream will increase the position of the
     * given buffer. If this is not desired, a slice of the actual
     * buffer may be passed to this constructor.
     *
     * @param byteBuffer The byte buffer from which this stream is reading
     */
    ByteBufferInputStream(ByteBuffer byteBuffer)
    {
        this.byteBuffer = Objects.requireNonNull(byteBuffer,
                "The byteBuffer may not be null");
    }

    @Override
    public int read() throws IOException
    {
        if (!byteBuffer.hasRemaining())
        {
            return -1;
        }
        return byteBuffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException
    {
        if (!byteBuffer.hasRemaining())
        {
            return -1;
        }
        int readLength = Math.min(len, byteBuffer.remaining());
        byteBuffer.get(bytes, off, readLength);
        return readLength;
    }
}

