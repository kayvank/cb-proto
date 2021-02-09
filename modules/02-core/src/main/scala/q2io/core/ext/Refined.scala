package q2io.core
package ext

import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.Size

object Refined {

  implicit def validateSizeN[N <: Int, R](
      implicit w: ValueOf[N]
  ): Validate.Plain[R, Size[N]] =
    Validate.fromPredicate[R, Size[N]](
      _.toString.size == w.value,
      _ => s"Must have ${w.value} digits",
      Size[N](w.value)
    )
}