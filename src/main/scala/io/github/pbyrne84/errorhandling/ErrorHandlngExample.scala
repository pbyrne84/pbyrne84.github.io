package io.github.pbyrne84.errorhandling

import cats.data.EitherT
import zio.ZIO

import scala.concurrent.Future

// Use an abstract class as it allows a constructor in scala 2.13, scala 3 has trait params.
// You can use traits in scala 2.12 but you have to override getMessage etc. and not doing
// it right breaks logging and people don't really notice until someone goes to the logs
// finding missing messages and causes whoever is supporting to be unable to resolve the issue easily.
//
// Always care about the people who support the application as it can get very sucky for them if we don't
sealed abstract class ExampleError(message: String, maybeCause: Option[Throwable] = None)
    extends RuntimeException(message, maybeCause.orNull)

case class ExampleNetworkError(message: String, cause: Throwable) extends ExampleError(message, Some(cause))
case class ExampleDatabaseError(message: String, cause: Throwable) extends ExampleError(message, Some(cause))

//
case class ExampleInvalidParamError(message: String) extends ExampleError(message)

class ErrorHandlngExample {

  // Don't use this one in production
  import scala.concurrent.ExecutionContext.Implicits.global

  import cats.implicits._

  /**
    * EitherT with future example
    *
    * From the signature we can determine we are only going to get 3 categories of failure.
    */
  def futureExample1(): Future[Either[ExampleError, Boolean]] = {
    // EitherT's is not very good at working out that the two errors are related
    // ExampleNetworkError
    // ExampleDatabaseError
    // Are children off ExampleError hence we can cast noisily
    (for {
      _ <- EitherT[Future, ExampleError, Boolean](Future(call1)) // boilerplate tastic, every line?
      _ <- Future(call2).asEitherT
      result <- eitherT(Future(call3(3)))
    } yield result).value // Flips from EitherT[Future,ExampleError, _] to Future[Either[ExampleError,_]]
  }

  // Extension method approach -
  // A <: ExampleError means B will be child of ExampleError
  // B - result type
  implicit class FutureOps[A <: ExampleError, B](value: Future[Either[A, B]]) {

    def asEitherT: EitherT[Future, ExampleError, B] =
      EitherT[Future, ExampleError, B](value)

  }

  // method call approach, give a better name :)
  private def eitherT[A <: ExampleError, B](value: Future[Either[A, B]]) = {
    EitherT[Future, ExampleError, B](value)
  }

  private def call1: Either[ExampleNetworkError, Boolean] = {
    ???
  }

  private def call2: Either[ExampleDatabaseError, Boolean] = {
    ???
  }

  private def call3(value: Int): Either[ExampleInvalidParamError, Boolean] = {
    if (value < 4) {
      // Have error with message signifying the value that caused the issue
      // Built in library exceptions have low guarantee of doing this as it could
      // leak security info in exceptions. We need the value to repeat the error and resolve the issue
      // easily.
      Left(ExampleInvalidParamError(s"value $value is below 4"))
    } else {
      Right(true)
    }
  }

  /**
    * Example to show that the EitherT noise is not an inherit Scala thing but we have to dance around the limitations
    * of Future[_] or Cats Task[_] not having typed errors in their design, this leads to a lot of noise, the following
    * code can detect the inheritance of the errors correctly so no monkey dancing.
    * @return
    */
  def zioExample(): ZIO[Any, ExampleError, Boolean] = {
    for {
      _ <- ZIO.fromEither(call1)
      _ <- ZIO.fromEither(call2)
      _ <- ZIO
        .attempt(throw new RuntimeException("eeek")) // returns the equivalent of Task[_], Future[_]
        .mapError(error => ExampleDatabaseError("eek caused an error", error)) // Make the error type safe easily
      result <- ZIO.fromEither(call3(4))
    } yield result
  }
}
