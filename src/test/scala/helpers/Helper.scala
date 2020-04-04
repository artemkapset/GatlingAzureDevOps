package helpers

import scala.util.Random

object Helper {

  def getRandomName(): String = {
    s"Azure${new Random().alphanumeric.filter(_.isLetter).take(5).mkString.toUpperCase}"
  }

}
