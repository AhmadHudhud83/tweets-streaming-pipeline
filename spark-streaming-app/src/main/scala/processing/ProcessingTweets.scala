package processing

import com.models.TweetSchema
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json, lit, struct, to_json, to_timestamp}
import org.apache.spark.sql.types.StringType
import SentimentAnalyzer._
object ProcessingTweets {
    def processingTweets (df:DataFrame): DataFrame ={
     val streaming_df = BasicProcessing.basicProcessing(df,TweetSchema.RawTweetSchema())



     // filter geo null rows
      val df_filtered =streaming_df.filter(col("geo").isNotNull)

     // Extract nested hashtags from dataframe
      val extracted_df = df_filtered.withColumn("hashtags",col("entities.hashtags.text")).drop(col("entities"))

     // Convert created_at rfc8 date format to timestamp format
      val convertedDateDF = extracted_df.withColumn("timestamp",to_timestamp(col("created_at"),"EEE MMM dd HH:mm:ss Z yyyy")).drop("created_at")

      val sentimentDF = SentimentAnalyzer.sentimentAnalyzer(convertedDateDF)
     //debugging
     println("Converted DF Schema : ")
     convertedDateDF.printSchema()

    // Prepare for storing at kafka topic
       val final_df= sentimentDF.withColumn("key",lit(null).cast(StringType))
        .withColumn("value",to_json(struct(sentimentDF.columns.map(col):_*)))
        .select("key","value")



     return final_df


    }
}
