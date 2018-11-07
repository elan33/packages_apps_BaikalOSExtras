/*
 * Copyright (C) 2017 AICP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package ru.baikalos.extras;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;

public abstract class BaseActivity extends Activity {

    private int mThemeRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeRes = getThemeRes();
        setTheme(mThemeRes);
        super.onCreate(savedInstanceState);
        fixStatusBarFg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeRes != getThemeRes()) {
            recreate();
        }
    }

    protected int getThemeRes() {
        int themePref = 2; //Settings.System.getInt(getContentResolver(), Settings.System.BE_THEME, 0);
        switch (themePref) {
            /*
            case 1:
                return R.style.AppTheme_DarkAmber;
            */
            case 2:
                return R.style.AppTheme_Light;
            case 3:
                return R.style.AppTheme_Dark;
            case 4:
                return R.style.AppTheme_Light_MoreAccent;
            case 5:
                return R.style.AppTheme_Dark_MoreAccent;
            default:
            {
                // Decide on whether to use a light or dark theme by judging devicedefault
                // settings theme (or descendant in this case) colors
                ContextThemeWrapper themeContext = new ContextThemeWrapper(this, R.style.AppTheme);
                TypedValue tv = new TypedValue();
                themeContext.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
                int bgColor = tv.data;
                themeContext.getTheme().resolveAttribute(android.R.attr.colorForeground, tv, true);
                int fgColor = tv.data;
                if (Color.luminance(fgColor) <= Color.luminance(bgColor)) {
                    // Use theme that follows DeviceDefault settings theme directly
                    // to get possibly themed changes
                    if (themePref == 6) {
                        return R.style.AppTheme_MoreAccent;
                    } else {
                        return R.style.AppTheme;
                    }
                } else {
                    // When using a dark theme, we could actually use AppTheme here too because
                    // it inherits from DeviceDefault, but we have some dark theme improvements here
                    if (themePref == 6) {
                        return R.style.AppTheme_Dark_MoreAccent;
                    } else {
                        return R.style.AppTheme_Dark;
                    }
                }
            }
        }
    }

    /**
     * When changing from a theme with light to one with dark status bar, recreating
     * the activity seems to be not enough to update status bar foreground color,
     * so it's black on black.
     * This is a workaround for that, basically adapted from Launcher3's dynamic
     * status bar color (fg color changing when opening/closing drawer).
     */
    private void fixStatusBarFg() {
        int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;
        int[] attrs = new int[] {
                android.R.attr.windowLightStatusBar,
                android.R.attr.windowLightNavigationBar,
        };
        TypedArray ta = getTheme().obtainStyledAttributes(attrs);
        boolean lightStatusBar = ta.getBoolean(0, false);
        boolean lightNavigationBar = ta.getBoolean(1, false);
        ta.recycle();
        if (lightStatusBar) {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            newSystemUiFlags &= ~(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (lightNavigationBar) {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            newSystemUiFlags &= ~(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        if (newSystemUiFlags != oldSystemUiFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newSystemUiFlags);
        }
    }
}
