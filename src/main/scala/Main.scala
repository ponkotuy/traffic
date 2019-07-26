
import java.io.File

import com.github.tototoshi.csv._

object Main {
  val CsvFile = new File("kasyo01.csv")

  implicit object MyFormat extends DefaultCSVFormat {
    override val quoting: Quoting = QUOTE_MINIMAL
  }

  def main(args: Array[String]): Unit = {
    val reader = CSVReader.open(CsvFile)
    val it = reader.iterator
    it.next() // drop header
    var boundaries: List[Boundary] = Nil
    var endType6s: List[CensusRecord] = Nil
    it.flatMap(CensusRecord.fromLine).foreach { census =>
      if(census.start.typ == BoundaryType.CityBoundary) {
        endType6s.find(_.end.name == census.start.name).foreach { end =>
          census.traffic.orElse(end.traffic).foreach { traffic =>
            boundaries :+= Boundary(census.cityCode, end.cityCode, traffic)
          }
          endType6s = endType6s.filterNot(_.id == end.id)
        }
      }
      if(census.end.typ == BoundaryType.CityBoundary) {
        endType6s :+= census
      }
      if(census.line.name == "美深中川線") println(census)
    }
    println(boundaries)
    println(endType6s)
  }
}

object BoundaryType {
  val CityBoundary = 6
}

case class CensusRecord(id: Long, line: Line, start: Point, end: Point, cityCode: Int, traffic: Option[Int])

object CensusRecord {
  val Id = 0
  val StartConnectType = 6
  val StartConnectName = 9
  val EndConnectType = 11
  val EndConnectName = 14
  val CityCode = 18
  val Traffic = 41

  def fromLine(xs: Seq[String]): Option[CensusRecord] = for {
    id <- xs.lift(Id)
    line <- Line.fromLine(xs)
    startType <- xs.lift(StartConnectType)
    startName <- xs.lift(StartConnectName)
    endType <- xs.lift(EndConnectType)
    endName <- xs.lift(EndConnectName)
    cityCode <- xs.lift(CityCode)
    traffic <- xs.lift(Traffic)
  } yield {
    CensusRecord(
      id.toLong,
      line,
      Point(startType.toInt, startName),
      Point(endType.toInt, endName),
      cityCode.toInt,
      traffic.toIntOption
    )
  }
}

case class Line(number: Int, name: String, period: BigDecimal)

object Line {
  val LineNumber = 4
  val LineName = 5
  val Period = 23

  def fromLine(xs: Seq[String]): Option[Line] = for {
    number <- xs.lift(LineNumber)
    name <- xs.lift(LineName)
    period <- xs.lift(Period)
  } yield Line(number.toInt, name, BigDecimal(period))
}

case class Point(typ: Int, name: String)

case class Boundary(startCity: Int, endCity: Int, traffic: Int)
