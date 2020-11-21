package imbibe

import zio.Has

package object persistence {
  type HasCocktailPersistence = Has[CocktailPersistence.Service]
}
