package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class AwsDbJumpHostApp {
    public static void main(final String[] args) {
        App app = new App();

        NetworkStack networkStack = new NetworkStack(app, "NetworkStack", StackProps.builder().build());
        DatabaseStack databaseStack = new DatabaseStack(app, "DatabaseStack",  StackProps.builder().build(), networkStack.vpc);
        new JumphostStack(app, "JumphostStack", StackProps.builder().build(), networkStack.vpc, networkStack.vpcEndpointSecurityGroup, databaseStack.databaseInstance);
        app.synth();
    }
}

