
# Scala Http Mock
[https://github.com/pbyrne84/scalahttpmock](https://github.com/pbyrne84/scalahttpmock/)

A proof of concept replacement for wiremock. Everything is, of course, tested. Originally it was HTTP4s, I switched it to Jetty though
as bringing in HTTP4s can cause conflicts. Really it needs to be able to have pluggable backends, so it can use akka-http, zio-http or
HTT4S with mappers between their implementation of requests and the internal one.

Each of those libraries could change on releases, so it could be fun to keep the backends up to date. A fun future project.

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