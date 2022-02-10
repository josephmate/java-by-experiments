
javac Main.java
retVal=$?
if [ $retVal -ne 0 ]; then
  echo "Error"
  exit $retVal
fi


java -Xmx16g Main $1
retVal=$?
if [ $retVal -ne 0 ]; then
  echo "Error"
  exit $retVal
fi
