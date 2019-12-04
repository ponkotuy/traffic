package csv

case class CensusRecord(id: Long, line: Line, start: Point, end: Point, cityCode: Int, traffic: Option[Int])

object CensusRecord {
  val Id = 0
  val StartConnectType = 6
  val StartConnectId = 7
  val StartConnectName = 9
  val EndConnectType = 11
  val EndConnectId = 12
  val EndConnectName = 14
  val CityCode = 18
  val Traffic = 41

  def fromLine(xs: Seq[String]): Option[CensusRecord] = for {
    id <- xs.lift(Id)
    line <- Line.fromLine(xs)
    startType <- xs.lift(StartConnectType)
    startId = xs.lift(StartConnectId)
    startName <- xs.lift(StartConnectName)
    endType <- xs.lift(EndConnectType)
    endId = xs.lift(EndConnectId)
    endName <- xs.lift(EndConnectName)
    cityCode <- xs.lift(CityCode)
    traffic <- xs.lift(Traffic)
  } yield {
    CensusRecord(
      id.toLong,
      line,
      Point(startType.toInt, startId.flatMap(_.toLongOption), startName),
      Point(endType.toInt, endId.flatMap(_.toLongOption), endName),
      cityCode.toInt,
      traffic.toIntOption
    )
  }
}

case class Point(typ: Int, id: Option[Long], name: String)
