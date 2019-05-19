package com.example.mifans.eif.Tools;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class HideTatusBar {
    public static void hidetatusBar(Activity activity){

        if(Build.VERSION.SDK_INT >= 21){

            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );

            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
