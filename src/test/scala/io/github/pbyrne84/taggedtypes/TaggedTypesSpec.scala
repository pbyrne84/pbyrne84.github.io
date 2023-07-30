package io.github.pbyrne84.taggedtypes

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TaggedTypesSpec extends AnyWordSpec with Matchers {

  // https://alvinalexander.com/scala/scala-3-opaque-types-how-to/ good reading

  "Tagged and Opaque types" must {

    "be creatable without a library in scala 2" in {
      // Tagged types work by using .asInstanceOf in the background. As long as a trait does not contain
      // any implementation we can cast something to having that trait
      // It is a variation of marker interfaces https://www.baeldung.com/java-marker-interfaces
      // that give compile time guarantees. The trait is not detectable at runtime.
      // Why use them? Well we can validate on creation meaning this is now a trusted value throughout the
      // system. Used for creation of JSON payloads we can guarantee our payloads fit a contract. The more
      // software we build that has good guarantees with clear failure means the more software we can
      // end up managing in an estate.
      //
      // If we cannot guarantee our contracts it can break things for teams who are quite removed from us.

      sealed trait AnimalTag
      sealed trait CatTag extends AnimalTag
      sealed trait DogTag extends AnimalTag

      // we can find usages of StringCat very easily, simple strings alas no, so helps us collate call paths
      // in business logic.
      type StringCat = String with CatTag

      object StringCat {
        def apply(noise: String): Either[String, StringCat] = {
          if (noise == "meow") {
            Right(noise.asInstanceOf[StringCat]) // this does not add to the heap anymore than the initial string does.
          } else {
            // Either is functional, throwing exceptions much less so as it cannot be reasoned about by signature.
            // This makes it very hard to recover from errors as people have to analyse call chains, call chains
            // also change. Clean code has a good section on checked exceptions which was Java's attempt at
            // clarifying error cases.
            // This will be covered in my error handling stuff.
            Left(s"'$noise' is not a noise cat makes")
          }
        }

        // Just mark it so the import is easily found. Though with these types of imports best to keep away from
        // the main imports and keep them close to use.
        // For example a lot of string operations are added by default by StringOps
        object ops {
          // Scala 2 Extension method teqnuique
          implicit class AnimalStringOps(value: String) {
            def attemptAsCat: Either[String, StringCat] = StringCat(value)
          }

        }

      }

      def onlyAllowCats(cat: StringCat): Either[String, String] = {
        Right(s"cat allowed - $cat")
      }

      def allowString(value: String): Unit = {}

      import StringCat.ops._

      val stringOrCat1 = for {
        stringCat1 <- StringCat("meow")
        stringCat2 <- "meow".attemptAsCat
        result <- onlyAllowCats(stringCat1)
        // StringCat is still passable as String
        _ = allowString(result)
      } yield result

      val stringOrCat2 = for {
        stringCat <- StringCat("woof")
        result <- onlyAllowCats(stringCat)
      } yield result

      stringOrCat1 mustBe Right("cat allowed - meow")
      stringOrCat2 mustBe Left("'woof' is not a noise cat makes")

    }

    "scala 3 has opaque types" in {
      // https://docs.scala-lang.org/scala3/book/types-opaque-types.html
      // https://www.baeldung.com/scala/opaque-type-alias

      object Animals {
        opaque type AnimalTag = String
        opaque type StringCat <: AnimalTag with String = String
        opaque type StringDog <: AnimalTag = String

        // We can directly assign here as within the object scope it is trusted
        // This makes things less boiler plate
        val a: AnimalTag = ""

        object OpaqueStringCat {
          def apply(noise: String): Either[String, StringCat] = {
            if (noise == "meow") {
              Right(noise)
            } else {
              // Either is functional, throwing exceptions much less so as it cannot be reasoned about by signature.
              // This makes it very hard to recover from errors as people have to analyse call chains, call chains
              // also change. Clean code has a good section on checked exceptions which was Java's attempt at
              // clarifying error cases.
              // This will be covered in my error handling stuff.
              Left(s"'$noise' is not a noise cat makes")
            }
          }

          object ops {
            // Scala 3 extension method. The term implicit is no longer used as it is a separate concept.
            // Conflating it with Parameters and Conversions causes negative association.
            // Scala uses extension methods for a lot of things in the background such a "2".toInt
            extension (text: String) {
              def attemptAsCat: Either[StringDog, Animals.StringCat] = OpaqueStringCat(text)
            }
          }

        }
      }

      import Animals._

      def onlyAllowCats(cat: StringCat): Either[String, String] = {
        Right(s"opaque cat allowed - $cat")
      }

      def onlyAllowAnimals(animal: AnimalTag): Either[String, String] = {
        Right(s"opaque animal - $animal")
      }

      def allowString(value: String): Unit = {}

      import Animals.OpaqueStringCat.ops._

      val stringOrOpaqueCat = for {
        stringCat1 <- OpaqueStringCat("meow")
        stringCat2 <- "meow".attemptAsCat
        _ <- onlyAllowCats(stringCat1)
        _ <- onlyAllowAnimals(stringCat1)
        // StringCat is still passable as String
        _ = allowString(stringCat1)
      } yield stringCat1

      stringOrOpaqueCat mustBe Right("meow")
    }
  }
}
