# Executive Summary
To effectively manage a Flink Streaming Application on AWS Kinesis Data Analytics (KDA) and ensure high availability, Flink has two features. 

The first is Checkpoints are fully automated in KDA and used to restart if there is a failure and a node is replaced in the underlying EKS cluster. 

The second feature is Flink Savepoints, which KDA calls Snapshots. Savepoints are used to restart an application after it has been purposefully stopped or if there is a data problem and the user wants to restart the application from a previous point in time. These operations also need to be monitored to ensure they are completed and in a timely manner, as they impact application performance 

Currently, some of these features are not managed by the KDA service and require implementation by each application team. This project provides an out-of-the-box solution to automate the KDA Snapshot process and monitoring.

# The Challenge
The AWS KDA service fully automates the creation and management of Flink Checkpoints for High Availability, it creates and manages Snapshots when the application is shut down in an orderly fashion, and it provides an API to create Snapshots (Flink Savepoints) periodically. KDA also publishes CloudWatch metrics on Checkpoint and Snapshot completion and duration. 

It is the responsibility of each application to implement the following features to achieve "Operational Readiness" to support and manage a streaming application in production.

1. Invoking the API for Snapshots periodically for application recovery. This is done to handle application-level issues that require restating the application from a known point in time if there is a non-recoverable error on the cluster for any reason.

2. Create CloudWatch Dashboards and Alarms to monitor Checkpoints and Snapshots' performance. This will ensure they are running correctly and not taking longer than expected, as long-running checkpoints and snapshots will impact overall application performance.

# Why AWS
Correctly deploying, managing, scaling, and monitoring Flink to ensure High Availability and scaling to large numbers of CPUs can be a significant undertaking for your DevOps team. AWS Kinesis Data Analytics (KDA) is a fully managed service that allows applications teams to deploy and operate Flink applications. KDA follows AWS best practices and can scale applications massively with a fully managed service and significantly reduce the burn on a client's DevOps team.

# The Solution
Ness has created an accelerator in the form of a CloudFormation template, and Github Repo(click here) with which you can configure KDA Snapshots to run at any time interval required using a Lambda function and create CloudWatch dashboards and alarms for monitoring. This will ensure you can recover your application at any time from a known point and not lose data. 

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/212.png" /></p>

The accelerator consists of the following components:

1. Lambda function automates the Snapshot creation process via a CloudWatch EventBridge rule, an SNS Topic. It uses DynamoDB table to create an audit trail. 

2. A demo KDA application is created that allows you to test the Lambda function and Cloudwatch dashboards and alarms. To validate the application is creating Snapshots, we have implemented logging in the sample application when a Savepoint is created to make it easy to do a demo. 

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/214.png" /></p>

3. CloudWatch dashboard is created to report on the performance statistics of the KDA application, including Snapshots and Checkpoints. For demo purposes, metrics are included for the demo KDA application.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/206.png" /></p>

4. CloudWatch alarms are created for Snapshot duration (to track problems with increasing times and Checkpoint duration and failed Checkpoints.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/210.png" /></p>

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/209.png" /></p>

---------------------------------------------------------------------------------------------

## How the Demo Application works

### User Story
As a developer, I want a simple Flink application that can be used to demonstrate Flink Snapshots so that we can see that it starts correctly and initializes state from the snapshot.

* For demonstration purposes, the app generates random users with information (name and age). It tabulates statistics on the number of people of a certain age and the total number of users.
* The application should create log entries to clarify when the application starts with and without a Snapshot and when a Snapshot is generated. 


### Acceptance Criteria
1. Create a Java Flink application that generates user data (name and age) based on a time interval

2. Print a log entry when starting:

- Starting without a Snapshot. “The application was not restored from context.”

- Starting with a Snapshot. “The application was restored from context.” 

- When a Snapshot is created, log: "Triggering Savepoint for job….” 

- Log what is in the value state (total records, sum values) 


<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/215.png" /></p>


<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/214.png" /></p>


---------------------------------------------------------------------------------------------

# Sample Cloudwatch Dashboard and Log output

## Reviewing Cloudwatch Logs to check Snapshot Operation

The KDA Snapshot operation is automated based on an AWS EventBridge rule invoking a Lambda function at the specified time interval. The Lambda function then calls the KDA API to initiate the Snapshot process, while also sending statistics to CloudWatch and DynamoDB. To ensure that the Snapshot process is working correctly, CloudWatch alarms are established to track the process, to alarm based on conditions, and to notify via SNS message if the alarm is 'in alarm'.

Firstly, if the KDA application is not running, a Snapshot cannot be taking. At every time interval specified when a Snapshot is to be taken, the following message would then be received via SNS message, starting the Snapshot process is working as expected, but a Snapshot cannot be taken.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/216g.png</p>

Additionally, if the Snapshot process is not working correctly, the duration of each Snapshot could take longer than expected. The threshold for the alarm is 30 seconds, and so if the Snapshot is taking longer than this threshold, an SNS message would be sent as follows.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/217.png/p>

From CloudWatch Log Insights, and using the Log Group from the CloudFormation 'resources' tab, a query can be run to show when the demo application was started; in this screenshot, there are three records.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/12a.png" /></p>

Looking at the detail of two log events, the top events show the demo application was restored from context (the application was re-started with state). The bottom event was when the demo application was first started, and there was no Snapshot and hence no state.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/107.jpg" /></p>


## Reviewing Cloudwatch Logs Check Demo Application events generated

A query can also be run to show the number of events when the demo application created a random user, with each event printing a message for the number of users.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></p>


Looking at the detail of one log event, the message details the number of users (random user records) that the demo application has created.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/108.jpg" /></p>


## Reviewing Audit log in DynamoDB

Finally, we can explore the data in the DynamoDB table, which details the Lambda function activities for invoking the snapshot function. These activities are populated into a table.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/22.png" /></p>

---------------------------------------------------------------------------------------------

# Feedback
If you have any feedback, please raise issues in the Github project and feel free to submit PRs to suggest improvements.


---------------------------------------------------------------------------------------------


# Deployment Instructions

The CloudFormation template will build:

- Create a Kinesis Data Analytics platform with demo Java application
- Create a Lambda function with required permission roles
- Create an EventBridge rule to invoke the Lambda function and automate the creation of Snapshots
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
  5. Scaling → This allows you to enable/disable autoscaling in KDA for testing, but for this demo you can always leave it as 'True', but it maybe a useful feature for deploying real applications.
  6. How long to generate user data(test application) → the demo application creates random user information, and this will be done for X seconds, for example, 600 seconds or 10 minutes
  7. Delay between data generation(test application) → the time in milliseconds between each random user data record created


## Step 1

  Create an S3 bucket in the region of your choice, and upload both the Zip file that contains the Lambda code, and the JSON CloudFormation templete file to that bucket. From the /src folder, compile the Java source code to a JAR file, and upload the file to that same S3 bucket.
  

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/213.png" /></p> 

------------------------------------------------------------------------------------------------------------------


## Step 2: launch CloudFormation stack

  From the CloudFormation landing page, launch a stack with new resources:
  
<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/100.jpg" /></p>
  
  
  
  From your S3 bucket, copy the object S3 URL of the template object into the CloudFormation template section:
  
<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/101.jpg" /></p>
  
  
  
  On the next page of the CloudFormation creation process, enter a Stack name:
  
<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/1.png" /></p>
  
  
  
  Below that, there are a number of parameters that can be defined for the CloudFormation stack:
  
    1. Log monitoring level → set to 'INFO' to see all events, such as each record that is created
    2. Monitoring level → set to 'APPLICATION' as this will provide logs for the demo application activities
    3. Service-triggered Snapshots → leave as 'true' to allow Snapshots to be created
    4. Number of Snapshots to retain → AWS KDA will retain up to 1000 Snapshots, but for testing purposes, this can be left at 10, whereby after 10 Snapshots are created, the oldest Snapshot is deleted when each new Snapshot is created
    5. Scaling → leave as 'true', which would allow the KDA application to scale
    6. S3 bucket → the name of the bucket where the files are located
    7. Java application → the name of the JAR file created
    8. Lambda code → the name of the Zip file with the Lambda code
    9. How long to generate user data → the demo application creates random user information, and this will be done for X seconds, for example, 600 seconds or 10 minutes
    10. Delay between data generation → time in milliseconds between each random user data record created
  
  
 
  <p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/211.png" /></p>


  Below these parameters are the following three parameters:
  
    1. CloudWatch dashboard name
    2. Email address for SNS notifications
    3. SNS topic name
  
<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/3.png" /></p>
  

  On the next page of the CloudFormation creation process, set an IAM role to allow the CloudFormation process to create all necessary resources.
  For the purpose of this demonstration, the role 'cloudformationKDA' has 'admin privileges'.
  
<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/102.jpg" /></p>
  
  
  
  The CloudFormation stack can now be created.

--------------------------------------------


## Step 3: Start the Demo Application

After the CloudFormation stack build has been completed, the Kinesis Data Analytics 'ApplicationName' can be found in the Outputs tab.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/103.jpg" /></p>



Navigate to the Kinesis Data Analytics page, and select this 'Streaming application'. This demo application needs to be started from the next page by clicking 'Run' on the right.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/104.jpg" /></p>



The following message will appear on the next page since the demo application has not yet run, and so no Snapshots have been created.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/5.png" /></p>

---------------------------------------------------------------------------------------------


## Step 4: Review CloudFormation Stack Resources

From CloudWatch EventBridge, we can see the 'kda-snapshots' rule for creating a Snapshot every 10 minutes.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/105.jpg" /></p>



From the Kinesis Data Analytics streaming application page, we can launch the Apache Flink dashboard to see the activity of the demo application.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.jpg" /></p>




We can see the number of random user records created (394 in this screenshot) on the Flink dashboard.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png" /></p>



From CloudWatch, we can also see the dashboard that has been created with the various widgets.

First, the demo application 'uptime' (and corresponding 'downtime') are shown.


<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/19.png" /></p>



Also, the snapshots and checkpoints metrics.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/20.png" /></p>



Lastly, the number of records is also shown (194), which matches the number of records sent and received in the Flink dashboard.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/21.png" /></p>


<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png" /></p>



Additionally, we can also see the CloudWatch alarms that are set up.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/8.png" /></p>


# Step 5: Review the Application, metrics, and logs

## KDA application page
We can see information on Snapshots. In this screenshot, it can be seen that four automated Snapshots have been created every 10 minutes, as well as a snapshot from a user-generated application 'stop'.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/10.png" /></p>


After the user-generated 'stop,' the demo application can be started again from the 'run' button. The latest Snapshot can be used for this application start, older snapshots, or without a snapshot.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/11.png" /></p>


## Reviewing Cloudwatch Logs

From the CloudFormation stack 'resources' tab, the log group and log stream results can be found. Using this information, you can access the  CloudWatch logs to monitor the demo application.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/18.png" /></p>



From CloudWatch Log Insights, and using the Log Group from the CloudFormation 'resources' tab, a query can be run to show when the demo application was started, in this screenshot, there are three records.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/12a.png" /></p>



Looking at the detail of two log events, the top events show the demo application was restored from context (the application was re-started with state). The bottom event was when the demo application was first started, and there was no Snapshot and hence no state.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/107.jpg" /></p>



A query can also be run to show the number of events when the demo application created a random user. Each event prints a message for the number of users.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></p>



Looking at the detail of one log event, the message details the number of users (random user records) that the demo application has created.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/108.jpg" /></p>



Finally, we can explore the data in the DynamoDB table, which details the Lambda function activities for invoking the snapshot function. These activities are populated into a table.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/22.png" /></p>



The demo application name, the Snapshot name, and other data are listed for each item.

<p align="center"><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/23.png" /></p>
