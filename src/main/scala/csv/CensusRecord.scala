package csv

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

case class Point(typ: Int, name: String)
