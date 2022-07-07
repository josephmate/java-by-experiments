

mvn install \
	&& spark-3.3.0-bin-hadoop3/bin/spark-submit --class CharacterCount target/spark.protobuf.experiment-1.0.0.jar
