
import com.github.tototoshi.csv._
import csv.{Boundary, BoundaryTrafficAnalyzer}
import geojson.MunicipalBoundaries

import scala.collection.mutable

object TrafficAnalyzer {
  implicit object MyFormat extends DefaultCSVFormat {
    override val quoting: Quoting = QUOTE_MINIMAL
  }

  def main(args: Array[String]): Unit = {
    val regions = new MaxTrafficRegion(new Boundaries(analyzeBoundaryTraffic())).regions
    val cities = new MunicipalBoundaries
    regions.foreach { case (city, cityGroup) =>
      cities.addCityProperty(city, "city_group", cityGroup)
    }
    cities.save("region.geojson")
  }

  def analyzeBoundaryTraffic(): List[Boundary] = {
    val analyzer = new BoundaryTrafficAnalyzer
    analyzer.run()
    analyzer.getResult
  }

  def calcCityTraffic(): Unit = {
    val cityTraffic = mutable.Map[Int, Int]()

    {
      val result = analyzeBoundaryTraffic()
      result.foreach{ boundary =>
        cityTraffic.updateWith(boundary.startCity){ value => Some(value.getOrElse(0) + boundary.traffic) }
        cityTraffic.updateWith(boundary.endCity){ value => Some(value.getOrElse(0) + boundary.traffic) }
      }
    }

    val cities = new MunicipalBoundaries
    cityTraffic.foreach { case (id, traffic) =>
      cities.addCityProperty(id, "traffic", traffic)
    }
    cities.save("traffic.geojson")
  }
}
