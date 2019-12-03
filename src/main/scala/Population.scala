import csv.{City, PopulationCensusAnalyzer, TypeCode}
import geojson.{MunicipalBoundaries, MunicipalBoundariesSettings}

object Population {
  val TargetCityType: Seq[TypeCode] = TypeCode.Kubu :: TypeCode.Shi :: TypeCode.ChoSon :: Nil

  def main(args: Array[String]): Unit = {
    val cities = parsePopulations()
    val geoJson = new MunicipalBoundaries(MunicipalBoundariesSettings.Hokkaido)
    cities.filter { city => TargetCityType.contains(city.typ) }.foreach { city =>
      geoJson.addCityProperty(city.code, "population", city.population)
      geoJson.addCityProperty(city.code, "density", city.density)
    }
    geoJson.save("population.geojson")
  }

  def parsePopulations(): Seq[City] = {
    val population = new PopulationCensusAnalyzer
    population.run()
    population.result
  }
}
