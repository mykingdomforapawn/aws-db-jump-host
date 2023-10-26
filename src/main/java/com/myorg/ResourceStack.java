package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.constructs.Construct;

public class ResourceStack extends Stack {
    public ResourceStack(final Construct scope, final String id, IVpc vpc) {
        this(scope, id, null, vpc);
    }

    public ResourceStack(final Construct scope, final String id, final StackProps props, final IVpc vpc) {
        super(scope, id, props);
        
        SecurityGroup jumpHostSecurityGroup = createJumpHostSecurityGroup(vpc);
        //props.getVpc();
    }

    private SecurityGroup createJumpHostSecurityGroup(IVpc vpc) {
        return SecurityGroup.Builder.create(this, "jump-host-security-group")
            .description("Security group for jump host to access database cluster.")
            .allowAllOutbound(false)
            .vpc(vpc)
            .build();
    }
}
