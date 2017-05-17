package com.gusya.mv.simplemultidexer.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gusya.mv.simplemultidexer.SimpleMultiDexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {

    private static final String TAG = "SMultidexer";

    // for the sake of simplicity we create multiple copies
    // of the same file found in assets folder
    final String sampleJar = "dagger.jar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ------- begin of sample preparations -------
        File st2InFiles = new File(getFilesDir(), "from-files-dir.jar");
        prepareFile(st2InFiles);

        File st3InFiles = new File(getCacheDir(), "from-cache-dir.jar");
        prepareFile(st3InFiles);

        File st4InFiles = new File(getDir("custom", Context.MODE_PRIVATE), "from-custom-dir.jar");
        prepareFile(st4InFiles);
        String st4AbsolutePath = st4InFiles.getAbsolutePath();
        // ------- end of sample preparations -------

        Log.d(TAG, "DEX files before: "+getClassLoader());

        String[] dexes = {
                /*from assets*/sampleJar,
                /*from getFilesDir()*/"from-files-dir.jar",
                /*from getCacheDir()*/"from-cache-dir.jar",
                /*absolute path*/st4AbsolutePath,
                /*from package name*/"com.android.calculator2"};

        SimpleMultiDexer.loadDexAsync(getApplicationContext(), dexes,
                new SimpleMultiDexer.Callback() {
                    @Override
                    public void onLoad(String dexFile) {
                        Log.v(TAG, "onLoad: "+dexFile);
                    }

                    @Override
                    public void onLoadAll() {
                        Log.v(TAG, "onLoadAll");
                        try {
                            // check that sample dagger DEX was loaded
                            Class.forName("dagger.internal.Collections");

                            // check that sample apk's code was loaded
                            Class.forName("com.android.calculator2.Calculator");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "DEX files after: "+getClassLoader());
                    }

                    @Override
                    public void onException(Exception exception, String dexFile) {
                        Log.v(TAG, "onException: "+dexFile+"; message: "+exception.getMessage());
                    }
                });
    }

    private void prepareFile(File fileToCreate){
        try {
            if(fileToCreate.exists()){
                fileToCreate.delete();
            }
            InputStream inputDex = getAssets().open(sampleJar);
            FileOutputStream outputDex = new FileOutputStream(fileToCreate);
            byte[] buf = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputDex.read(buf)) != -1) {
                outputDex.write(buf, 0, bytesRead);
            }
            outputDex.close();
            inputDex.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
