package imbibe

import zio._

package object config{
  type Config = Has[Config.Config]

  val getConfig: URIO[Config, Config.Config] =
    ZIO.access(_.get)
}
