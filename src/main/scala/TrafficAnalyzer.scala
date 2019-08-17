
import com.github.tototoshi.csv._
import csv.{Boundary, BoundaryTrafficAnalyzer}
import geojson.MunicipalBoundaries

import scala.collection.mutable

object TrafficAnalyzer {
  implicit object MyFormat extends DefaultCSVFormat {
    override val quoting: Quoting = QUOTE_MINIMAL
  }

  def main(args: Array[String]): Unit = {
    val cityTraffic = mutable.Map[String, Int]()

    {
      val result = analyzeBoundaryTraffic()
      result.foreach{ boundary =>
        cityTraffic.updateWith(digit5(boundary.startCity)){ value => Some(value.getOrElse(0) + boundary.traffic) }
        cityTraffic.updateWith(digit5(boundary.endCity)){ value => Some(value.getOrElse(0) + boundary.traffic) }
      }
    }

    val cities = new MunicipalBoundaries
    cityTraffic.foreach { case (id, traffic) =>
      cities.addCityProperty(id, "traffic", traffic)
    }
    cities.save("traffic.geojson")
  }

  def analyzeBoundaryTraffic(): List[Boundary] = {
    val analyzer = new BoundaryTrafficAnalyzer
    analyzer.run()
    analyzer.getResult
  }

  private def digit5(int: Int) = f"$int%05d"
}
