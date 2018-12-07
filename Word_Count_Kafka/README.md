# Spark streaming using Kafka

In this project we will build a spark application which will consume and process the data from the kafka using spark streaming library.


## Step 0 - Getting the repository


    # Clone the repository

    git clone https://github.com/bigdatarajatpancholi/SparkStreaming.git

    # If already cloned then update the repository

    cd ~/SparkStreaming && git pull origin master

    # Code is inside Word_Count_Kafka

    # Build the sbt project
    cd Word_Count_Kafka
    sbt clean && sbt package
    # To understand the code please go thru WordCountKafka.scala


## Step 1 - Interacting with Kafka - Create Topic

Execute the following commands on the terminal. Please read thru the comments to get idea about the command

    # Include Kafka binaries in the path. HDP includes the kafka and installs at /usr/hdp/current/kafka-broker
    export PATH=$PATH:/usr/hdp/current/kafka-broker/bin

    # Locate the kafka brokers
    # The kafka brokers inform zookeeper about their IPs adresses. Most of the eco-system considers the zookeeper as a central registry.
    # First launch zookeeper client
    zookeeper-client
    
    
    # I found the for me the location of one of the brokers was ip-172-31-38-146.ec2.internal:6667
       
    # Create the topic
    # Replace localhost with the hostname of node where zookeeper server is running. Generally, zk runs on all hosts on the cluster.
    # Replace test with your topic name
    kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic rajatpancholi_test

    # Check if topic is created
    kafka-topics.sh  --list --zookeeper localhost:2181

## Step 2 - Interacting with Kafka - Produce 

    # find the ip address of any broker from zookeeper-client using command get /brokers/ids/0
    # On the zookeeper-client prompt, list all the brokers that registered
    ls /brokers/ids
    
    # Now, get the information about all the ids using the get command with the nodes listed in previous command
    # For example:
    get /brokers/ids/1001
    get /brokers/ids/1002
    get /brokers/ids/1003

    
    # test is a topicname here. Replace test with some name specific to your username like rajatpancholi_test
    # Replace localhost with the hostname of broker
    # Replace test with your topic name
    kafka-console-producer.sh --broker-list ip-172-31-38-146.ec2.internal:6667 --topic rajatpancholi_test

    # Push messages to topic, type "my first kafka topic"

    # Test if producer is working by consuming messages in another terminal
    # Replace test with your topic name
    kafka-console-consumer.sh --zookeeper localhost:2181 --topic rajatpancholi_test --from-beginning

    # Produce a lot
    # Replace localhost with the hostname of broker
    kafka-console-producer.sh --broker-list ip-172-31-38-146.ec2.internal:6667 --topic rajatpancholi_test 
    
    #This will give you a prompt to type the input which will be pushed to the topic
    # Say I typed here: this is a cow this is a bow
    
## Step 3 - Launching the spark streaming
    
    # Leave the producer prompt open and open a new terminal
    # If you are already not in the project folder, please change directory
    cd ~/SparkStreaming/Word_Count_Kafka
    
    # Build the package
    # Note that there are only two files in the project: build.sbt and one scala program
    # Using scala build tool, first clean the build and then package it
    # Notice that "&&" is the usual unix operator to chain commands
    sbt clean && sbt package

    # Run the Spark streaming code
    # Use new topic
    # Replace rajatpancholi_test with your new topic name
    spark-submit --class "KafkaWordCount" --jars spark-streaming-kafka-0-10_2.11-2.3.0.jar target/scala-2.11/kafkawordcount_2.11-1.0.jar ip-172-31-38-146.ec2.internal:6667 spark-streaming-consumer-group rajatpancholi_test


    #or use below command for spark-submit
    
    spark-submit --class "KafkaWordCount" target/scala-2.11/kafkawordcount_2.11-1.0.jar ip-172-31-38-146.ec2.internal:6667 spark-streaming-consumer-group rajatpancholi_test
    
    
    #Optional: If you dont want too much debugging information you can redirect some to /dev/null device in unix
    spark-submit --class "KafkaWordCount" --jars spark-streaming-kafka-0-10-assembly_2.11-2.3.0.jar target/scala-2.11/kafkawordcount_2.11-1.0.jar ip-172-31-38-146.ec2.internal:6667 spark-streaming-consumer-group rajatpancholi_test 2>/dev/null
    
This will start printing the word count of whatever you are typing in step 2.

## Done!

This is how the output would look like:

<pre>
-------------------------------------------
Time: 1542458016000 ms
-------------------------------------------
(a,2)
(is,2)
(bow,1)
(cow,1)
(this,2)
</pre>
