import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp._
import org.apache.spark.ml._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender
object Main {
  def main(args: Array[String]): Unit = {
    val nullAppender = new NullAppender
    BasicConfigurator.configure(nullAppender)
    println("Hello Spark App ! ")


  }
}
