package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.constructs.Construct;

public class JumphostStack extends Stack {
    public JumphostStack(final Construct scope, final String id, IVpc vpc) {
        this(scope, id, null, vpc);
    }

    public JumphostStack(final Construct scope, final String id, final StackProps props, final IVpc vpc) {
        super(scope, id, props);
        
        SecurityGroup jumpHostSecurityGroup = createJumpHostSecurityGroup(vpc);
        createJumpHost(vpc, jumpHostSecurityGroup);
    }

    private SecurityGroup createJumpHostSecurityGroup(IVpc vpc) {
        return SecurityGroup.Builder.create(this, "jump-host-security-group")
            .description("Security group of jump host.")
            .allowAllOutbound(false)
            .vpc(vpc)
            .build();
    }

    private Instance createJumpHost(IVpc vpc, ISecurityGroup securityGroup) {
        return Instance.Builder.create(this, "jump-host-instance")
            .vpc(vpc)
            .vpcSubnets(SubnetSelection.builder().subnets(vpc.getIsolatedSubnets()).build())
            .ssmSessionPermissions(true)
            .machineImage(AmazonLinuxImage.Builder.create().generation(AmazonLinuxGeneration.AMAZON_LINUX_2).build())
            .securityGroup(securityGroup)
            .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.NANO))
            .detailedMonitoring(false)
            .requireImdsv2(true)
            .build();
    }
}