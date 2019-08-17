package csv

import com.github.tototoshi.csv.CSVReader

trait CsvAnalyzer {
  private[this] val reader = CSVReader.open(fileName)
  private[this] val iter = reader.iterator
  iter.next() // drop header

  def fileName: String
  def execLine(line: Seq[String]): Unit

  def run() = {
    iter.foreach(execLine)
  }
}
