# Executive Summary
To effectively manage a Flink Streaming Application on AWS Kinesis Data Analytics (KDA) and ensure high availability, Flink has two features. 

The first are checkpoints that are fully automated in KDA and used to restart a node in the cluster if there is a failure and the node must be replaced in the underlying EKS cluster.  

The second feature is Flink Savepoints, which KDA calls Snapshots. Savepoints are used to restart an application after it has be purposefully stopped or if there is a data problem and the user wants to restart the application from a previous point in time. These operations also need to be monitored to ensure they are running and not taking too long as they impact application performance.

Currently, some of these features are not managed by the KDA service and require implementation by each application team. This project provides an out-of-the-box implementation to address fully automate the snapshots and required monitoring.

# Why AWS
Correctly deploying, managing, scaling, and monitoring Flink to ensure High Availability and scaling to large numbers of CPUs can be a significant undertaking for your DevOps team. The AWS Kinesis Data Analytics (KDA) is a fully managed service that allows applications teams to deploy and operate Flink applications. KDA follows AWS best practices and can scale applications massively with a fully managed service without hiring a large DevOps team.


# The Challenge
The AWS KDA service fully automates the creation and management of Flink Checkpoints for High Availability, and it creates and manages Snapshot when the application is shut down in an orderly fashion and provides an API to create Snapshots (Flink Savepoints) periodically.  

KDA also publishes metrics on Checkpoint and Snapshot completion and duration.

It is the responsibility of each application to implement the following features to achieve "Operational Readiness" to support and manage a streaming application in production.
1. Invoking the API for Snapshots periodically for application recovery. This is done to handle application-level issues that require restating the application from a known point in time if there is a non-recoverable error on the cluster for any reason.
2. Create Cloudwatch Dashboards and Alarms to monitor Checkpoints and Snapshots' performance. This will ensure they are running correctly and not taking longer than expected, as long-running checkpoints and snapshots will impact overall application performance.


# The Solution
Ness has created an accelerator in the form of a Cloudformation template, with which you can configure KDA  Snapshots to run at any interval required, and creates a CloudWatch dashboard and alarms for monitoring.

The template leverages the KDA API, AWS Lambda, AWS EventBridge rules, Amazon CloudWatch, and a simple KDA application to demonstrate the functionality. These services have been combined into an AWS CloudFormation template that can be deployed in your environment.

The template configures the AWS services required to create Snapshots on an automated schedule and creates a robust enterprise-grade streaming platform. It then creates a sample KDA Application to demonstrate the functionality is working by creating CloudWatch logs when the application is asked to create a snapshot. 
<insert AWS Arch Diagram Here>

The accelerator consists of the following components:

1. Lambda function automates the Snapshot creation process via a CloudWatch EventBridge rule, an SNS Topic. It uses DynamoDB table to create an audit trail.

2. A demo KDA application is created that allows you to test the Lambda function and Cloudwatch dashboards and alarms. To validate the application is creating snapshots, we have implemented logging in the sample application when a Savepoint is created to make it easy to do a demo.
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/21.png" /></kbd>

3. CloudWatch dashboard is created to report on the performance statistics of the KDA application, including Snapshots and Checkpoints. For demo purposes, metrics are included for the demo KDA application.
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/20.png" /></kbd>

4. Cloudwatch alarms are created for
    a. Snapshot duration (to track problems with increasing times)
    b. Checkpoint duration and failed Checkpoints
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/8.png" /></kbd>


## How the sample application works
The application randomly does xxxx and publishes records to yyyy.   We have implemented logging as follows
1. In the flink 'xxx' method, the application logs the following messages
  a. when the application starts without a Snapshot/without Stage look for this string: ""
  b. When the application is started with a snapshot, you will see the following log message: ""

2. When KDA triggers a checkpoint automatically, you will see this log: ""
3. When the API is invoked, you will see the following log message: ""

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></kbd>


Using this accelerator template, you can add the ability to create Snapshots to ensure you can recover your application at any time from a know point and not lose any data.

# Feedback
If you have any feedback, please raise issues in the Github project and feel free to submit PRs to suggest improvements.


# Sample Cloudwatch Dashboard and Log output

## Reviewing Cloudwatch Logs to check Snapshot Operation

From CloudWatch Log Insights, and using the Log Group from the CloudFormation 'resources' tab, a query can be run to show when the demo application was started; in this screenshot, there are three records.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/12a.png" /></kbd>

Looking at the detail of two log events, the top events show the demo application was restored from context (the application was re-started with state). The bottom event was when the demo application was first started, and there was no Snapshot and hence no state.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/104.png" /></kbd>


## Reviewing Cloudwatch Logs Check Demo Application events generated

A query can also be run to show the number of events when the demo application created a random user, with each event printing a message for the number of users.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></kbd>


Looking at the detail of one log event, the message details the number of users (random user records) that the demo application has created.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/105.png" /></kbd>


## Reviewing Audit log in DynamoDb

Finally, we can explore the data in the DynamoDB table, which details the Lambda function activities for invoking the snapshot function. These activities are populated into a table.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/22.png" /></kbd>



---------------------------------------------------------------------------------------------


# Deployment Instructions

The CloudFormation template will build:

- Create a Kinesis Data Analytics platform with demo application
- Create a Lambda function with required permission roles
- Create an EventBridge rule for the automated creation of Snapshots
- Create a log group and log stream as part of KDA platform
- Create a CloudWatch dashboard with widgets related to KDA uptime, Snapshots, Checkpoints, and records
- Create a CloudWatch alarm for application downtime, Checkpoints and Snapshots
- Create an SNS topic for the notification of Snapshot creation events
- Create a DynamoDB table to detail the Lambda function activities for invoking the snapshot function. These activities are populated into the table

Using a single CloudFormation template, we will deploy the necessary AWS services to build a Kinesis Data Analytics platform with a demo application and to automate the creation of Snapshots based on a user-defined timeline.

The CloudFormation template will also build AWS services, including a Lambda function, a CloudWatch dashboard with widgets, an SNS topic and a DynamoDB table to store the snapshotting events.

## The CloudFormation document takes the following parameters
  1. Log monitoring level → set to 'INFO' to see all events, such as each record that is created
  2. Monitoring level → set to 'APPLICATION' as this will provide logs for the demo application activities
  3. Service-triggered Snapshots → left as 'true' to allow Snapshots to be created. This can be changed for testing (if you set it to false, you will see in the metrics and logs snapshots will not be created, and you will get an alarm)
  4. Number of Snapshots to retain → AWS KDA will retain up to 1000 Snapshots, but for testing purposes, this can be left at 10, whereby after 10 Snapshots are created, the oldest Snapshot is deleted when each new Snapshot is created
  5. Scaling → leave as 'true'
  6. How long to generate user data(test application) → the demo application creates random user information, and this will be done for X seconds, for example, 600 seconds or 10 minutes
  7. Delay between data generation(test application) → the time in milliseconds between each random user data record created


## Step 1

  From the /src folder, compile the Java source code to a JAR file, and upload the file to your S3 bucket of choice.

------------------------------------------------------------------------------------------------------------------


## Step 2: launch CloudFormation stack

  From the CloudFormation landing page, launch a stack with new resources:
  
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/100.png" /></kbd>
  
  
  
  The CloudFormation template should be stored in an S3 bucket of your choice, and then copy the object S3 URL of the template object into the CloudFormation template section:
  
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/14.png" /></kbd>
  
  
  
  On the next page of the CloudFormation creation process, enter a Stack name:
  
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/1.png" /></kbd>
  
  
  
  Below that, there are a number of parameters that can be defined for the CloudFormation stack:
  
    1. Log monitoring level → set to 'INFO' to see all events, such as each record that is created
    2. Monitoring level → set to 'APPLICATION' as this will provide logs for the demo application activities
    3. Service-triggered Snapshots → leave as 'true' to allow Snapshots to be created
    4. Number of Snapshots to retain → AWS KDA will retain up to 1000 Snapshots, but for testing purposes, this can be left at 10, whereby after 10 Snapshots are created, the oldest Snapshot is deleted when each new Snapshot is created
    5.Scaling → leave as 'true'
    6. How long to generate user data → the demo application creates random user information, and this will be done for X seconds, for example, 600 seconds or 10 minutes
    7. Delay between data generation → time in milliseconds between each random user data record created
  
  
  
    <kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/2.png" /></kbd>
  
  
  
  Below these parameters are the following three parameters:
  
    1. CloudWatch dashboard name
    2. Email address for SNS notifications
    3. SNS topic name
  
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/3.png" /></kbd>
  

  On the next page of the CloudFormation creation process, set an IAM role to allow the CloudFormation process to create all necessary resources.
  For the purpose of this demonstration, the role 'cloudformationKDA' has 'admin privileges'.
  
<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/4.png" /></kbd>
  
  
  
  The CloudFormation stack can now be created.

--------------------------------------------


## Step 3: Start the Demo Application

After the CloudFormation stack build has been completed, the Kinesis Data Analytics 'ApplicationName' can be found in the Outputs tab.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/16.png" /></kbd>



Navigate to the Kinesis Data Analytics page, and select this 'Streaming application'. This demo application needs to be started from the next page by clicking 'Run' on the right.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/103.png" /></kbd>



The following message will appear on the next page since the demo application has not yet run, and so no Snapshots have been created.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/5.png" /></kbd>

---------------------------------------------------------------------------------------------


## Step 4: Review CloudFormation Stack Resources

From CloudWatch EventBridge, we can see the 'kda-snapshots' rule for creating a Snapshot every 10 minutes.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/7.png" /></kbd>



From the Kinesis Data Analytics streaming application page, we can launch the Apache Flink dashboard to see the activity of the demo application.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/102.png" /></kbd>




We can see the number of random user records created (394 in this screenshot) on the Flink dashboard.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png" /></kbd>



From CloudWatch, we can also see the dashboard that has been created with the various widgets.

First, the demo application 'uptime' (and corresponding 'downtime') are shown.


<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/19.png" /></kbd>



Also, the snapshots and checkpoints metrics.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/20.png" /></kbd>



Lastly, the number of records is also shown (194), which matches the number of records sent and received in the Flink dashboard.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/21.png" /></kbd>


<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png" /></kbd>



Additionally, we can also see the CloudWatch alarms that are set up.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/8.png" /></kbd>


# Step 5: Review the Application, metrics, and logs

## KDA application page
We can see information on Snapshots. In this screenshot, it can be seen that four automated Snapshots have been created every 10 minutes, as well as a snapshot from a user-generated application 'stop'.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/10.png" /></kbd>


After the user-generated 'stop,' the demo application can be started again from the 'run' button. The latest Snapshot can be used for this application start, older snapshots, or without a snapshot.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/11.png" /></kbd>


## Reviewing Cloudwatch Logs

From the CloudFormation stack 'resources' tab, the log group and log stream results can be found. Using this information, you can access the  CloudWatch logs to monitor the demo application.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/18.png" /></kbd>



From CloudWatch Log Insights, and using the Log Group from the CloudFormation 'resources' tab, a query can be run to show when the demo application was started, in this screenshot, there are three records.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/12a.png" /></kbd>



Looking at the detail of two log events, the top events show the demo application was restored from context (the application was re-started with state). The bottom event was when the demo application was first started, and there was no Snapshot and hence no state.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/104.png" /></kbd>



A query can also be run to show the number of events when the demo application created a random user. Each event prints a message for the number of users.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></kbd>



Looking at the detail of one log event, the message details the number of users (random user records) that the demo application has created.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/105.png" /></kbd>



Finally, we can explore the data in the DynamoDB table, which details the Lambda function activities for invoking the snapshot function. These activities are populated into a table.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/22.png" /></kbd>



The demo application name, the Snapshot name, and other data are listed for each item.

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/23.png" /></kbd>
