package com.gusya.mv.simplemultidexer.loader.source;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Supplies InputStream from file
 */
public interface InputStreamSource {
    InputStream getInputStream(Context context, String filePath)
            throws IOException;
}
