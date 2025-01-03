  package com.models
  import org.apache.spark.sql.types._

  object TweetSchema{
    def RawTweetSchema():StructType={
      val rawTweetSchema = StructType(Array(
        StructField("id",StringType,nullable = false),
        StructField("text",StringType,nullable=false),
        StructField("created_at",StringType,nullable=true),

        StructField("entities",StructType(Array(
          StructField("hashtags",ArrayType(StructType(Array(
            StructField("text",StringType,nullable = true),
            StructField("indices",ArrayType(IntegerType),nullable = true),
          ))),nullable = true)
        )),nullable = true) ,


          StructField("geo",StructType(Array(
            StructField("type",StringType,nullable = true),
            StructField("coordinates",ArrayType(DoubleType),nullable=true)
          )

          )),




      ))

      return rawTweetSchema
    }

    def ProcessedTweetSchema(): StructType = {
      val processedTweetSchema = StructType(Array(
        StructField("id", StringType, nullable = false),
        StructField("text", StringType, nullable = false),
        StructField("timestamp", TimestampType, nullable = true),
        StructField("geo", StructType(Array(
          StructField("type", StringType, nullable = true),
          StructField("coordinates", ArrayType(DoubleType), nullable = true)
        ))),
        StructField("hashtags", ArrayType(StringType), nullable = true),
        StructField("sentimentScore",IntegerType,nullable=true)
      ))
  return processedTweetSchema
    }
  }