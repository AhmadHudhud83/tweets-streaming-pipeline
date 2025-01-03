import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.io.Source
import java.util.concurrent.TimeUnit

object RawDataProducer {
  def main(args: Array[String]): Unit = {
    // Kafka properties config
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // Settings for simulating stream
    props.put("linger.ms", "1000")
    props.put("batch.size", "16384")

    // Kafka Producer
    val producer = new KafkaProducer[String, String](props)
    val topic = "raw-tweets-topic"

    // Reading tweets json file line by line
    val path ="data/boulder_flood_geolocated_tweets.json"
    val source = Source.fromFile(path)
    val lines = source.getLines()

    // Collect messages in a queue buffer (batching them)
    val buffer = new scala.collection.mutable.Queue[String]()

    try {
      lines.foreach { line =>
        // Add the line to the buffer
        buffer.enqueue(line)

        // If the buffer is full, send the batch to Kafka
        if (buffer.size >= 10) {
          sendTweetsBatch(producer, topic, buffer)
          // Clear the buffer
          buffer.clear()
          //Simulated Delay by 2-5 secs
          TimeUnit.SECONDS.sleep(5)

        }
      }

      // If stream stopped and there  remained any tweets in queue , then send them
      if (buffer.nonEmpty) {
        sendTweetsBatch(producer, topic, buffer)
      }
    }
      source.close()
      producer.close()

  }

  // Send Batch of tweets stream function
  def sendTweetsBatch(producer: KafkaProducer[String, String], topic: String, buffer: scala.collection.mutable.Queue[String]): Unit = {
    buffer.foreach { line =>
      val record = new ProducerRecord[String, String](topic, null, line)
      producer.send(record)

      println(s"Sent: $line")
    }
  }
}
