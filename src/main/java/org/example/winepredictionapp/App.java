package org.example.winepredictionapp;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final String ACCESS_KEY_ID = "ASIA5FTY7O2RO4DFVKTP";
    private static final String SECRET_KEY = "iwIW/iPkqHRg7RXYcK3oPUBwGPRWCBydWydvX0Sm";

    private static final String MASTER_URI = "local[*]";
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("Apache Spark Wine Prediction").master(MASTER_URI)
                .config("spark.executor.memory", "3g")
                .config("spark.driver.memory", "12g")
                .config("spark.jars.packages", "org.apache.hadoop:hadoop-aws:3.2.2")
                .getOrCreate();

        spark.sparkContext().hadoopConfiguration().set("fs.s3a.aws.credentials.provider",
                "com.amazonaws.auth.InstanceProfileCredentialsProvider,com.amazonaws.auth.DefaultAWSCredentialsProviderChain");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", ACCESS_KEY_ID);
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", SECRET_KEY);

        // ModelPrediction parser = new ModelPrediction();
        ModelTraining parser = new ModelTraining();
        parser.run(spark);

        spark.stop();
    }
}
