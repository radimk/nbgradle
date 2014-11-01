package org.nbgradle.netbeans.project;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public interface StandardStreams extends Closeable {

    InputStream getInputStream();
    OutputStream getOutputStream();
    OutputStream getErrorStream();
}
