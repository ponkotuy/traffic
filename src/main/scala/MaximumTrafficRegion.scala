import csv.Boundary

import scala.annotation.tailrec

class MaxTrafficRegion(boundaries: Boundaries) {
  def regions: Map[Int, Int] = {
    val maxTraffic = new MaxTraffic(boundaries)
    val maxTrafficRecursive = boundaries.cities.map { city =>
      city -> maxTraffic.getRecursive(city)
    }.toMap
    val cityGroupTable: Map[Set[Int], Seq[Int]] = maxTrafficRecursive.groupBy(_._2).map { case (k, v) => k -> v.map(_._1).toSeq }
    val cityGroupHeadTable: Map[Int, Seq[Int]] = cityGroupTable.map { case (k, v)  => k.min -> v }
    val cityByCityGroup: Map[Int, Int] = cityGroupHeadTable.flatMap { case (group, cities) => cities.map { city => city -> group } }
    cityByCityGroup
  }
}

class MaxTraffic(boundaries: Boundaries) {
  val maxTraffics: Map[Int, Int] = calcMaxTraffic
  println(maxTraffics)

  private def calcMaxTraffic = {
    boundaries.cities.map{ city =>
      val traffics = boundaries.byCity(city)
      city -> Boundaries.other(traffics.maxBy(_.traffic), city)
    }.toMap
  }

  def get(city: Int) = maxTraffics(city)

  @tailrec final def getRecursive(city: Int, points: List[Int] = Nil): Set[Int] = {
    val next = get(city)
    if(points.contains(city)) { points.reverse.dropWhile(_ != city).toSet }
    else getRecursive(next, city :: points)
  }
}

class Boundaries(boundaries: Seq[Boundary]) {
  def cities: Set[Int] = boundaries.flatMap { b => b.startCity :: b.endCity :: Nil }.toSet
  def byCity(city: Int): Seq[Boundary] =
    boundaries.filter { b => b.startCity == city || b.endCity == city }
}

object Boundaries {
  def other(boundary: Boundary, city: Int): Int =
    (Set(boundary.startCity, boundary.endCity) - city).head
}
