package processing

import com.models.TweetSchema
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.StructType

object BasicProcessing {
def basicProcessing(df:DataFrame,tweetSchema:StructType): DataFrame = {
// Deserialize Binary Keys / Values
  val kafka_df_casted = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

  //Convert To Json

  val streaming_df = kafka_df_casted.withColumn("values_json",from_json(col("value"),tweetSchema)).selectExpr("values_json.*")

  // Streaming df schema for debugging
  println("StreamingDF Schema : ")
  streaming_df.printSchema()
  return streaming_df
}
}
