package com.lori.base;

import com.lori.core.app.App;

/**
 * @author artemik
 */
public class TestInstrumentationApplication extends App {

    @Override
    protected void initAnalytics() {
        // Disable analytics in tests.
    }
}
