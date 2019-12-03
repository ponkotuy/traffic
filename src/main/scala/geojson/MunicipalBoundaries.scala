package geojson

import scala.collection.mutable

case class MunicipalBoundariesSettings(
    fname: String,
    idColumn: String,
    cityColumn: String
)

object MunicipalBoundariesSettings {
  val Hokkaido: MunicipalBoundariesSettings = MunicipalBoundariesSettings(
    "N03-19_01_190101.geojson",
    "N03_007",
    "N03_004"
  )

  val Japan: MunicipalBoundariesSettings = MunicipalBoundariesSettings(
    "geojson/japan.geojson",
    "JCODE",
    "SIKUCHOSON"
  )
}

class MunicipalBoundaries(settings: MunicipalBoundariesSettings) {
  type Property = Map[String, Any]

  val geojson = new GeoJson(settings.fname)
  val cities: mutable.Map[String, scala.collection.Seq[FeatureWithUUID]] =
    mutable.Map(geojson.getFeatures.groupBy(_.properties(settings.idColumn).toString).toSeq:_*)

  def getCity(id: Int): scala.collection.Seq[FeatureWithUUID] = cities.getOrElse(digit5(id), Nil)

  def getCityProperty(id: Int): scala.collection.Seq[Property] = getCity(id).map(_.properties)

  def addCityProperty(id: Int, key: String, value: Any): Boolean = cities.updateWith(digit5(id)) { optCities =>
    optCities.map { cities =>
      val newCities = cities.map { city =>
        val newCity = city.copy(properties = city.properties + (key -> value))
        geojson.updateFeature(newCity)
        newCity
      }
      newCities
    }
  }.isDefined

  private def digit5(int: Int) = f"$int%05d"

  def save(fname: String): Unit = {
    geojson.save(fname)
  }
}
