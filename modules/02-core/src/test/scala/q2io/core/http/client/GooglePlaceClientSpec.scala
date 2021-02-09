package q2io.core.http.client

import org.specs2._
import org.specs2.specification.core.SpecStructure
import cats.effect.Sync
import cats.syntax.all._
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import ciris.refined._
import ciris._

import q2io.core.config.Load
import q2io.core.config.AppConfig.GooglePlaceClientConfig
import q2io.core.config.AppConfig.GooglePlaceURI
import q2io.core.config.AppConfig.GoogleApiKey
import cats.effect.IO

class GooglePlaceClientSpec extends Specification {
  def is: SpecStructure = s"""
    GooglePlaceClient API specs are:
        Build places request uri  $e1

"""
  def e1 = {
    val cfg = GooglePlaceClientConfig(
      uri = GooglePlaceURI("http://map"),
      key = GoogleApiKey(Secret("someKey"))
    )

    val actual =
      "maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBBHBgLQfRjis0WUsSSWeEgpFeLxo-WLYI"

    true === true
  }

}
