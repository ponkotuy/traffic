package csv

import java.io.File

import scala.util.matching.Regex

class PopulationCensusAnalyzer extends CsvAnalyzer {
  override def file: File = new File("001_01.csv")
  override val dropLine: Int = 10
  override val encode: String = "Shift_JIS"

  var cities: List[City] = Nil

  override def execLine(line: Seq[String]): Unit = {
    City.fromRecord(line).foreach { city => cities = city :: cities }
  }

  def result: List[City] = cities
}

case class City(code: Int, typ: TypeCode, name: String, population: Int, area: BigDecimal, density: BigDecimal)

object City {
  val Code = 2
  val Type = 3
  val Name = 6
  val Population = 7
  val Area = 11
  val Density = 12
  val Space: Regex = """[\s　]""".stripMargin.r

  def fromRecord(xs: Seq[String]): Option[City] = for {
    code <- xs.lift(Code).flatMap(_.toIntOption)
    typ <- xs.lift(Type)
    typeCahr <- typ.headOption
    tCode <- TypeCode.find(typeCahr)
    name <- xs.lift(Name)
    population <- xs.lift(Population)
    area <- xs.lift(Area)
    density <- xs.lift(Density)
  } yield {
    City(code, tCode, Space.replaceAllIn(name, ""), population.toInt, BigDecimal(area), BigDecimal(density))
  }
}

sealed abstract class TypeCode(val code: Char)
object TypeCode {
  case object Todofuken extends TypeCode('a') // 都道府県合計
  case object ShiGun extends TypeCode('b') // 都道府県の市部/郡部合計
  case object ShinkoKyoku extends TypeCode('c') // 北海道の振興局
  case object Shigaichi extends TypeCode('d') // 市街地人口
  case object Kubu extends TypeCode('0') // 政令指定都市の区部
  case object Seireishi extends TypeCode('1') // 政令指定都市合計
  case object Shi extends TypeCode('2') // 政令指定都市でない市
  case object ChoSon extends TypeCode('3') // 町村
  case object Gappei extends TypeCode('9') // ここ最近合併した市区町村の旧領域

  val values: List[TypeCode] =
    Todofuken :: ShiGun :: ShinkoKyoku :: Shigaichi :: Kubu :: Seireishi :: Shi :: ChoSon :: Gappei :: Nil

  def find(c: Char): Option[TypeCode] = values.find(_.code == c)
}
