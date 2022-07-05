import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class WordCount {
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

        Dataset<Row> dataSet = sparkSession.read().text("README.md");
        dataSet.printSchema();
        /**
         * root
         *  |-- value: string (nullable = true)
         *
         *  it's string, but how do i know what type that is in java?
         */

        sparkSession.read().text("README.md")
                .map((FlatMapFunction<Row, String>)  row -> row.getAs("value").getClass().getCanonicalName())

        //sparkSession.read().text("README.md")
        //        .flatMap((FlatMapFunction<Row, String>)  row -> row.)
    }
}