package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class AwsDbJumpHostApp {
    public static void main(final String[] args) {
        App app = new App();

        NetworkStack networkStack = new NetworkStack(app, "NetworkStack", StackProps.builder().build());
        JumphostStack jumphostStack = new JumphostStack(app, "JumphostStack", StackProps.builder().build(), networkStack.vpc, networkStack.vpcEndpointSecurityGroup);
        new DatabaseStack(app, "DatabaseStack",  StackProps.builder().build(), networkStack.vpc, jumphostStack.jumphost);
        app.synth();
    }
}

