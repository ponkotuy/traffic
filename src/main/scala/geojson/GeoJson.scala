package geojson

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.util.UUID

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable

class GeoJson(fname: String) {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  private[this] val collection = mapper.readValue(new File(fname), classOf[FeatureCollection])
  private[this] val features: mutable.Seq[FeatureWithUUID] = mutable.Seq(collection.features.map(FeatureWithUUID.apply(UUID.randomUUID(), _)): _*)

  def getFeatures: scala.collection.Seq[FeatureWithUUID] = features

  def updateFeature(feature: FeatureWithUUID): Unit = {
    val idx = getFeatures.indexWhere(_.uuid == feature.uuid)
    features.update(idx, feature)
  }

  def save(fname: String): Unit = {
    val bytes = mapper.writeValueAsBytes(collection.copy(features = features.map(_.toOrig).toSeq))
    val bos = new BufferedOutputStream(new FileOutputStream(fname))
    bos.write(bytes)
    bos.close()
  }
}

@JsonIgnoreProperties(Array("name"))
case class FeatureCollection(`type`: String, crs: Crs, features: Seq[Feature])
case class Crs(`type`: String, properties: Map[String, Any])
case class Feature(`type`: String, properties: Map[String, Any], geometry: Geometry)

case class FeatureWithUUID(uuid: UUID, `type`: String, properties: Map[String, Any], geometry: Geometry) {
  def toOrig: Feature = Feature(`type`, properties, geometry)
}

object FeatureWithUUID {
  def apply(uuid: UUID, f: Feature): FeatureWithUUID = FeatureWithUUID(uuid, f.`type`, f.properties, f.geometry)
}

case class Geometry(`type`: String, coordinates: Seq[Seq[Seq[Seq[BigDecimal]]]])
