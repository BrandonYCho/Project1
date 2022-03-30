package db

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.language.reflectiveCalls

object ReadCSV {
  def readCSV(filePath: String): Array[Array[String]] = {
    import Control.using
    val bufferedSource = Source.fromFile(filePath)

    using(bufferedSource) { source => {
      val data = source.getLines().map(_.split("\n")).toArray // turns data into an Array(size = # tracks+1) of Arrays(size 1, the full track metadata)
      val dataArray:ArrayBuffer[Array[String]] = ArrayBuffer[Array[String]]()
      val tempArray:ArrayBuffer[String] = ArrayBuffer[String]()

      // We split tracks, not we want to split the individual information for each track and set it in it's own array
      for(i <- 1 until data.length){
        data(i)(0).split("\",\"").foreach(x => tempArray += x.replaceAll("^\"|\"$", "").trim )   // fills temporary with split track information
        dataArray += tempArray.toArray                                                                               // fills ArrayBuffer with Array holding single track
        tempArray.clear()                                                                                           // clear tempArray so I can fill it with the next track
      }
      // Array of array, each array consists of the split track information
      return dataArray.toArray
    }}
  }
}

object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}
