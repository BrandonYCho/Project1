package db

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}

class SparkConnection {
  // Create the Spark session so we can connect to Hive, similar to MySQL
  def sparkConnect(): SparkSession = {
    // Logger lines are for getting rid of unnecessary console output
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)
    Logger.getLogger("org").setLevel(Level.ERROR)
    System.setProperty("hadoop.home.dir", "C:\\hadoop2")
    // code from demo example week 4 demo3
    val spark = SparkSession
      .builder
      .appName("Project1")
      .config("spark.master", "local[*]")
      .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    spark
  }

  // Close the connection
  def sparkDisconnect(spark: SparkSession): Unit = {
    spark.close()
  }

  // DML - INSERT, UPDATE, DELETE
  def sparkDML(spark: SparkSession, sql: String): Unit = {
    spark.sql(sql).queryExecution
  }

  // DQL - SELECT
  def sparkQuery(spark: SparkSession, sql: String): DataFrame = {
    spark.sql(sql)
  }

  // Spark/PySpark DataFrame show() is used to display the contents of the DataFrame in a Table Row & Column Format.
  // By default it shows only 20 Rows and the column values are truncated at 20 characters.
  // So we set it to not truncate and show the max number of rows
  def sparkShowQuery(spark: SparkSession, sql: String): Unit = {
    spark.sql(sql).show(Integer.MAX_VALUE, false)
  }
}