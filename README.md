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

------------------------------------------------------------------------------------------------------------------


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



On the next page of the CloudFormation creation process, set an IAM role to allow the CloudFormation process to create all necessary resources.
For the purpose of this demonstration, the role 'cloudformationKDA' has 'admin privileges'.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/4.png)



The CloudFormation stack can now be created.

--------------------------------------------


## Step 3: Start the Java Application

After the CloudFormation stack build has completed, from the Outputs tab, the Kinesis Data Analytics 'ApplicationName' can be found.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/16.png)



Navigate to the Kinesis Data Analytics page, and select this 'Streaming application'. From the next page, this Java application needs to be started by clicking 'Run' on the right.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/103.png)



On the next page, the following message will appear, since the Java application has not yet run and so no Snapshots have been created.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/5.png)

---------------------------------------------------------------------------------------------


## Step 4: Review CloudFormation Stack Resources

From CloudWatch EventBridge, we can see the 'kda-snapshots' rule for creating a Snapshot every 10 minutes.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/7.png)



From the Kinesis Data Analystics streaming application page, we can launch the Apache Flink dashboard to see the activity of the Java application.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/102.png)




From the Fink dashboard, we can see the number of random user records created (394 in this screen shot).



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png)



From CloudWatch, we can also see the dashboard that has been created with the various widgets.

First, the Java application 'uptime' (and corresponding 'downtime') are shown.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/19.png)



Also, the snapshots and checkpoints metrics.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/20.png)



Lastly, the number of records is also shown (194), which matches the number of records sent and received in the Flink dashboard.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/21.png)



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/106.png)



Additionally, we can also see the CloudWatch alarms that are set up.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/8.png)


---------------------------------------------------------------------------------------------


## Step 5: Review the Results


From the Kinesis Data Analytics streaming application page, we can see information on Snapshots. In this screen shot, it can be seen that four automated Snapshots have been created every 10 minutes, as well as a snapshot from a user-generated application 'stop'.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/10.png)



After the user-generated 'stop' the Java application can be started again from the 'run' button. For this application start, the latest snapshot can be used, older snapshots, or without a snapshot.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/11.png)



From the CloudFormation stack 'resources' tab, the log group and log stream results can be found. Using this information, from CloudWatch logs, information on the Java application can be view.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/18.png)



From CloudWatch Log Insights, and using the Log Group from the CloudFormation 'resources' tab, a query can be run to show when the Java application was started, in this screen shot there are three records.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/12a.png)



Looking at the detail of two log events, the top events shows the Java application was restored from context (the application was re-started with state). The bottom events was when the Java application was first started, and there was no Snapshot and hence no state.



![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/104.png)


A query can also be run to show the number of events when the Java application created a random user, with each event printing a message for the amount of users.


<kbd>
![Image of S3 bucket](https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png)
</kbd>

<kbd><img src="https://github.com/riskfocus/rfs-kda-snapshot/blob/master/Images/9.png" /></kbd>
