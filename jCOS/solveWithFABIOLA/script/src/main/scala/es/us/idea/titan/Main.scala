package es.us.idea.titan

import org.apache.spark.sql.SparkSession
import java.nio.file.{FileSystems, Files}
import scala.collection.JavaConverters._

object Main extends App {

  // Use args to pass recipe path and number of samples
  val _args = args.toList

  var recipePath: String = null
  var n_samples: Int = -1

  // If args are received, search for recipe path and number of samples using key=value format. Keywords: "recipe", "n-samples"
  if (_args.length != 0) {
    var i = 0
    while ((recipePath == null || n_samples == -1) && i < _args.length) {
      if (_args(i).startsWith("recipe=")) {
        recipePath = _args(i).substring(7)
      } else if (_args(i).startsWith("n-samples=")) {
        n_samples = _args(i).substring(10).toInt
      }
      i+=1
    }
  }

  // If recipe path is not received, throw exception
  if (recipePath == null) {
    throw new Exception("Recipe path not specified")
  }
  // If number of samples is not received, set it to 5 and print a warning
  if (n_samples == -1) {
    n_samples = 5
    println("WARNING: Number of samples not specified. Using 5 as default")
  }

  val spark = SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()

  val recipe = FABIOLARecipe.fromJson(recipePath)

  val df = spark.read.option("multiline", "true").json(recipe.datasetPath)

  val finalDF = FABIOLARecipe.applyRecipe(
    spark.createDataFrame(df.takeAsList(n_samples), df.schema), recipe
  )

  finalDF.coalesce(1)
    .write
    .json(recipe.outDatasetPath)
}
