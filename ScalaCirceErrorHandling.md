# Scala Circe Error Rendering
[scala-circe-error-rendering](https://github.com/pbyrne84/scala-circe-error-rendering)

When decoding json with circe the default failure message is not really presentable in an informative fashion. You return that error
to a caller it will not help them fix their payload, which ideally is what you want else you may have to get involved. Also logging
these problems in an informative fashion is useful.

The test examples how it renders accumulatively.

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
  "xField5" : "custom error message"
}
```

This can be hooked into something like the ErrorAccumulatingCirceSupport for akka http
[de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport](https://github.com/hseeberger/akka-http-json/blob/master/akka-http-circe/src/main/scala/de/heikoseeberger/akkahttpcirce/CirceSupport.scala)

It is/has been in production in various projects and always proves useful.
