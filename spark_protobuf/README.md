
== TODO ==

1. copy word count sample and get it running
2. try to convert it ot character count
3. create a fake, denormalized protobuf model for a library
   with most of the entries in the same genre
4. create a tool that can generate X records
4. how to load the data from binary files?
5. implement a job that sorts based on the genre (to demonstrate skew distribution) and how it impacts you unexpectedly
6. workaround using sort on genre+id
7. how fast is it?
8. do some more complicated calculation

== Journey ==

1. Not sure where to get the text file's line from
    1. Solution: printed the schema and found a value column
1. SparkSession not found by Intellij
    1. Root Cause: SparkSession is not in core, but in spark sql? Added it as provided since spark's classloader will provide it
1. TODO: not sure how string is translated to Java.
    1. Solution: print the classname after calling .getAs("value")
