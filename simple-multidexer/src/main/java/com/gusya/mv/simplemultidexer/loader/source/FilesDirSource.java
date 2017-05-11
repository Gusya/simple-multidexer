package com.gusya.mv.simplemultidexer.loader.source;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class FilesDirSource implements InputStreamSource {

    @Override
    public InputStream getInputStream(Context context, String filePath) throws IOException {
        File completeFile = new File(context.getFilesDir(), filePath);
        return new FileInputStream(completeFile);
    }
}
