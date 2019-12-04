package csv

import java.io.File

import scala.collection.mutable
import BoundaryTrafficAnalyzer.Result

class BoundaryTrafficAnalyzer(override val file: File, init: Result) extends CsvAnalyzer {
  import BoundaryType._
  import BoundaryTrafficAnalyzer._

  private[this] var boundaries: List[Boundary] = init.boundaries
  private[this] val prefFragments: mutable.Map[Long, CensusRecord] = mutable.Map.from(init.fragments)
  private[this] var cityEndFragments: List[CensusRecord] = Nil

  override val dropLine: Int = 1

  override def execLine(line: Seq[String]): Unit = {
    CensusRecord.fromLine(line).foreach { census =>
      if(prefFragments.get(census.id).isDefined) {
        prefFragments.remove(census.id).foreach { other =>
          boundaries ++= genBoundary(census, other)
        }
      } else {
        val points = census.start :: census.end :: Nil
        points.filter(_.typ == PrefBoundary).foreach { point =>
          point.id.foreach { id =>
            prefFragments.put(id, census)
          }
        }
      }
      if(census.start.typ == CityBoundary) {
        cityEndFragments.find { x => x.end.name == census.start.name && x.line.number == census.line.number }.foreach { end =>
          boundaries ++= genBoundary(census, end)
          cityEndFragments = cityEndFragments.filterNot(_.id == end.id)
        }
      }
      if(census.end.typ == CityBoundary) {
        cityEndFragments :+= census
      }
    }
  }

  def getResult: Result = Result(boundaries, prefFragments.toMap)
}

object BoundaryTrafficAnalyzer {
  def getTraffic(x: CensusRecord, y: CensusRecord): Option[Int] =
    x.traffic.orElse(y.traffic)

  def genBoundary(x: CensusRecord, y: CensusRecord): Option[Boundary] = {
    val low = math.min(x.cityCode, y.cityCode)
    val high = math.max(x.cityCode, y.cityCode)
    if(low == high) println(x, y)
    getTraffic(x, y).map(Boundary(low, high, _))
  }

  case class Result(boundaries: List[Boundary], fragments: Map[Long, CensusRecord])

  object Result {
    val Empty: Result = Result(Nil, Map.empty)
  }
}

object BoundaryType {
  val PrefBoundary = 3
  val CityBoundary = 6
  val Boundaries: Seq[Int] = PrefBoundary :: CityBoundary :: Nil
}

case class Boundary(startCity: Int, endCity: Int, traffic: Int)
