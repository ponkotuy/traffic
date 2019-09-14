import csv.{PopulationCensusAnalyzer, TypeCode}
import geojson.MunicipalBoundaries

object Population {
  val TargetCityType = TypeCode.Kubu :: TypeCode.Shi :: TypeCode.ChoSon :: Nil
  def main(args: Array[String]): Unit = {
    val cities = parsePopulations()
    val geoJson = new MunicipalBoundaries
    cities.filter { city => TargetCityType.contains(city.typ) }.foreach { city =>
      geoJson.addCityProperty(city.code, "population", city.population)
      geoJson.addCityProperty(city.code, "density", city.density)
    }
    geoJson.save("population.geojson")
  }

  def parsePopulations() = {
    val population = new PopulationCensusAnalyzer
    population.run()
    population.result
  }
}
