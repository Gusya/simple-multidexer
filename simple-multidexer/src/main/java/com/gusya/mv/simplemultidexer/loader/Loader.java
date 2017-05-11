package com.gusya.mv.simplemultidexer.loader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.gusya.mv.simplemultidexer.SimpleMultiDexer;
import com.gusya.mv.simplemultidexer.loader.source.AbsolutePathDirSource;
import com.gusya.mv.simplemultidexer.loader.source.AssetsSource;
import com.gusya.mv.simplemultidexer.loader.source.CacheDirSource;
import com.gusya.mv.simplemultidexer.loader.source.FilesDirSource;

import java.io.File;
import java.io.IOException;

import dalvik.system.BaseDexClassLoader;

/**
 * <p>
 *     Prepares and loads other classes bundled in dex, jar or apk archives.
 * </p>
 */
public abstract class Loader {

    public abstract BaseDexClassLoader loadDexElements(Context context, String filePath)
            throws Exception;

    /**
     * Creates concrete Loader implementation
     */
    public static class Factory {

        /**
         * Hidden constructor
         */
        private Factory(){
        }

        /**
         * <p>
         *     Factory method that creates a suitable Loader based on file's location and type
         * </p>
         * @param context
         * @param dexFileName
         * @return
         */
        public static Loader createLoader(Context context, String dexFileName){
            Loader resolved = null;

            //search in assets
            try{
                String[] assets = context.getAssets().list("");
                for(String asset : assets){
                    if(asset.equalsIgnoreCase(dexFileName)){
                        resolved = new FileLoader(new AssetsSource());
                        Log.v(SimpleMultiDexer.TAG, dexFileName +" found in Assets");
                        return resolved;
                    }
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }

            //search in files dir
            try{
                File completeFile = new File(context.getFilesDir(), dexFileName);
                if(completeFile.exists() && completeFile.isFile()){
                    resolved = new FileLoader(new FilesDirSource());
                    Log.v(SimpleMultiDexer.TAG, dexFileName +" found in Files directory");
                    return resolved;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            //search in cache dir
            try{
                File completeFile = new File(context.getCacheDir(), dexFileName);
                if(completeFile.exists() && completeFile.isFile()){
                    resolved = new FileLoader(new CacheDirSource());
                    Log.v(SimpleMultiDexer.TAG, dexFileName +" found in Cache directory");
                    return resolved;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //may be it's an absolute path to file
            try{
                File completeFile = new File(dexFileName);
                if(completeFile.exists() && completeFile.isFile()){
                    resolved = new FileLoader(new AbsolutePathDirSource());
                    Log.v(SimpleMultiDexer.TAG, dexFileName +" found by absolute path");
                    return resolved;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //may be it's a package name for some already installed application
            try{
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo(dexFileName, 0);

                // if we get no exception, it is ok to try to load package's code
                resolved = new PackageLoader();
                Log.v(SimpleMultiDexer.TAG, dexFileName +" found by package manager");
                return resolved;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return resolved;
        }
    }
}
