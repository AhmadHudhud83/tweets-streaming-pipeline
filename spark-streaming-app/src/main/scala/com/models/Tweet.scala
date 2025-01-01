  package com.models
  import org.apache.spark.sql.types._

  object Tweet{
    def TweetSchema():StructType={
      val tweetsSchema = StructType(Array(
        StructField("id",StringType,nullable = false),
        StructField("text",StringType,nullable=true),
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

      return tweetsSchema
    }
  }