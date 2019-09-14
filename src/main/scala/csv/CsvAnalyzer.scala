package csv

import com.github.tototoshi.csv.CSVReader

trait CsvAnalyzer {
  private[this] lazy val reader = CSVReader.open(fileName, encode)
  private[this] lazy val iter = {
    val it = reader.iterator
    (0 until dropLine).foreach(it.next()) // drop header
    it
  }

  def dropLine: Int
  def fileName: String
  def encode: String = "UTF-8"
  def execLine(line: Seq[String]): Unit

  def run() = {
    iter.foreach(execLine)
  }
}
