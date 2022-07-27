package es.us.idea

import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.SparkSession
import es.us.idea.dmn4spark.implicits._

object Main {

  val usage = """
    Usage: java -cp dq-dmn-1.0-SNAPSHOT-jar-with-dependencies.jar es.us.idea.Main --mongo-connection string --input path --model path --delimiter str [--url-spark local --output-folder path]
  """

  def main(args: Array[String]): Unit = {

    if (args.length == 0) {
      println("It is necessary to specify the input parameters")
      println(usage)
      sys.exit(1)
    }
    val arglist = args.toList
    type OptionMap = Map[Symbol, String]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "--mongo-connection" :: value :: tail =>
                               nextOption(map ++ Map('mongo_connection -> value), tail)
        case "--input" :: value :: tail =>
                               nextOption(map ++ Map('input -> value), tail)
        case "--model" :: value :: tail =>
                               nextOption(map ++ Map('model -> value), tail)
        case "--delimiter" :: value :: tail =>
                               nextOption(map ++ Map('delimiter -> value), tail)
        case "--url-spark" :: value :: tail =>
                               nextOption(map ++ Map('url_spark -> value), tail)
        case "--output-folder" :: value :: tail =>
                               nextOption(map ++ Map('output_folder -> value), tail)
        case option :: tail => println("Unknown option " + option) 
                               println(usage)
                               sys.exit(1) 
      }
    }

    val options = nextOption(Map(),arglist)

    val required = Array('mongo_connection, 'input, 'model, 'delimiter)
    for( param <- required ){
      if (! options.contains(param)) {
        println(param + " parameter is required")
        println(usage)
        sys.exit(1)
      }
    }

    val mongo_conection : String = options.get('mongo_connection).get
    val input : String = options.get('input).get
    val model : String = options.get('model).get
    val delimiter : String = options.get('delimiter).get
    var url_spark : String = if (options.contains('url_spark)) options.get('url_spark).get else "local"
    var output_folder : String = if (options.contains('output_folder)) options.get('output_folder).get else "output"

    val spark = SparkSession.builder()
      .master(url_spark)
      .config("spark.mongodb.output.uri", mongo_conection)
      .appName("DMN DQ Sample").getOrCreate()

    val df = spark.read.option("header", "true").option("sep", delimiter).csv(input)
      .dmn.loadFromLocalPath(model)

    MongoSpark.save(df)
    df.write.option("header", "true").option("sep", delimiter).csv(output_folder)
  }

}
