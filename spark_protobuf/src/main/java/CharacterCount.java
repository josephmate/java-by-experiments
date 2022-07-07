import org.apache.spark.sql.SparkSession;

import java.util.Arrays;

public class CharacterCount {
    public static void main(String[] args) {
        SparkSession sparkSession = SparkSession
                .builder()
                .appName("CharacterCount")
                .getOrCreate();


        /* Copied from https://spark.apache.org/docs/latest/quick-start.html
            scala> val textFile = spark.read.textFile("README.md")
            scala> val wordCounts = textFile.flatMap(line => line.split(" ")).groupByKey(identity).count()
            scala> wordCounts.collect()
         */
        System.out.println(
            sparkSession.read().text("README.md")
                .javaRDD()
                .flatMap(row -> Arrays.stream(row.toString().split("")).iterator()
                )
            .groupBy(row -> row)
            .countByKey()
        );
        /*
        {core,=1, =1, X=1, for=1, 2.=1, spark's=1, this=1, count=1, in=1, Intellij=1,  ...
         */

    }
}