package com.lori.ui;

import android.widget.Button;

/**
 * @author artemik
 */
public class UiUtils {
    /**
     * Custom buttons enabling/disabling.
     * Standard Button.setEnabled(false) makes the button it too transparent.
     * This is a simple workaround instead of using styles and themes.
     */
    public static void setButtonEnabled(Button button, boolean enabled) {
        button.setAlpha(enabled ? 1 : 0.5f);
        button.setClickable(enabled);
    }
}
