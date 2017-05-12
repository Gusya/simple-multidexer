package com.gusya.mv.simplemultidexerapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

/**
 * Created by Gusya on 02/05/2017.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        setContentView(ll, params);

        String[] dexFiles = {"st"};
        SimpleMultiDexer.instance().loadDex(this, dexFiles);

        try {
            Class.forName("com.startapp.android.publish.Ad.class");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
