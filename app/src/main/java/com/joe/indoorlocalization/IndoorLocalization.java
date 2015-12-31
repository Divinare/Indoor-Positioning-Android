package com.joe.indoorlocalization;

import android.app.Application;

/**
 * Created by joe on 31/12/15.
 */
public class IndoorLocalization extends Application {

    private ApplicationState applicationState = new ApplicationState();

    public ApplicationState getApplicationState() {
        return this.applicationState;
    }

}
