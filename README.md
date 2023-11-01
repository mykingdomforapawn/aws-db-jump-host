# aws-db-jump-host

> This project is part of my software architecture journey and more specifically the cloud platforms path. See my [software-architecture-journey](https://github.com/mykingdomforapawn/software-architecture-journey) repository for more details.

It is very common to deploy critical components into private subnets and restict any communication with clients outside of the network. This solution implements an approach to access those resources without having to open any ports. It leverages the Systems Manager - Session Manager, AWS IAM and the Direct Connect technology.  

---

## Table of contents:

- [Get started](#get-started)
- [Solution diagram](#solution-diagram)
- [References](#references)

---


## Get started

### Create the CDK stack
- Set up your AWS environment for CDK (bootstrap, etc.)
- Run `cdk deploy --all true`
- Have a look at the solution diagram below to see what you have build

### Create an IAM entity with the required policies
- This can be an IAM user a role passed to a SSO user or something else
- The IAM entity that you use needs this permission: `ssm:StartSession`
- Add the permission to an attached policy

### Start a port forwarding session
- Go to a terminal and set your credentials from  Identity Center or via `aws configure`
- Run the command below in the terminal with the parameters that you can get from the AWS Console
    - `<region>`: The AWS region you have created the stack in
    - `<instance-id>`: This is the instance ID of the jumphost that you can get from the EC2 page in the AWS managament console
    - `<database-endpoint`: This is the database endpointthat you can get from the RDS page in the AWS managament console
```
aws ssm start-session --region <region> --target <instance-id> --document-name AWS-StartPortForwardingSessionToRemoteHost --parameters '{"host":["<database-endpoint>"],"portNumber":["5432"], "localPortNumber":["5432"]}
```

- The terminal should now say `Waiting for connections...`

### Connects to the database via the jumphost
- You can download a client like pgAdmin to connect to the database
- The hostname should be `127.0.0.1`
- The port is `5432` but could be different depending on the database
- Go to AWS Secrets Manager to get the `password`, `username` and `databasename`
- After providing all that information you should be able to connect to your private resource without having to expose any ports to the public

### Destroy and clean up the stack
- Run `cdk destroy --all true`
- Clean up the IAM setup that you have created

---

## Solution diagram
![Diagram](diagram.drawio.png)

---

## References
- [1] https://aws.amazon.com/blogs/database/securely-connect-to-an-amazon-rds-or-amazon-ec2-database-instance-remotely-with-your-preferred-gui/
- [2] https://aws.amazon.com/blogs/aws/new-port-forwarding-using-aws-system-manager-sessions-manager/
- [3] https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-troubleshooting.html
