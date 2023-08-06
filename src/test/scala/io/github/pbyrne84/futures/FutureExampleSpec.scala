package io.github.pbyrne84.futures

import org.scalactic.source
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FutureExampleSpec extends AnyWordSpec with Matchers with ScalaFutures {

  "Scala futures" should {
    "waits for too long with default patience on a blocking task" in {
      val startEpochMill = currentEpochMill

      val eventualResult = Future {
        Thread.sleep(1)
        1
      }

      withClue("Default integration patience polls every 15 mills for futureValue") {
        eventualResult.futureValue mustBe 1
        val milliSecondsTaken: Long = currentEpochMill - startEpochMill
        println("Waiting " + milliSecondsTaken)
        milliSecondsTaken must be > 15L
      }
    }

    def currentEpochMill: Long = {
      Instant.now().toEpochMilli
    }

    "still wait too long for immediate tasks" in {
      val startEpochMill = currentEpochMill

      val eventualResult = Future.successful(1)

      eventualResult.futureValue mustBe 1
      val milliSecondsTaken: Long = currentEpochMill - startEpochMill
      println("No waiting " + milliSecondsTaken)
      milliSecondsTaken must be < 15L

    }

    "custom integration patience should speed things up" in {

      val startEpochMill = currentEpochMill

      implicit val customPatienceConfig: PatienceConfig =
        PatienceConfig(
          timeout = scaled(Span(15, Seconds)),
          interval = scaled(Span(1000, Millis))
        )

      val pos = implicitly[source.Position]

      Future {
        Thread.sleep(100)
        1
      }.futureValue(interval(Span.apply(300, Millis)))(customPatienceConfig, pos) mustBe 1
      val milliSecondsTaken: Long = currentEpochMill - startEpochMill
      println("Faster waiting " + milliSecondsTaken)
      milliSecondsTaken must be < 5L

    }
  }

}
