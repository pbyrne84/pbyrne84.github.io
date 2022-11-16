# Scala Http Mock
[https://github.com/pbyrne84/scalahttpmock](https://github.com/pbyrne84/scalahttpmock/)

A proof of concept replacement for wiremock. Everything is of course tested. Originally it was HTTP4s, I switched it to Jetty though
as bringing in HTTP4s can cause conflicts. Really it needs to be able to have pluggable backends so it can use akka-http, zio-http or
http4s with mappers between their implementation of requests and the internal one.

Each of those libraries could change on releases, so it could be fun to keep the backends up to date. A fun future project.

## Why not use wiremock?
Wiremock is actually quite complicated for what it does. Everything is hidden in static method imports and builders. In Scala we have
nicer things such as default values for parameters and variations of copy constructors to override that giving a new instance.
Things like expectations can be used for verifications in a much more friendly fashion. Verifications before result assertion can
be a better approach for cleaner test failures but wiremock can make this a lot of effort so this approach is probably not done by example as
much as it should be. Knowing a remote service was not called at all or with the wrong values is a much better failure than expected true and
got false.

Though wiremock is a lot better if you use 1 instance per service you are faking, just having 1 wiremock over multiple services
renders the nearest match error reporting useless. Using one instance of this for multiple services would also render the nearest
match error reporting useless. Basically you can be told unfriendly misleading garbage on failure in either case.

## Example use

```scala
val responseJson = """{"a":"1"}"""
val expectation = ServiceExpectation()
    .addHeader(HeaderEquals("a", "avalue"))
    .withMethod(PostMatcher())
    .withUri("/test/path".asUriEquals)
    .withResponse(JsonResponse(202, Some(responseJson))) // adds json header and allows for custom headers

service.addExpectation(expectation)
```

## Example failure

```
The following expectation was matched 0 out of 1 times:-
ServiceExpectation[method="Post"](
  Headers : [ Equals("a", "avalue") ],
  Uri     : Uri equals "/test/path",
  Params  : Any,
  Body    : {
    "z" : "26"
  }
)
The following requests were made (1):-
[INVALID] SCORE:4.0/3.0 failed {CONTENT Equals Json}
  Method  - Matching     : Post
  Headers - Matching     : [ Equals("a", "avalue") ]
  Headers - Non matching : None
  Uri     - Matching     : Uri equals "/test/path"
  Params  - Matching     : None
  Params  - Non matching : None
  Content - Non matching : {
    "z" : "26"
  }
Request[method="POST", path="/test/path"](
  Uri     : "/test/path",
  Params  : [],
  Headers : [ ("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"),
              ("Accept-Encoding", "gzip, deflate"),
              ("Connection", "keep-alive"),
              ("Host", "localhost:9000"),
              ("User-Agent", "Java/1.8.0_77"),
              ("a", "avalue") ],
  Body    : None
)

```