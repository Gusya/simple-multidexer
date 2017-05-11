package com.gusya.mv.simplemultidexer.loader;

import android.content.Context;

import com.gusya.mv.simplemultidexer.loader.source.InputStreamSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

/**
 * <p>
 *     Loader that uses Input Stream from file
 * </p>
 */
public class FileLoader extends Loader {

    private static final String PATH_SEPARATOR = "/";
    private static final String OPTIMIZED_DIR = "optimized";
    private static final String WORK_DIR = "working";

    private final InputStreamSource mInputStreamSource;
    public FileLoader(InputStreamSource inputStreamSource) {
        this.mInputStreamSource = inputStreamSource;
    }

    @Override
    public BaseDexClassLoader loadDexElements(Context context, String filePath) throws Exception {
        if(filePath.endsWith(PATH_SEPARATOR)){
            throw new IllegalArgumentException("Bad filePath to load: "+filePath);
        }
        int fileNameIndex = filePath.lastIndexOf(PATH_SEPARATOR);
        // substring from symbol next to forward slash
        // or substring all symbols when there is no forward slash
        String fileName = filePath.substring(++fileNameIndex);

        // prepare files and directories for Dex classes loading
        File optimized = context.getDir(OPTIMIZED_DIR, Context.MODE_PRIVATE);
        optimized = new File(optimized, fileName);
        optimized.mkdir();
        File work = context.getDir(WORK_DIR, Context.MODE_PRIVATE);
        work = new File(work, fileName);

        // get correct input stream
        InputStream inputDex = mInputStreamSource.getInputStream(context, filePath);

        // get output stream to work file
        FileOutputStream outputDex = new FileOutputStream(work);

        byte[] buf = new byte[8192];
        int bytesRead;
        while( (bytesRead = inputDex.read(buf)) != -1 ){
            outputDex.write(buf, 0, bytesRead);
        }
        outputDex.close();
        inputDex.close();

        return new DexClassLoader(work.getAbsolutePath(), optimized.getAbsolutePath(), null, context.getClassLoader());
    }
}
