mvn install 

retVal=$?
if [ $retVal -ne 0 ]; then
      echo "Compile error"
fi

mvn exec:java \
  -Dexec.mainClass="Main" \
  -Dexec.args="$*" \
  2>&1  \
  | tee out.txt
