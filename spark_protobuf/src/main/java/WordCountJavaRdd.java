import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.List;

public class WordCountJavaRdd {
    public static void main(String[] args) {
        SparkSession sparkSession = SparkSession
                .builder()
                .appName("Spark SQL basic example")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();


        /* Copied from https://spark.apache.org/docs/latest/quick-start.html
            scala> val textFile = spark.read.textFile("README.md")
            scala> val wordCounts = textFile.flatMap(line => line.split(" ")).groupByKey(identity).count()
            scala> wordCounts.collect()
         */
        System.out.println(
            sparkSession.read().text("README.md")
                .javaRDD()
                .flatMap(row -> Arrays.stream(row.getAs("value")
                        .toString()
                        .split(" "))
                        .iterator()
                )
            .groupBy(row -> row)
            .countByKey()
        );
        /*
        {core,=1, =1, X=1, for=1, 2.=1, spark's=1, this=1, count=1, in=1, Intellij=1,  ...
         */

    }
}