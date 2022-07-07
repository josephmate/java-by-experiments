import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.List;

public class WordCount {
    public static void main(String[] args) {
        SparkSession sparkSession = SparkSession
                .builder()
                .appName("WordCount")
                .getOrCreate();

        Dataset<Row> dataSet = sparkSession.read().text("README.md");
        dataSet.printSchema();
        /*
         * root
         *  |-- value: string (nullable = true)
         *
         *  it's string, but how do i know what type that is in java?
         */

        List<String> types = sparkSession.read().text("README.md")
                .map((MapFunction<Row, String>) row -> row.getAs("value").getClass().getCanonicalName(),
                        Encoders.STRING())
                .collectAsList();
        if (!types.isEmpty()) {
            System.out.println(types.get(0));
        }
        /*
         * output: java.lang.String
         */


        /* Copied from https://spark.apache.org/docs/latest/quick-start.html
            scala> val textFile = spark.read.textFile("README.md")
            scala> val wordCounts = textFile.flatMap(line => line.split(" ")).groupByKey(identity).count()
            scala> wordCounts.collect()
         */
        System.out.println(
            sparkSession.read().text("README.md")
                .flatMap((FlatMapFunction<Row, String>) row -> Arrays.stream(row.getAs("value")
                                .toString()
                                .split(" "))
                                .iterator(),
                        Encoders.STRING())
                .groupByKey((MapFunction<String, String> ) i -> i, Encoders.STRING() )
                .count()
                .collectAsList()
        );
        /*
        [(some,1), (column,1), (character,1), (sorts,1), (copy,1), ...
         */

    }
}