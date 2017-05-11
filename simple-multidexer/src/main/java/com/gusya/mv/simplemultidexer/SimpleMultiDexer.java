package com.gusya.mv.simplemultidexer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.gusya.mv.simplemultidexer.loader.Loader;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

/**
 * <p>
 * Helper class used for external DEX files loading.
 * Using this class one can update DexClassLoader's path to add DEX files
 * from assets, directly from filesystem, from other installed applications.
 * However these changes are valid only for application's process lifetime
 * (e.g. if process is killed one need to perform update again in order to use classes from external DEX files)
 * </p>
 *
 */

public class SimpleMultiDexer {

    /**
     * <p>
     *     Callback that is invoked on client side
     * </p>
     */
    public interface Callback {
        /**
         * When DEX file was processed and successfully loaded
         * @param dexFile DEX file to be processed
         */
        void onLoad(String dexFile);

        /**
         * When DEX file processing raised and exception
         * @param exception
         * @param dexFile
         */
        void onException(Exception exception, String dexFile);

        /**
         * <p>
         *      When all DEX files were processed.
         *      However, it doesn't mean that all DEX files were loaded successfully
         * </p>
         */
        void onLoadAll();

    }

    public static final String TAG = "SimpleMultidexer";

    private SimpleMultiDexer() {
    }

    /**
     * <p>
     * Load DEX files using application context.
     * This call is performed on separate thread, so callback methods will be called also on separate thread
     * </p>
     * @param context Application context
     * @param dexFiles
     * @param callback
     */
    public static void loadDexAsync(@NonNull final Context context, @NonNull final String[] dexFiles,
                                    @NonNull final SimpleMultiDexer.Callback callback){
        final Context appContext = context.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
               loadMultipleDexFiles(appContext, dexFiles, callback);
            }
        }).start();
    }

    /**
     * <p>
     * Load DEX files using activity instance. It runs on UI thread.
     * If the current thread is the UI thread, then the action is executed immediately.
     * If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.
     * </p>
     * @param activity Activity
     * @param dexFiles List of dex files, along with their extensions
     */
    public static void loadDex(@NonNull final Activity activity, @NonNull final String[] dexFiles) {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                Context appContext = activity.getApplicationContext();
                loadMultipleDexFiles(appContext, dexFiles, new Callback() {
                    @Override
                    public void onLoad(String dexFile) {
                        // do nothing
                    }

                    @Override
                    public void onException(Exception exception, String dexFile) {
                        // do nothing
                    }

                    @Override
                    public void onLoadAll() {
                        // do nothing
                    }
                });
            }
        });
    }

    private static void loadMultipleDexFiles(@NonNull Context context, @NonNull String[] dexFiles, @NonNull Callback callback){
        for (String dexFile : dexFiles) {
            try {
                Loader loader = Loader.Factory.createLoader(context, dexFile);

                // can't find suitable loader
                if(loader == null){
                    callback.onException(new IllegalArgumentException(dexFile+" not found"),
                            dexFile);
                    continue;
                }

                // process dex file
                processWithLoader(context, loader, dexFile);
                callback.onLoad(dexFile);
            }
            catch (Exception e){
                callback.onException(e, dexFile);
            }
        }

        if(callback != null)callback.onLoadAll();
    }

    private static void processWithLoader(Context context, Loader loader, String filePath)
            throws Exception {

        // get class loader with local dex files
        ClassLoader localClassLoader = SimpleMultiDexer.class.getClassLoader();
        // get class loader with requested dex files
        BaseDexClassLoader dexBaseClassLoader = loader.loadDexElements(context, filePath);

        // try to merge
        try{
            BaseDexClassLoader localBaseClassLoader = (BaseDexClassLoader)localClassLoader;
            Object localElements = getDexClassLoaderElements(localBaseClassLoader);
            Object loaderElements = getDexClassLoaderElements(dexBaseClassLoader);
            Object merged = mergeArrays(loaderElements, localElements);
            setDexClassLoaderElements(localBaseClassLoader, merged);
        } catch (Exception e){
            e.printStackTrace();
            throw new UnsupportedOperationException("Class loader operations are not supported");
        }
    }

    private static void setDexClassLoaderElements(BaseDexClassLoader classLoader, Object elements)
            throws Exception {
        Class<BaseDexClassLoader> dexClassLoaderClass = BaseDexClassLoader.class;
        Field pathListField = dexClassLoaderClass.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        dexElementsField.set(pathList, elements);
    }

    private static Object getDexClassLoaderElements(BaseDexClassLoader classLoader)
            throws Exception {
        Class<BaseDexClassLoader> dexClassLoaderClass = BaseDexClassLoader.class;
        Field pathListField = dexClassLoaderClass.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        Object dexElements = dexElementsField.get(pathList);
        return dexElements;
    }

    private static Object mergeArrays(Object newElements, Object oldElements) {
        Class<?> o1Type = newElements.getClass().getComponentType();
        Class<?> o2Type = oldElements.getClass().getComponentType();
        if (o1Type != o2Type) {
            throw new IllegalArgumentException("Bad types found trying to merge dex elements");
        }
        int newElementsSize = Array.getLength(newElements);
        int oldElementsSize = Array.getLength(oldElements);
        Object array = Array.newInstance(o1Type, newElementsSize + oldElementsSize);

        System.arraycopy(newElements, 0, array, 0, newElementsSize);
        System.arraycopy(oldElements, 0, array, newElementsSize, oldElementsSize);

        return array;
    }
}
