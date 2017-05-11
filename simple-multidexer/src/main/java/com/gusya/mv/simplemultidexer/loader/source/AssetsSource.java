package com.gusya.mv.simplemultidexer.loader.source;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetsSource implements InputStreamSource {

    @Override
    public InputStream getInputStream(Context context, String filePath)
            throws IOException {
        return context.getAssets().open(filePath);
    }
}
