package es.us.idea

import es.us.idea.cop.definitions.ModelDefinitions
import es.us.idea.cop.{ClassCompiler, ModelBuilder}
import es.us.idea.dao.COPModel
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import dsl.implicits._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

object TestDSL {

  val usage = """
    Usage: java -cp fabiola-0.6.0-BETA-WG.0.2-jar-with-dependencies.jar es.us.idea.TestDSL --input-file file.json --model-input-variables i1,i2,...,in --model-output-variables o1,o2,...,om [--output-folder path]
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
        case "--input-file" :: value :: tail =>
                               nextOption(map ++ Map('input_file -> value), tail)
        case "--model-input-variables" :: value :: tail =>
                               nextOption(map ++ Map('model_input_variables -> value), tail)
        case "--model-output-variables" :: value :: tail =>
                               nextOption(map ++ Map('model_output_variables -> value), tail)
        case "--output-folder" :: value :: tail =>
                               nextOption(map ++ Map('output_folder -> value), tail)
        case option :: tail => println("Unknown option " + option) 
                               println(usage)
                               sys.exit(1) 
      }
    }

    val options = nextOption(Map(),arglist)

    val required = Array('input_file, 'model_input_variables, 'model_output_variables)
    for( param <- required ){
      if (! options.contains(param)) {
        println(param + " parameter is required")
        println(usage)
        sys.exit(1)
      }
    }

    val input_file : String = options.get('input_file).get
    val model_input_variables : Array[String] = options.get('model_input_variables).get.split(",")
    val model_output_variables : Array[String] = options.get('model_output_variables).get.split(",")
    var output_folder : String = if (options.contains('output_folder)) options.get('output_folder).get else "output"

    //val copModel = COPModel("name", "definition")
    val copModel = ModelDefinitions.conquenseDef

    var spark = SparkSession
      .builder()
      .master("local[*]")
      .getOrCreate()

    val df = spark.read.json(input_file)
    df.show()
    df.printSchema()

    val copDf = df.cop
      .string(copModel.model)
      .in(model_input_variables: _*)
      .out(model_output_variables: _*)
      .execute()

    copDf.show()
    copDf.printSchema()

    val jsonDs = copDf.toJSON
    val count = jsonDs.count()
    jsonDs
      .coalesce(1)
      .rdd
      .zipWithIndex()
      .map { case(json, idx) =>
          if(idx == 0) "[\n" + json + ","
          else if(idx == count-1) json + "\n]"
          else json + ","
      }
      .saveAsTextFile(output_folder)
    
    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val srcPath=new Path(output_folder + "/part-00000")
    val destPath= new Path(output_folder + "/part-00000.json")

    if(fs.exists(srcPath) && fs.isFile(srcPath))
     fs.rename(srcPath,destPath)

  }

}
