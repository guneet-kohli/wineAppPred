package org.example.winepredictionapp;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ModelPrediction {
    private static final String TESTING_DATASET = "s3a://wineprediction7/TestDataset.csv";
    private static final String MODEL_PATH = "s3a://wineprediction7/LogisticRegressionModel";
    public void run(SparkSession spark) {
        PipelineModel pipelineModel = PipelineModel.load(MODEL_PATH);
        Dataset<Row> testDf = getDataFrame(spark, true, TESTING_DATASET).cache();
        Dataset<Row> predictionDF = pipelineModel.transform(testDf).cache();
        predictionDF.select("features", "label", "prediction").show(5, false);

        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();

        evaluator.setMetricName("accuracy");
        double accuracy1 = evaluator.evaluate(predictionDF);
        System.out.println("Test Error = " + (1.0 - accuracy1));     
        
        evaluator.setMetricName("f1");
        double f1 = evaluator.evaluate(predictionDF);
        System.out.println("F1: " + f1);
    }

    public Dataset<Row> getDataFrame(SparkSession spark, boolean transform, String name) {

        Dataset<Row> validationDf = spark.read().format("csv").option("header", "true")
                .option("multiline", true).option("sep", ";").option("quote", "\"")
                .option("dateFormat", "M/d/y").option("inferSchema", true).load(name);

        Dataset<Row> lblFeatureDf = validationDf.withColumnRenamed("quality", "label").select("label",
                "alcohol", "sulphates", "pH", "density", "free sulfur dioxide", "total sulfur dioxide",
                "chlorides", "residual sugar", "citric acid", "volatile acidity", "fixed acidity");

        lblFeatureDf = lblFeatureDf.na().drop().cache();

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[] { "alcohol", "sulphates", "pH", "density",
                        "free sulfur dioxide", "total sulfur dioxide", "chlorides", "residual sugar",
                        "citric acid", "volatile acidity", "fixed acidity" })
                .setOutputCol("features");

        if (transform)
            lblFeatureDf = assembler.transform(lblFeatureDf).select("label", "features");

        return lblFeatureDf;
    }
}
