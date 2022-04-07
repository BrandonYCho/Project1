package db

import ReadCSV.readCSV
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.catalyst.analysis.NoSuchDatabaseException
import com.github.t3hnar.bcrypt._
import ui.AdminMaster.state

import java.io.{BufferedReader, File, FileReader, FileWriter}
import java.sql.Timestamp
import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.{readInt, readLine}

object HiveMaster extends SparkConnection {
  // generateSalt
  val salt = "$2a$10$/UdoECyyigsRmTG9q8TOq."

  // Use Database maindb
  def startDB(): Unit = {
    val spark: SparkSession = sparkConnect()
    try {
      val df: DataFrame = sparkQuery(spark, "USE maindb")
    }
    catch {
      case nsdbe: NoSuchDatabaseException => createDB()
    }
  }

  // Check to see if DB Main exists, then creates it if it does not exist
  // Each method adds in necessary tables
  protected def createDB(): Unit = {
    val spark: SparkSession = sparkConnect()
    sparkDML(spark, "CREATE DATABASE IF NOT EXISTS maindb")
    createUserTable("maindb.users")
    createMusicTable("maindb.music")
    //createQueryTable("maindb.queries")
    //add more tables here

    // Insert default user admin
    sparkDML(spark, s"insert into maindb.users values (${getNextUserId()}, 'admin', '${"admin".bcryptBounded(salt)}', true)")
    insertMusicData(readCSV("musicdata.csv"))
  }

  // Insert user table for DB, ORC = Optimized Row Columnar (default storage for Hive data)
  protected def createUserTable(tableName: String): Unit = {
    sparkDML(sparkConnect(),
      s"""CREATE TABLE IF NOT EXISTS
         |$tableName (
         |userID Int,
         |username String,
         |password String,
         |isAdmin Boolean
         |) STORED AS orc
         |""".stripMargin
    )
  }

  protected def createMusicTable(tableName: String): Unit = {
    sparkDML(sparkConnect(),
      s"""CREATE TABLE IF NOT EXISTS
         |$tableName (
         |TrackID String,
         |TrackName String,
         |ArtistID String,
         |ArtistName String,
         |AlbumID String,
         |AlbumName String,
         |AlbumArtistID String,
         |AlbumArtistName String,
         |DiskNumber Int,
         |TrackNumber Int,
         |TrackDuration BigInt,
         |isExplicit Boolean,
         |Popularity Int,
         |ArtistGenre String,
         |Danceability Float,
         |Energy Float,
         |Key Int,
         |Loudness Float,
         |Speechiness Float,
         |Acousticness Float,
         |Instrumentalness Float,
         |Liveness Float,
         |Valence Float,
         |Tempo Float,
         |TimeSignature Int
         |)
         |STORED AS orc
         |""".stripMargin

      /*partitioned by (Key Int)  Didn't work
      clustered by (Popularity)  Found duplicate column(s) in the table definition of `maindb`.`music`: `key`
      sorted by (TrackName)
      into 50 buckets*/
    )
  }

  protected def createQueryTable(tableName: String): Unit = {
    sparkDML(sparkConnect(),
      s"""CREATE TABLE IF NOT EXISTS
         |$tableName (
         |queryID Int,
         |userID Int,
         |queryName String,
         |query String
         |) STORED AS orc
         |""".stripMargin
    )
  }


  // getters and setters
  def authenticate(username: String, password: String, isAdmin: Boolean):  Int = {
    var result: Int = 0
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, s"SELECT userID FROM maindb.users WHERE username = '$username' AND password = '${password.bcryptBounded(salt)}' AND isAdmin = $isAdmin")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        result = df.take(1)(0).getInt(0)

    result
  }

  def getNextUserId(): Int = {
    var result: Int = 1
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, "SELECT max(userID) FROM maindb.users")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        result = df.take(1)(0).getInt(0) + 1

    result
  }

  def getUsers(): List[(Int, String, String, Boolean)] = {
    var result: ArrayBuffer[(Int, String, String, Boolean)] = ArrayBuffer()
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, "select userID, username, password, isAdmin from maindb.users order by userID")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        for (r : Row <- df.collect()) {
          val tuple : (Int, String, String, Boolean) = (r.getInt(0), r.getString(1), r.getString(2), r.getBoolean(3))
          result += tuple
        }

    result.toList
  }
  def addUser(username: String, password: String): Int = {
    val result: Int = getNextUserId()
    sparkDML(sparkConnect(), s"INSERT INTO maindb.users VALUES ($result, '$username', '${password.bcryptBounded(salt)}', false)")
    result
  }

  def updateUsername(userID: Int, newUsername: String): Boolean = {
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, s"SELECT password, isAdmin FROM maindb.users WHERE userID = $userID")
    val passAdmin: (String, Boolean) = (df.take(1)(0).getString(0), df.take(1)(0).getBoolean(1))
    createUserTable("maindb.usersTemp")
    sparkDML(spark, s"insert into maindb.usersTemp select * from maindb.users where userID != $userID")
    sparkDML(spark, "drop table maindb.users")
    sparkDML(spark, "alter table maindb.usersTemp rename to users")
    sparkDML(spark, s"insert into table maindb.users values ($userID, '$newUsername', '${passAdmin._1}', ${passAdmin._2})")
    true
  }

  def updatePassword(userID: Int, oldPassword: String, newPassword: String): Boolean = {
    val spark: SparkSession = sparkConnect()
    var df: DataFrame = sparkQuery(spark, s"select * from maindb.users where userID = $userID and password = '${oldPassword.bcryptBounded(salt)}'")
    if (df.isEmpty)
      false
    else {
      df = sparkQuery(spark, s"select username, isAdmin from maindb.users where userID = $userID")
      val userAdmin: (String, Boolean) = (df.take(1)(0).getString(0), df.take(1)(0).getBoolean(1))
      createUserTable("maindb.usersTemp")
      sparkDML(spark, s"insert into maindb.usersTemp select * from maindb.users where userID != $userID")
      sparkDML(spark, "drop table maindb.users")
      sparkDML(spark, "alter table maindb.usersTemp rename to users")
      sparkDML(spark, s"insert into table maindb.users values ($userID, '${userAdmin._1}', '${newPassword.bcryptBounded(salt)}', ${userAdmin._2})")
      true
    }
  }

  def usernameExists(username: String): Boolean = {
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, s"select username from maindb.users where username = '$username'")
    !df.isEmpty
  }

  def isAdmin(userID: Int): Boolean = {
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, s"select isAdmin from maindb.users where userID = $userID")
    df.take(1)(0).getBoolean(0)
  }

  def setToAdmin(userID: Int): Unit = {
    val spark: SparkSession = sparkConnect()
    var df: DataFrame = sparkQuery(spark, s"select username, password from maindb.users where userID = $userID")
    val userPass : (String, String) = (df.take(1)(0).getString(0), df.take(1)(0).getString(1))
    createUserTable("maindb.usersTemp")
    sparkDML(spark, s"insert into maindb.usersTemp select * from maindb.users where userID != $userID")
    sparkDML(spark, "drop table maindb.users")
    sparkDML(spark, "alter table maindb.usersTemp rename to users")
    sparkDML(spark, s"insert into table maindb.users values ($userID, '${userPass._1}', '${userPass._2}', true)")
  }

  def deleteUser(userID: Int): Unit = {
    val spark: SparkSession = sparkConnect()
    createUserTable("maindb.usersTemp")
    sparkDML(spark, s"insert into maindb.usersTemp select * from maindb.users where userID != $userID")
    sparkDML(spark, "drop table maindb.users")
    sparkDML(spark, "alter table maindb.usersTemp rename to users")
  }

  def getNextQueryId(): Int = {
    var result: Int = 1
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, "select max(queryID) from maindb.queries")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        result = df.take(1)(0).getInt(0) + 1

    result
  }

  def executeQuery1(): Unit = {
    // Average popularity of a song
    val spark: SparkSession = sparkConnect()
    println("\nFinding the average rating per song")

    val query = "select sum(popularity)/count(popularity) as AVG_Popularity_Rating from music"
    sparkShowQuery(spark, query)
    readLine()
  }

  def executeQuery2(): Unit = {
    val spark: SparkSession = sparkConnect()
    println("\nFinding your most popular songs")

    val query = "SELECT TrackName, ArtistName, Popularity  FROM music WHERE (popularity > 60) ORDER BY popularity"
    sparkShowQuery(spark, query)
    readLine()
  }

  def executeQuery3(): Unit = {
    val spark: SparkSession = sparkConnect()
    println("\nTotal songs by key")

    val query = "SELECT key, count(key) as Number_of_Songs From Music GROUP BY key ORDER BY key"
    sparkShowQuery(spark, query)
    readLine()
  }

  def executeQuery4(): Unit = {
    val spark: SparkSession = sparkConnect()
    println("\nHow does Loudness and Energy impact Danceability")

    val query = "SELECT TrackName, Danceability, ABS(Loudness/Energy) as Energy, Tempo, isExplicit FROM Music"
    sparkShowQuery(spark, query)
    readLine()
  }

  def executeQuery5(): Unit = {
    val spark: SparkSession = sparkConnect()
    println("\nAverage Artist Tempo")

    val query = "SELECT ArtistName, SUM(Tempo)/COUNT(Tempo) as AVG_Tempo FROM Music GROUP BY ArtistName ORDER BY ArtistName"
    sparkShowQuery(spark, query)
    readLine()
  }

  def executeQuery6(): Unit = {
    val spark: SparkSession = sparkConnect()
    println("\nTrack Duration/Liveness")

    val query = "SELECT TrackName, ROUND(TrackDuration/60, 0) as Duration_Seconds, ABS(Loudness), Liveness  FROM Music"
    sparkShowQuery(spark, query)
    readLine()
  }

  def getQuery(queryID: Int): String = {
    val df: DataFrame = sparkQuery(sparkConnect(), s"select query from maindb.queries where queryID = $queryID")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        return df.take(1)(0).getString(0)
    ""
  }

  def getQueries(): Map[Int, String] = {
    var result: Map[Int, String] = Map()
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, "select queryID, queryName from maindb.queries order by queryID")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0))
        for (row : Row <- df.collect())
          result += (row.getInt(0) -> row.getString(1))
    result
  }

  def queryExists(queryID: Int): Boolean = {
    val queries: Map[Int, String] = getQueries()
    if (queries.isEmpty)
      false
    else
      queries.keys.exists(q => q.equals(queryID))
  }

  def queryNameExists(queryName: String): Boolean = {
    val queries: Map[Int, String] = getQueries()
    if (queries.isEmpty)
      false
    else
      queries.values.exists(q => q.equals(queryName))
  }

  def showQuery(queryID: Int): Unit = {
    val spark: SparkSession = sparkConnect()
    val df: DataFrame = sparkQuery(spark, s"select query from maindb.queries where queryID = $queryID")
    if (!df.isEmpty)
      if (!df.take(1)(0).isNullAt(0)) {
        sparkShowQuery(spark, df.take(1)(0).getString(0))
      }
  }

  def showQuery(query: String): Unit = {
    sparkShowQuery(sparkConnect(), query)
  }

  def renameQuery(queryID: Int, newName: String): Unit = {
    if (!queryNameExists(newName)) {
      val spark: SparkSession = sparkConnect()
      var query: (Int, String) = null
      val df: DataFrame = sparkQuery(spark, s"select userID, query from maindb.queries where queryID = $queryID")
      if (!df.isEmpty)
        if (!df.take(1)(0).isNullAt(0))
          query = (
            df.take(1)(0).getInt(0),
            df.take(1)(0).getString(1)
          )
      if (query != null) {
        createQueryTable("maindb.queriesTemp")
        sparkDML(spark, s"insert into maindb.queriesTemp select * from maindb.queries where queryID != $queryID")
        sparkDML(spark, "drop table maindb.queries")
        sparkDML(spark, "alter table maindb.queriesTemp rename to queries")
        sparkDML(spark, s"insert into maindb.queries values ($queryID, ${query._1}, '$newName', '${query._2}')")
      }
    }
  }

  def saveQuery(userID: Int, queryName: String, query: String): Unit = {
    sparkDML(sparkConnect(), s"insert into maindb.queries values (${getNextQueryId()}, $userID, '$queryName', '$query')")
  }

  def exportQueryResults(queryID: Int, filePath: String): Unit = {
    val df: DataFrame = sparkQuery(sparkConnect(), getQuery(queryID))
    df.write.option("header", "true").json("temp/")
    val dir: File = new File("temp/")
    for (file: File <- dir.listFiles().filter(f => f.getName.endsWith(".json")).sorted) {
      val writer: FileWriter = new FileWriter(new File(filePath), true)
      val reader: BufferedReader = new BufferedReader(new FileReader(file))
      reader.lines().forEachOrdered(line => writer.write(line + System.lineSeparator()))
      writer.close()
      reader.close()
    }
    dir.listFiles().foreach(f => f.delete())
    dir.delete()
  }

  def deleteQuery(queryID: Int): Unit = {
    val spark: SparkSession = sparkConnect()
    createQueryTable("maindb.queriesTemp")
    sparkDML(spark, s"insert into maindb.queriesTemp select * from maindb.queries where queryID != $queryID")
    sparkDML(spark, "drop table maindb.queries")
    sparkDML(spark, "alter table maindb.queriesTemp rename to queries")
  }

  def insertMusicData(array:Array[Array[String]]): Unit = {
    val spark: SparkSession = sparkConnect()
    try {
      if (array.length > 0){
        for (i <- 0 until array.length){
          var trackID: String = array(i)(0).replaceAll("spotify:track:", "").trim
          var trackName: String = array(i)(1).trim
          var artistID: String = array(i)(2).replaceAll("spotify:artist:", "").trim
          var artistName: String = array(i)(3).trim
          var albumID: String = array(i)(4).replaceAll("spotify:album:", "").trim
          var albumName: String = array(i)(5).trim
          var albumArtistID: String = array(i)(6).replaceAll("spotify:artist:", "").trim
          var albumArtistName: String = array(i)(7).trim
          //var albumReleaseDate: LocalDate = fixDate(array(i)(8).trim) ArrayIndexOutOfBoundsException: 2
          var diskNumber: Int= array(i)(10).toInt
          var trackNumber: Int = array(i)(11).toInt
          var trackDuration: BigInt = array(i)(12).toInt
          var isExplicit: Boolean = array(i)(14).toLowerCase.toBoolean
          var popularity: Int = array(i)(15).toInt
          var artistGenre: String = array(i)(18).trim
          var danceability: Float = array(i)(19).toFloat
          var energy: Float = array(i)(20).toFloat
          var key: Int = array(i)(21).toInt
          var loudness: Float = array(i)(22).toFloat
          var speechiness: Float = array(i)(24).toFloat
          var acousticness: Float = array(i)(25).toFloat
          var instrumentalness: Float = array(i)(26).toFloat
          var liveness: Float = array(i)(27).toFloat
          var valence: Float = array(i)(28).toFloat
          var tempo: Float = array(i)(29).toFloat
          var timeSignature: Int = array(i)(30).toInt

          val insertMusicData: String =
            s"""
               |INSERT INTO maindb.music values (
               |'$trackID', '$trackName',
               |'$artistID', '$artistName',
               |'$albumID', '$albumName', '$albumArtistID', '$albumArtistName',
               |$diskNumber, $trackNumber, $trackDuration,
               |$isExplicit, $popularity, '$artistGenre',
               |$danceability, $energy, $key, $loudness,
               |$speechiness, $acousticness, $instrumentalness,
               |$liveness, $valence, $tempo, $timeSignature
               |)
               |""".stripMargin

          sparkDML(spark, insertMusicData)
        }
      }
    }
  }

  def fixDate(string: String): LocalDate = {
    val temp = string.split("/")
    val fixedDate:String = temp(2)+"-"+temp(0)+"-"+temp(1)
    val formatDate = LocalDate.parse(fixedDate)
    formatDate
  }
}
