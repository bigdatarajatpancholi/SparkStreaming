#!/usr/bin/python
from pyspark import SparkContext
sc = SparkContext()
commonWords = ["a", "an", "the", "of", "at", "is", "am", "are", "this", "that", '', 'at']
file = sc.textFile("hdfs://hadoop1.knowbigdata.com/data/mr/wordcount/input/")
commonWordsBC = sc.broadcast(commonWords)
# Create Accumulator[Int] initialized to 0 
def toWords(line):
        words = line.split(" ")
        output = [];
        for word in words:
                word = word.lower();
                cws = commonWordsBC.value;
                if word not in cws:
                        output.append(word)
        return output;
uncommonWords = file.flatMap(toWords)
uncommonWords.saveAsTextFile("hdfs://hadoop1.knowbigdata.com/user/student/wc_uncommon_sg")
