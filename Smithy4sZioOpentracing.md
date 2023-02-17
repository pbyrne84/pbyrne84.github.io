# Smithy4s ZIO Opentracing (ZIO 2/Scala/tracing/layering/logging/testing)

This utilises the knowledge learnt from [Zio2Playground.html](Zio2Playground.html) and applies it to
[https://disneystreaming.github.io/smithy4s/](https://disneystreaming.github.io/smithy4s/).

The teason for being interested in Smithy is it handles ADTs in requests and responses. One of the joys of Scala is ADTs.
Having payloads that can have values in some circumstances and not others is difficult otherwise. Errors should
happen at deserialization not in the app on random edge cases.

Full readme can be found
at [https://github.com/pbyrne84/smithy4s-zio-opentracing](https://github.com/pbyrne84/smithy4s-zio-opentracing)

Test exercising everything can be found
at [ZioMainSpec.scala](https://github.com/pbyrne84/smithy4s-zio-opentracing/blob/main/examplezio/src/test/scala/zioexample/ZIOMainSpec.scala).

Challenges faced include

1. All examples of ZIO with http4s seems to be Blaze not Ember.
2. Starting traces from the request. ZIO has tracing as a first class citizen but requires the request headers at least
   for open tracing.
3. Layering is not compatible with ```TASK[_]```. The dependencies need to be provided to create that signature.
   The easiest way seems to using the ZIO smithy compatible implementation as a facade that gives live layers and then
   the lower level actual business services can be tested singularly with custom layering.
4. Managing trace info on successful route responses, this makes sure we are not bad citizens breaking the tracing chain.
5. Managing trace info on BadRequest responses that never get into the route implementation. This logic can be found 
   at [ZioRoutes.scala](https://github.com/pbyrne84/smithy4s-zio-opentracing/blob/main/examplezio/src/main/scala/zioexample/ZIORoutes.scala).
