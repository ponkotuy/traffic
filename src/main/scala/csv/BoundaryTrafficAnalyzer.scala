package csv

class BoundaryTrafficAnalyzer extends CsvAnalyzer {
  private[this] var boundaries: List[Boundary] = Nil
  private[this] var endType6s: List[CensusRecord] = Nil

  override def fileName: String = "kasyo01.csv"

  override def execLine(line: Seq[String]): Unit = {
    CensusRecord.fromLine(line).foreach { census =>
      if(census.start.typ == BoundaryType.CityBoundary) {
        endType6s.find(_.end.name == census.start.name).foreach { end =>
          census.traffic.orElse(end.traffic).foreach { traffic =>
            val low = math.min(census.cityCode, end.cityCode)
            val high = math.max(census.cityCode, end.cityCode)
            boundaries :+= Boundary(low, high, traffic)
          }
          endType6s = endType6s.filterNot(_.id == end.id)
        }
      }
      if(census.end.typ == BoundaryType.CityBoundary) {
        endType6s :+= census
      }
    }
  }

  def getResult: List[Boundary] = boundaries
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
