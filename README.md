# winepredictionapp
Parallel machine learning (ML) applications in Amazon AWS cloud platform using Apche Spark
# AWS Setup Guide

## Setting Up AWS Environment

1. Obtain AWS learner module access and set up your account.
2. Start the AWS Lab and launch the AWS Management Console.

## EMR Cluster Setup

1. Navigate to services and select "EMR".
2. Choose EMR Configuration:
   - EMR Version: 5.36.0
   - Custom Configuration: Hadoop 2.10.1, Spark 2.4.8, Zeppelin 0.10.0
   - Set up Cluster Configuration:
     - Primary Instance: m4.large
     - Core Instance: m4.large
     - Task Instance: m4.large
     - Select instance size as 4 for both core and tasks.
3. Create two instances with the specified configuration.
4. Create the cluster and wait for the status to change from "starting" to "waiting".

## Network Firewall Setup

1. Go to Security Firewall and select "Primary".
2. In Inbound Network Rules, select Edit.
3. Create a new rule to allow SSH connection from your IP.

## Connecting via SSH

1. Create a folder and move Java files and key pair file inside the folder.
2. Open Terminal and set the current directory using the cd command to the directory with the ".pem" and ".java" files.
   Command: cd /user/path/folder
3. Check key pair file access.
   Command: chmod 400 keypair.pem
4. Connect to the EMR Cluster using the below command:
ssh -i /filepath/keypair.pem hadoop@ec2-107-23-222-201.compute-1.amazonaws.com


## Creating S3 Bucket

1. Go to services and select "S3".
2. Choose a bucket name and go with default settings.
3. Create the bucket.
4. Upload the datasets: training.csv and validation.csv.
5. Upload both Java files: training and prediction, as well as the Dockerfile to the bucket.
6. Use this bucket to save the model after running it through the EMR Cluster.

## Publishing Java Files to EMR Instances

1. Host the training and prediction Java codes in EMR.
2. Sync files to the EMR primary instance with AWS S3 bucket using the command:
aws s3 sync s3://bucket-name

3. Verify the files using ls commands.

## Creating Docker Image

1. Create an account on Docker Hub and save credentials.
2. In Terminal, use the command: docker login
3. Build a repository using this command:
docker build -t path/pa2
4. Run prediction in Docker:
docker run path/pa2
5. Push your image to the Docker container:
docker push path/pa2


## Training ML Model in Spark Cluster with 4 EC2 Instances in Parallel

1. Perform SSH to the master of the cluster using the following command:
ssh -i "ec2key.pem" <<User>>@<<Public IPv4 DNS>>
2. Once logged in successfully, change to the root user by running the command:
sudo su
3. Submit the job using the following command:
Run training: spark-submit train.py
4. You can trace the status of this job in the EMR UI application logs. Once the status is succeeded, you can see trainingmodel.model created within the S3 bucket.
5. After that, re-sync the S3 bucket with the cluster to read the model and run prediction code using:
spark-submit predict.py



