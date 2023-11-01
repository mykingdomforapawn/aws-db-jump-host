package com.myorg;

import software.constructs.Construct;

import java.util.List;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.InterfaceVpcEndpointAwsService;
import software.amazon.awscdk.services.ec2.InterfaceVpcEndpointOptions;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;

public class NetworkStack extends Stack {
    
    public final Vpc vpc;
    public final SecurityGroup vpcEndpointSecurityGroup;
    
    public NetworkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public NetworkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc = createVpc();
        vpcEndpointSecurityGroup = createVpcEndpointSecurityGroup(vpc);
        createVpcEndpoints(vpc, vpcEndpointSecurityGroup);
        manageConnections(vpcEndpointSecurityGroup);
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "DbJumpHostVpc")
            .maxAzs(2)
            .createInternetGateway(false)
            .natGateways(0)
            .restrictDefaultSecurityGroup(true)
            .subnetConfiguration(
                List.of(
                    SubnetConfiguration.builder()
                        .name("PrivateSubnet1")
                        .subnetType(SubnetType.PRIVATE_ISOLATED)
                        .cidrMask(24)
                        .build()))
            .build();
    }

    private SecurityGroup createVpcEndpointSecurityGroup(IVpc vpc) {
        return SecurityGroup.Builder.create(this, "vpc-endpoint-security-group")
            .description("Security group of vpc endpoints.")
            .allowAllOutbound(false)
            .vpc(vpc)
            .build();
    }

    private void createVpcEndpoints(Vpc vpc, ISecurityGroup securityGroup) {
        SubnetSelection subnetSelection = SubnetSelection.builder().subnetType(SubnetType.PRIVATE_ISOLATED).build();
        
        vpc.addInterfaceEndpoint("ssm-endpoint", InterfaceVpcEndpointOptions.builder()
            .service(InterfaceVpcEndpointAwsService.SSM)
            .subnets(subnetSelection)
            .securityGroups(List.of(securityGroup))
            .build());

        vpc.addInterfaceEndpoint("ssm-messages-endpoint", InterfaceVpcEndpointOptions.builder()
            .service(InterfaceVpcEndpointAwsService.SSM_MESSAGES)
            .subnets(subnetSelection)
            .securityGroups(List.of(securityGroup))
            .build());

        vpc.addInterfaceEndpoint("ec2-endpoint", InterfaceVpcEndpointOptions.builder()
            .service(InterfaceVpcEndpointAwsService.EC2)
            .subnets(subnetSelection)
            .securityGroups(List.of(securityGroup))
            .build());

        vpc.addInterfaceEndpoint("ec2-messages-endpoint", InterfaceVpcEndpointOptions.builder()
            .service(InterfaceVpcEndpointAwsService.EC2_MESSAGES)
            .subnets(subnetSelection)
            .securityGroups(List.of(securityGroup))
            .build());
    }

    private void manageConnections(ISecurityGroup securityGroup) {
        securityGroup.getConnections().allowFromAnyIpv4(Port.tcp(443), "Allow access to vpc endpoints.");
    }
}
