import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp._
import org.apache.spark.ml._
import com.johnsnowlabs.nlp.util.io.ReadAs
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender
import com.models.TweetSchema
import org.apache.spark.sql.streaming.Trigger
import processing.ProcessingTweets
object Main {

  def main(args: Array[String]): Unit = {
    val nullAppender = new NullAppender
    BasicConfigurator.configure(nullAppender)


  //Spark Config
    val spark = SparkSession
      .builder()
      .appName("Spark Tweets Streaming")
      .config("spark.streaming.stopGracefullyOnShutdown", "true")
      .config("spark.sql.shuffle.partitions",4)
      .config("spark.sql.legacy.timeParserPolicy", "LEGACY")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    // Creating Kafka Dataframe
    val kafka_df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "raw-tweets-topic")
      .option("startingOffsets", "earliest")
      .load()






    // Adding Keys & Values to store on processed topic , serialization of the whole row as JSON

    val final_df = ProcessingTweets.processingTweets(kafka_df)













    // Display the data on the console
    val query = final_df
      .writeStream
      .outputMode("append")
      .format("kafka")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("topic","processed-tweets-topic")
      .option("checkpointLocation","temp/kafka_checkpoint")
      .option("truncate","false")
      .start()

    query.awaitTermination()












  }
}
