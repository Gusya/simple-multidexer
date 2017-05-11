package com.gusya.mv.simplemultidexer.loader;

import android.content.Context;

import dalvik.system.BaseDexClassLoader;

/**
 * <p>
 *     Loader that uses other application's context.
 *     *Attention:* this loader allows another application's code to be loaded into a process
 *     even when it isn't safe to do so. Use with extreme care!
 * </p>
 */
public class PackageLoader extends Loader {

    @Override
    public BaseDexClassLoader loadDexElements(Context context, String filePath) throws Exception {
        final String packageName = filePath;
        Context otherContext = context.createPackageContext(packageName,
                Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        return (BaseDexClassLoader)otherContext.getClassLoader();
    }
}
