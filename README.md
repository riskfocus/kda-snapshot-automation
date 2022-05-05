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

From the /src folder, compile the Java source code to a JAR file, and upload the file to your S3 bucket of choice.

## Step 2: launch CloudFormation stack

From the CloudFormation landing page, launch a stack with new resources:

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/100.png)


The CloudFormation template should be stored in an S3 bucket of your choice, and then copy the object S3 URL of the template object into the CloudFormation template section:

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/14.png)


On the next page of the CloudFormation creation process, enter a Stack name:

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/1.png)


Below that, there are a number of parameters that can be defined for the CloudFormation stack:

  1. Log monitoring level → set to 'INFO' to see all events, such as each record that is created
  2. Monitoring level → set to 'APPLICATION' as this will provide logs for the Java application activities
  3. Service-triggered Snapshots → leave as 'true' to allow Snapshots to be created
  4. Number of Snapshots to retain → AWS KDA will retain up to 1000 Snapshots, but for testing purposes this can be left at 10, whereby after 10 Snapshots are created, the oldest Snapshot is deleted when each new Snapshot is created
  5.Scaling → leave as 'true'
  6. How long to generate user data → the Java application creates random user information, and this will be done for X seconds, for example 600 seconds or 10 minutes
  7. Delay between data generation → the time in milliseconds between each random user data record created

![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/2.png)


Below these parameters are the following three parameters:

  1. CloudWatch dashboard name
  2. Email address for SNS notifications
  3. SNS topic name


![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/3.png)

