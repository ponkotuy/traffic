
import java.nio.file.{Files, Paths}

import com.github.tototoshi.csv._
import csv.{Boundary, BoundaryTrafficAnalyzer}
import geojson.{MunicipalBoundaries, MunicipalBoundariesSettings}

import scala.collection.mutable
import scala.compat.java8.StreamConverters._

object TrafficAnalyzer {
  implicit object MyFormat extends DefaultCSVFormat {
    override val quoting: Quoting = QUOTE_MINIMAL
  }

  def main(args: Array[String]): Unit = {
    val regions = new MaxTrafficRegion(new Boundaries(analyzeBoundaryTraffic())).regions
    val cities = new MunicipalBoundaries(MunicipalBoundariesSettings.Japan)
    regions.foreach { case (city, cityGroup) =>
      cities.addCityProperty(city, "city_group", cityGroup)
    }
    cities.save("region.geojson")
  }

  def analyzeBoundaryTraffic(): Seq[Boundary] = {
    val dir = Paths.get("census")
    val files = Files.list(dir).toScala[Vector].filter(Files.isRegularFile(_))
    files.flatMap { path =>
      println(path)
      val analyzer = new BoundaryTrafficAnalyzer(path.toFile)
      analyzer.run()
      analyzer.getResult
    }
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

    val cities = new MunicipalBoundaries(MunicipalBoundariesSettings.Japan)
    cityTraffic.foreach { case (id, traffic) =>
      cities.addCityProperty(id, "traffic", traffic)
    }
    cities.save("traffic.geojson")
  }
}
