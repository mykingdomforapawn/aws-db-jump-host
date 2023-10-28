package com.myorg;

import java.util.List;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.ec2.IInstance;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.constructs.Construct;

public class DatabaseStack extends Stack{

    public DatabaseStack(final Construct scope, final String id, final IVpc vpc, final IInstance jumphost) {
        this(scope, id, null, vpc, jumphost);
    }

    public DatabaseStack(final Construct scope, final String id, final StackProps props, final IVpc vpc, final IInstance jumphost) {
        super(scope, id, props);

        SecurityGroup securityGroup = createDatabasetSecurityGroup(vpc);
        createDatabaseInstance(vpc, securityGroup);
    }

    private SecurityGroup createDatabasetSecurityGroup(IVpc vpc) {
        return SecurityGroup.Builder.create(this, "jump-host-security-group")
            .description("Security group of jump host.")
            .allowAllOutbound(false)
            .vpc(vpc)
            .build();
    }

    private DatabaseInstance createDatabaseInstance(IVpc vpc, ISecurityGroup securityGroup) {
        return DatabaseInstance.Builder.create(this, "MyRDSInstance")
                .vpc(vpc)
                .instanceIdentifier("myrdsinstance")
                .deleteAutomatedBackups(true)
                .publiclyAccessible(false)
                .removalPolicy(RemovalPolicy.DESTROY)
                .securityGroups(List.of(securityGroup))
                .vpcSubnets(SubnetSelection.builder().subnets(vpc.getIsolatedSubnets()).build())
                .engine(DatabaseInstanceEngine.POSTGRES)
                .allocatedStorage(2)
                .databaseName("private-database")
                .instanceType(InstanceType.of(InstanceClass.M5, InstanceSize.SMALL))
                .storageEncrypted(false)
                .build();
    }
    
}
