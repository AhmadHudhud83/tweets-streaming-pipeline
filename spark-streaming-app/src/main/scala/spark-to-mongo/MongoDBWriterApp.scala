
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender
import org.apache.spark.sql.streaming.Trigger
import com.mongodb.client._
import org.bson.Document
import processing.{BasicProcessing, ProcessingTweets}
import com.models.TweetSchema
object MongoDBWriterApp {

  def main(args: Array[String]): Unit = {
    val nullAppender = new NullAppender
    BasicConfigurator.configure(nullAppender)


    val mongoClient = MongoClients.create("mongodb+srv://ahmad:ahmad1212@cluster0.aq5ou.mongodb.net")
    val database = mongoClient.getDatabase("SparkTweetsStorage")
    val collection = database.getCollection("tweets")

    // Define Indices
    collection.createIndex(new Document("id", 1))
    collection.createIndex(new Document("created_at", 1))
    collection.createIndex(new Document("text", "text"))
    collection.createIndex(new Document("geo", 1))
    collection.createIndex(new Document("hashtags", 1))
    //Spark Config
    val spark = SparkSession
      .builder()
      .appName("MongoDB Writer")
      .config("spark.streaming.stopGracefullyOnShutdown", "true")
      .config("spark.sql.shuffle.partitions",2)

      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    // Creating Kafka Dataframe
    val kafka_df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "processed-tweets-topic")
      .option("startingOffsets", "earliest")
      .load()



    val final_df = BasicProcessing.basicProcessing(kafka_df,TweetSchema.ProcessedTweetSchema())


    val query = final_df
      .writeStream
      .format("mongodb")
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .option("checkpointLocation", "temp/mongodb_checkpoint")
      .option("forceDeleteTempCheckpointLocation", "true")
      .option("spark.mongodb.connection.uri", "mongodb+srv://ahmad:ahmad1212@cluster0.aq5ou.mongodb.net")
      .option("spark.mongodb.database","SparkTweetsStorage")
      .option("spark.mongodb.collection", "tweets")
      .option("truncate","false")
      .outputMode("append")
      .start()

    query.awaitTermination()


  mongoClient.close()









  }
}
