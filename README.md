AWS CloudFormation template (JSON) for automating Kinesis Data Analytics Snapshots at a user-define time internal.
The CloudFormation template will build:

- Create a Kinesis Data Analytics platform with Java application
- Create a Lambda function with required permission roles
- Create an EventBridge rule for the automated creation of Snapshots
- Create a log group and log stream as part of KDA platform
- Create a CloudWatch dashboard with widgets related to KDA uptime, Snapshots, Checkpoints and records
- Create a CloudWatch alarms for application downtime, Checkpoints and Snapshots
- Create an SNS topic for the notification of Snapshot creation events
- Create a DynamoDB table to detail the Lambda function activities for invoking the snapshot function. These activities are populated into the table

# Deployment Instructions

Using a single CloudFormation template, we will deploy the necessary AWS services to build a Kinesis Data Analytics platform with a Java application, and to automate the creation of Snapshots based on a user-defined timeline.

The CloudFormation template will also build AWS services, including a Lambda function, a CloudWatch dashboard with widgets, an SNS topic and a DynamoDB table to store the snapshotting events.

## Step 1


## Step 2: launch CloudFormation stack

From the CloudFormation landing page, launch a stack with new resources:

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/100.png)

The CloudFormation template should be stored in an S3 bucket, so copy the object S3 URL of the object into the CloudFormation template section:

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/14.png)

