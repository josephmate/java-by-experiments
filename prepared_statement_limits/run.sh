mvn --batch-mode install > compile.txt 2>&1

mvn --batch-mode exec:java \
  -Dexec.mainClass="Main" \
  -Dexec.args="$1 $2 $3 $4 $5" \
  2> err.txt \
  | tee out.txt
