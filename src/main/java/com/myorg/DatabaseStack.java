package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.IInstance;
import software.amazon.awscdk.services.ec2.IVpc;
import software.constructs.Construct;

public class DatabaseStack extends Stack{

    public DatabaseStack(final Construct scope, final String id, final IVpc vpc, final IInstance jumphost) {
        this(scope, id, null, vpc, jumphost);
    }

    public DatabaseStack(final Construct scope, final String id, final StackProps props, final IVpc vpc, final IInstance jumphost) {
        super(scope, id, props);

        
    }
    
}
