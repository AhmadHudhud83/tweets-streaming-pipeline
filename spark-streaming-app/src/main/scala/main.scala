import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp._
import org.apache.spark.ml._
import com.johnsnowlabs.nlp.util.io.ReadAs
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender
import com.models.Tweet

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

    // Tweets Schema
    val tweetSchema = Tweet.TweetSchema()
    // Select and cast key and value to String
    val kafka_df_casted = kafka_df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

    //streaming df

    val streaming_df = kafka_df_casted.withColumn("values_json",from_json(col("value"),tweetSchema)).selectExpr("values_json.*")

    // Streaming df schema for debugging
    println("StreamingDF Schema : ")
    streaming_df.printSchema()
    // Extract nested hashtags from dataframe

    val extracted_df = streaming_df.withColumn("hashtags",col("entities.hashtags.text")).drop(col("entities"))
    println("Exlpoded DF Schema : ")
    extracted_df.printSchema()

    val df_filtered = extracted_df.filter(col("geo").isNotNull)



    // Adding Keys & Values to store on processed topic , serialization of the whole row as JSON

    val final_df = df_filtered.
      withColumn("key",lit(null).cast(StringType)).
      withColumn("value",to_json(struct(df_filtered.columns.map(col):_*))).

      select("key","value")

    final_df.printSchema()












    // Display the data on the console
    val query = final_df
      .writeStream
      .outputMode("append")
      .format("console")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("topic","processed-tweets-topic")
      .option("checkpointLocation","checkpoint_dir_kafka")
      .option("truncate","false")
      .start()

    query.awaitTermination()












  }
}
