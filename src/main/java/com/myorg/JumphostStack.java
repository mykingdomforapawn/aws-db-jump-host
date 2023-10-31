package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.IConnectable;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.constructs.Construct;

public class JumphostStack extends Stack {

    public final Instance jumphost;

    public JumphostStack(final Construct scope, final String id, final Vpc vpc, final SecurityGroup vpcEndpointSecurityGroup) {
        this(scope, id, null, vpc, vpcEndpointSecurityGroup);
    }

    public JumphostStack(final Construct scope, final String id, final StackProps props, final Vpc vpc, final SecurityGroup vpcEndpointSecurityGroup) {
        super(scope, id, props);
        
        SecurityGroup jumphostSecurityGroup = createJumphostSecurityGroup(vpc);
        jumphost = createJumpHost(vpc, jumphostSecurityGroup);
        manageConnections(jumphost, vpcEndpointSecurityGroup);
    }

    private SecurityGroup createJumphostSecurityGroup(IVpc vpc) {
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
 
    private void manageConnections(IConnectable jumphost, IConnectable vpcEndpoinSecurityGroup) {
        jumphost.getConnections().allowTo(vpcEndpoinSecurityGroup, Port.allTcp(), "Allow traffic to vpc endpoints.");
    }
}