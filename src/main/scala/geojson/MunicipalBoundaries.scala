package geojson

import scala.collection.mutable

object MunicipalBoundaries {
  val FName = "N03-19_01_190101.geojson"
  val Id = "N03_007"
  val City = "N03_004"
}

class MunicipalBoundaries {
  import MunicipalBoundaries._

  type Property = Map[String, Any]

  val geojson = new GeoJson(FName)
  val cities: mutable.Map[String, scala.collection.Seq[FeatureWithUUID]] =
    mutable.Map(geojson.getFeatures.groupBy(_.properties(Id).toString).toSeq:_*)

  def getCity(id: String): scala.collection.Seq[FeatureWithUUID] = cities.getOrElse(id, Nil)

  def getCityProperty(id: String): scala.collection.Seq[Property] = getCity(id).map(_.properties)

  def addCityProperty(id: String, key: String, value: Any): Boolean = cities.updateWith(id) { optCities =>
    optCities.map { cities =>
      val newCities = cities.map { city =>
        val newCity = city.copy(properties = city.properties + (key -> value))
        geojson.updateFeature(newCity)
        newCity
      }
      newCities
    }
  }.isDefined

  def save(fname: String): Unit = {
    geojson.save(fname)
  }
}
