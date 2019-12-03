package csv

import java.io.File

class BoundaryTrafficAnalyzer(override val file: File) extends CsvAnalyzer {
  private[this] var boundaries: List[Boundary] = Nil
  private[this] var endType6s: List[CensusRecord] = Nil

  override val dropLine: Int = 1

  override def execLine(line: Seq[String]): Unit = {
    CensusRecord.fromLine(line).foreach { census =>
      if(census.start.typ == BoundaryType.CityBoundary) {
        endType6s.find { x => x.end.name == census.start.name && x.line.number == census.line.number }.foreach { end =>
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

case class Boundary(startCity: Int, endCity: Int, traffic: Int)
