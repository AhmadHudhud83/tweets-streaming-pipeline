# Spark Streaming Pipeline App

This program aims to simulate the process of processing data streams and storing them in a MongoDB Database in NoSQL Format using Apache Spark Structured Stream integration with Apache Kafka , in order to consume them for a tweets real-time tracker application.

![Streaming Pipeline](images\about-project-stream-app.jpg"
## Description

- **The first stage of the program begins by reading the raw data from a JSON dataset file. The data is processed in microbatches, with each microbatch containing no more than 10 records. To simulate flow, an artificial delay of five seconds is introduced between processing each microbatch. The processed microbatches are then published to a Kafka topic designated for storing raw streamed tweets.**
  <br><br><br>
  ![Flow Simulation](images\raw-data-producer-simualtion.jpg)
  <br><br><br>
- **The second stage, the program's core, consumes raw data from Kafka, extracts attributes, builds the required schema, and processes tweets via a microservice for rule-based sentiment analysis. The processed data is converted into key-value format, categorized into tweets with or without geographic data, and stored in a Kafka topic for the processed load.**
<br><br><br>
![Spark Structured Streaming Process](images\spark-stream-app.jpg)
<br><br><br>
![Processed Tweets Kafka Topic](images\processed-tweets-topic.jpg)
<br><br><br>

- **The third stage involves a consumer program for the processed data, which creates attribute indexes to optimize search and query operations. The streaming data is then stored in a MongoDB database for use in the tweet tracking application.**


<br><br><br>
![MongoDB Writer Consumer](images\mongo-writer-consumer.jpg)
<br><br><br>
## Getting Started

### Dependencies

- **Apache Spark Core**  
  Library: `spark-core`  
  Version: `3.4.0`

- **Apache Spark SQL**  
  Library: `spark-sql`  
  Version: `3.4.0`

- **Apache Spark MLlib**  
  Library: `spark-mllib`  
  Version: `3.4.0`

- **Apache Spark SQL Kafka Integration**  
  Library: `spark-sql-kafka-0-10`  
  Version: `3.4.0`

- **Apache Spark Streaming Kafka Integration**  
  Library: `spark-streaming-kafka-0-10`  
  Version: `3.4.0`

- **John Snow Labs Spark NLP**  
  Library: `spark-nlp`  
  Version: `5.4.0`

- **MongoDB Spark Connector**  
  Library: `mongo-spark-connector`  
  Version: `10.3.0`
- **JDK 11+ or 8+**
- **Scala 2.12.15**
- **Docker Desktop (Latest Version)**

### Installing

- Clone the project into your preferd directory

```
git clone  https://github.com/AhmadHudhud83/tweets-streaming-pipeline.git
```

### Executing the Program

**How to Run the Program**

1. **Set Up Kafka & Zookeeper Environment:**

   - Use the provided `docker-compose.yml` file to set up the Kafka and Zookeeper environment.
   - Open a terminal, navigate to the directory where the file is located, and run the following command:
     ```bash
     docker-compose up -d
     ```
   - You should see the Kafka container running in Docker Desktop.
   - Open a new terminal and access the Kafka container bash shell:
     ```bash
     docker exec -it <container_id> /bin/bash
     ```
     Replace `<container_id>` with your actual Kafka container ID ("6dda6c66149a" in this case).

2. **Create Kafka Topics:**

   - Inside the Kafka container, create the topics for raw and processed tweets using the following command:
     ```bash
     kafka-topics --create --topic <topic-name> --partitions 2 --replication-factor 2 --bootstrap-server localhost:9092
     ```
     Create both `raw-tweets-topic` and `processed-tweets-topic` topics.

3. **Run the Raw Data Producer:**

   - Navigate to the `RawDataProducer` Scala program, and execute it. It will read from the tweets dataset file and send batches of data (you'll see output in the terminal).
   - Open another terminal window and use the Kafka console consumer to trace the stream:
     ```bash
     kafka-console-consumer --bootstrap-server localhost:9092 --topic raw-tweets-topic --from-beginning
     ```

4. **Run the Spark Stream Processing Program:**

   - Execute the `Main.scala` (Main Spark Stream Processing Program).
   - Open the Kafka console consumer for `processed-tweets-topic` to trace the processed data stream.

5. **Run the MongoDB Writer Program:**
   - Execute the `MongoDBWriter.scala` program to save the processed tweets into the MongoDB `tweets` collection.
   - You can modify the MongoDB configuration within the program if you wish to use your own MongoDB setup.

---

### Help

- **When Opening the Project** Ensure opening "spark-streaming-pipeline" wtih Intelij for proper build.sbt building if there was building problems.
- **Verify the Path:** Ensure the path to the `tweets.json` dataset is correct before running the producer.
- **Execution Order:** Make sure to run the programs in the provided order.

## Authors

Contributors names and contact info

- [@Ahmad Hudhud](https://example.com/dompizzie)
- [@Momen Raddad](https://example.com/dompizzie)
- [@Bayan Abdel-Haq](https://example.com/dompizzie)

## Acknowledgments & Sources

- [Apache Kafka Integration with Spark Stream](https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html)
- [Spark Streaming Demo](https://github.com/subhamkharwal/spark-streaming-with-pyspark)
- [MongoDB with Spark Documentation](https://www.mongodb.com/docs/spark-connector/v10.0/streaming-mode/streaming-write/)
- [Spark NLP Rule-Based Sentiment Analysis](https://www.johnsnowlabs.com/sentiment-analysis-with-spark-nlp-without-machine-learning/)
- [Bing Dictionary for Sentiment Analysis Model](https://www.kaggle.com/datasets/andradaolteanu/bing-nrc-afinn-lexicons)
