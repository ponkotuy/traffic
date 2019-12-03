package csv

case class Line(number: Int, name: String, period: BigDecimal)

object Line {
  val LineNumber = 4
  val LineName = 5
  val Period = 23

  def fromLine(xs: Seq[String]): Option[Line] = for {
    number <- xs.lift(LineNumber)
    name <- xs.lift(LineName)
    period <- xs.lift(Period)
    if number.nonEmpty
  } yield Line(number.toInt, name, BigDecimal(period))
}
