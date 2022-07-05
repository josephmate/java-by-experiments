VERSION_SHORT=spark-3.3.0
VERSION=spark-3.3.0-bin-hadoop3
if [ ! -d "$VERSION" ]
then
	curl -o $VERSION.tgz https://dlcdn.apache.org/spark/$VERSION_SHORT/$VERSION.tgz
	tar xf $VERSION.tgz
	rm $VERSION.tgz
fi

spark-3.3.0-bin-hadoop3/sbin/start-master.sh 
spark-3.3.0-bin-hadoop3/sbin/start-workers.sh 

