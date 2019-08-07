import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object GeoJsonAnalyzer {
  val FName = "N03-19_01_190101.geojson"
  def main(args: Array[String]): Unit = {
    val mapper = new ObjectMapper
    mapper.registerModule(DefaultScalaModule)
    val collection = mapper.readValue(new File(FName), classOf[FeatureCollection])
    println(collection.features.head)
  }
}

case class FeatureCollection(`type`: String, crs: Crs, features: Seq[Feature])
case class Crs(`type`: String, properties: Map[String, String])
case class Feature(`type`: String, properties: Map[String, String], geometry: Geometry)
case class Geometry(`type`: String, coordinates: Seq[Seq[Seq[BigDecimal]]])
