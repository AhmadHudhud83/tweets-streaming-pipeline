package processing

import com.models.TweetSchema
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json, lit, struct, to_json, to_timestamp}
import org.apache.spark.sql.types.StringType
import SentimentAnalyzer._
object ProcessingTweets {
    def processingTweets (df:DataFrame): DataFrame ={
     val streaming_df = BasicProcessing.basicProcessing(df,TweetSchema.RawTweetSchema())





     // Extract nested hashtags from dataframe
      val extracted_df = streaming_df.withColumn("hashtags",col("entities.hashtags.text")).drop(col("entities"))


     // Convert created_at rfc8 date format to timestamp format
      val convertedDateDF = extracted_df.withColumn("timestamp",to_timestamp(col("created_at"),"EEE MMM dd HH:mm:ss Z yyyy")).drop("created_at")

      val sentimentDF = SentimentAnalyzer.sentimentAnalyzer(convertedDateDF)
     //debugging
     println("Converted DF Schema : ")
     sentimentDF.printSchema()





     return sentimentDF


    }
}
