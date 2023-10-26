package com.myorg;

import software.constructs.Construct;

import java.util.List;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;

public class NetworkStack extends Stack {
    
    public final Vpc vpc;
    
    public NetworkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public NetworkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc = createVpc();
        //this.vpc = vpc;
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "DbJumpHostVpc")
            .maxAzs(1)
            .createInternetGateway(false)
            .natGateways(0)
            .restrictDefaultSecurityGroup(true)
            .subnetConfiguration(
                List.of(
                    SubnetConfiguration.builder()
                    .name("PrivateSubnet")
                    .subnetType(SubnetType.PRIVATE_ISOLATED)
                    .cidrMask(24)
                    .build()))
            .build();
    }

    public Vpc getVpc() {
        return vpc;
    }
}
