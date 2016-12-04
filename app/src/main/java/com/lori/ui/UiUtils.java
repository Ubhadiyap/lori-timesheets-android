package com.lori.ui;

import android.widget.Button;

/**
 * @author artemik
 */
public class UiUtils {

    private static final int ENABLED_BUTTON_ALPHA = 1;
    private static final float DISABLED_BUTTON_ALPHA = 0.5f;

    private UiUtils() {

    }

    /**
     * Custom buttons enabling/disabling.
     * Standard Button.setEnabled(false) makes the button it too transparent.
     * This is a simple workaround instead of using styles and themes.
     */
    public static void setButtonEnabled(Button button, boolean enabled) {
        button.setAlpha(enabled ? ENABLED_BUTTON_ALPHA : DISABLED_BUTTON_ALPHA);
        button.setClickable(enabled);
    }
}
