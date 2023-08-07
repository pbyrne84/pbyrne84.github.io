# Scala Circe Error Rendering
[scala-circe-error-rendering](https://github.com/pbyrne84/scala-circe-error-rendering)

When decoding json with Circe the default failure message is not really presentable in an informative fashion. You return that error
to a caller it will not help them fix their payload, which ideally is what you want, else you may have to get involved. Also, logging
these problems in an informative fashion is useful.

When machines talk to machines, a lot of things can go wrong sporadically without anyone noticing for a while, maybe months. 
"Getting stuff done" can often lead to not much stuff done when dealing with distributed systems. "Getting stuff done" often leads to us 
not considering the journey required to troubleshoot a system, just that we have achieved a task in a very "happy path" way.

The test examples how it renders accumulatively with a Tagged decoder so we can verify something like Age is valid on API entrance.

[Rendering test](https://github.com/pbyrne84/scala-circe-error-rendering/blob/main/src/test/scala/com/github/pbyrne84/circe/rendoring/CirceErrorRenderingSpec.scala)

```json
{
  "xField1" : {
    "aField3" : {
      "bField1" : "the field is missing"
    },
    "aField2" : {
      "cField2" : "the field is missing",
      "cField1" : "the field is missing"
    },
    "aField1" : "the field is missing"
  },
  "xField2" : "the field is missing",
  "xField3" : "the field is not the correct type, expected 'Boolean'",
  "xField4" : "the field is not the correct type, expected 'Array'",
  "xField5" : "custom error message",
  "age" : "Age '-1' cannot be below zero"
}
```

This can be hooked into something like the ErrorAccumulatingCirceSupport for Akka HTTP
[de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport](https://github.com/hseeberger/akka-http-json/blob/master/akka-http-circe/src/main/scala/de/heikoseeberger/akkahttpcirce/CirceSupport.scala)

It is/has been in production in various projects and always proves useful.

A test exampling this can be found at
[AkkaHttpCirceErrorRenderingSpec.scala#L43](https://github.com/pbyrne84/scala-circe-error-rendering/blob/c300c414cde00b3ee5bc8778ef38df1fce095a90/src/test/scala/com/github/pbyrne84/circe/rendering/AkkaHttpCirceErrorRenderingSpec.scala#L43)

Example Akka HTTP error handling code
```scala
  .handle {
    case malformedRequestContentRejection: MalformedRequestContentRejection =>
      malformedRequestContentRejection.cause match {
        case decodingFailuresException: ErrorAccumulatingCirceSupport.DecodingFailures =>
          val renderedErrorJson: Json = CirceErrorRendering.renderErrors(decodingFailuresException.failures)

          complete(StatusCodes.BadRequest,
                   ErrorResponse("The payload was invalid with the following errors", renderedErrorJson))

        case _ =>
          //Something more sensible should be done here
          complete(
            StatusCodes.BadRequest,
            s"The payload was invalid with the following errors ${malformedRequestContentRejection.toString}"
          )
      }
  }
```

### Example tagged decoder

Ties into [ScalaTaggedTypes.html](ScalaTaggedTypes.html) and guaranteeing data stringency easily.

```scala 
  trait SafetyTag
  type TaggedAge = Int with SafetyTag

  class TaggedDecoder[From, To](attempt: From => Either[String, To])(implicit aDecoder: Decoder[From])
      extends Decoder[To] {
    override def apply(c: HCursor): Result[To] = {
      c.as[From]
        .flatMap(
          value => attempt(value).left.map((error: String) => DecodingFailure(CustomReason(error), c))
        )
    }
  }

  object TaggedAge {

    implicit val taggedAgeDecoder: Decoder[TaggedAge] =
      new TaggedDecoder((possibleAge: Int) => attemptAge(possibleAge))

    // You can generify all this code when doing many like we have created a generified decoder
    def attemptAge(age: Int): Either[String, TaggedAge] = {
      if (age < 0) {
        Left(s"Age '$age' cannot be below zero")
      } else {
        Right(age.asInstanceOf[TaggedAge])
      }
    }
  }
```