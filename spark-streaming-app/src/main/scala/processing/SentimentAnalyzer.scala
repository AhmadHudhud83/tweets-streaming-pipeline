
package processing
import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp._
import com.johnsnowlabs.nlp.util.io.ReadAs
import org.apache.spark.ml._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender
object SentimentAnalyzer {
  def sentimentAnalyzer(df: DataFrame): DataFrame = {
    // NLP ----------------------------------------------------------
    val sentimentToInt = udf((sentiment: Seq[String]) => sentiment match { case Seq("positive") => 1 case Seq("negative") => -1 case _ => 0 })
    val documentAssembler = new DocumentAssembler().
      setInputCol("text").
      setOutputCol("document")

    val sentenceDetector = new SentenceDetector().
      setInputCols(Array("document"))
      .setOutputCol("sentence")

    val tokenizer = new Tokenizer().
      setInputCols(Array("sentence")).
      setOutputCol("token")

    val LemmaPath = "src/main/scala/processing/resources/lemmas_small.txt"
    val SDPath = "src/main/scala/processing/resources/default-sentiment-dict.txt"

    val lemmatizer = new Lemmatizer().
      setInputCols("token")
      .setOutputCol("lemma")
      .setDictionary(LemmaPath, "->", "\t")


    val sentimentDetector = new SentimentDetector()
      .setInputCols("lemma", "document")
      .setOutputCol("sentimentScore")
      .setDictionary(SDPath, ",", ReadAs.TEXT)

    val pipeline = new Pipeline().setStages(Array(
      documentAssembler,
      sentenceDetector,
      tokenizer,
      lemmatizer,
      sentimentDetector
    ))


    val result = pipeline.fit(df).transform(df)
    val sentimentDF = result.withColumn("sentimentScore", sentimentToInt(col("sentimentScore.result"))).select("id", "text", "timestamp", "geo","hashtags", "sentimentScore")
    sentimentDF.printSchema()
    
    return sentimentDF
  }
}

