import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object KafkaWordCount {
    def main(args: Array[String]) {
        if (args.length < 3) {
            System.err.println("Usage: KafkaWordCount <zkQuorum> <consumerGroup> <topic>")
                System.exit(1)
        }
        val Array(zkQuorum, consumerGroup, topic) = args
        val kafkaParams = Map[String, Object](
          "bootstrap.servers" -> zkQuorum,
          "key.deserializer" -> classOf[StringDeserializer],
          "value.deserializer" -> classOf[StringDeserializer],
          "group.id" -> consumerGroup,
          "auto.offset.reset" -> "latest",
          "enable.auto.commit" -> (false: java.lang.Boolean)
        )
        
        val conf = new SparkConf().setMaster("local[*]").setAppName("KafkaWordCount")
        val ssc = new StreamingContext(conf, Seconds(2))
	ssc.sparkContext.setLogLevel("ERROR")
        val stream = KafkaUtils.createDirectStream[String, String](
          ssc,
          PreferConsistent,
          Subscribe[String, String](Array(topic), kafkaParams)
        )

        var lines = stream.map(record => record.value)
//         result.print()
//         val lines = KafkaUtils.createStream(ssc, zkQuorum, consumerGroup, Map(topic -> 1)).map(_._2)
        val words = lines.flatMap(_.split(" "))
        val pairs = words.map(word => (word, 1))
        val wordCounts = pairs.reduceByKey(_ + _)
        wordCounts.print()
        ssc.start()
        ssc.awaitTermination()
    }
}
