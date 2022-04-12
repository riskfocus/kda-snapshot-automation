AWS CloudFormation template (JSON) for automating Kinesis Data Analytics Snapshots at a specific time internal.
The CloudFormation template will build:

- Create a Kinesis Data Analytics platform with Java application
- Create Lambda function with required permission roles
- Create an EventBridge rule for the automated creation of Snapshots
- Create log group and log stream as part of KDA platform
- Create CloudWatch dashboard with widgets related to KDA uptime, Snapshots, Checkpoints and records
- Create CloudWatch alarms for application downtime, Checkpoints and Snapshots
- Create an SNS topic for the notification of Snapshot creation events
- Create a DynamoDB table to detail the Lambda function activities for invoking the snapshot function. These activities are populated into the table

