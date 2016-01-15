package com.joe.indoorlocalization;

import android.app.Application;

import com.joe.indoorlocalization.State.ApplicationState;

/**
 * Created by joe on 31/12/15.
 */
public class IndoorLocalization extends Application {

    private ApplicationState applicationState = new ApplicationState();

    public ApplicationState getApplicationState() {
        return this.applicationState;
    }

}
