package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class AwsDbJumpHostApp {
    public static void main(final String[] args) {
        App app = new App();

        new NetworkStack(app, "NetworkStack", StackProps.builder().build());
        
        

        app.synth();
    }
}

