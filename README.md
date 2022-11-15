# Project overviews for PByrne84

Even though I do not drink in the week everything I do tries to take into account what it would like deal with 
a massive hangover (Hangover test). For myself I think is a good simulation of what it is like for other people to deal with it
afterwards. Though I do not think torturous hang over based coding empathy training will be on any HR's todo list soon.

Google doesn't seem to like indexing GitHub repos without a kick and I tend to write documentation in repos that could
be useful. Medium is a bit too showy for me.

## Index
1. [zio2playground](Zio2Playgound.html) - ZIO 2 project exampling logging and http with telemetry (B3), shared test layers, testing etc.
2. [Scala Circe Error Rendering](#scala-circe-error-rendering) - Change the error rendering to be informative to other humans
3. [Case Class Pretty Rendering](#scala-case-class-prettification) - This is useful for showing diffs in scalatest as it renders better.
4. [Scala http mock](#scalahttpmock) - A proof of concept replacement for wiremock
3. [PHPStorm based projects](#PHPStorm-based-projects) - Historic intellij plugin life cycle




## <a name="scalahttpmock"> Scala Http Mock
[https://github.com/pbyrne84/scalahttpmock](https://github.com/pbyrne84/scalahttpmock/)

A proof of concept replacement for wiremock. Everything is of course tested. Originally it was HTTP4s, I switched it to Jetty though
as bringing in HTTP4s can cause conflicts. Really it needs to be able to have pluggable backends so it can use akka-http, zio-http or
http4s with mappers between their implementation of requests and the internal one.

Each of those libraries could change on releases, so it could be fun to keep the backends up to date. A fun future project.

### Why not use wiremock?
Wiremock is actually quite complicated for what it does. Everything is hidden in static method imports and builders. In Scala we have 
nicer things such as default values for parameters and variations of copy constructors to override that giving a new instance.
Things like expectations can be used for verifications in a much more friendly fashion. Verifications before result assertion can
be a better approach for cleaner test failures but wiremock can make this a lot of effort so this approach is probably not done by example as
much as it should be. Knowing a remote service was not called at all or with the wrong values is a much better failure than expected true and 
got false.

Though wiremock is a lot better if you use 1 instance per service you are faking, just having 1 wiremock over multiple services
renders the nearest match error reporting useless. Using one instance of this for multiple services would also render the nearest 
match error reporting useless. Basically you can be told unfriendly misleading garbage on failure in either case.

## <a name="PHPStorm-based-projects"> PHPStorm metadata example and the undead plugin

Not having an implementation or at least a way to communicate generics is unfun. Lot of boilerplate for typed languages or broken IDE
functionality for non explicitly typed. 

[https://github.com/pbyrne84/phpstorm-metadata-example](https://github.com/pbyrne84/phpstorm-metadata-example)

A long time ago I wrote a version of mockito for PHP that we used to use internally where I worked at the time.

[https://github.com/pbyrne84/phpmockito](https://github.com/pbyrne84/phpmockito)

There was another similar project but that had issues with equality due to it used serialization to equal values. PHP at the time
and probably still does have fatal errors when serializing things like exceptions and SplFileInfo. This version uses type based factories
to create something that can be equalled. You can see from the test what is achieved by this.

https://github.com/pbyrne84/phpmockito/blob/master/test/PHPMockito/ToString/ToStringAdaptorFactoryTest.php

THis leads to human-readable errors in verifies etc. Mocking can be made harder than it should be by the implementation such as being 
stringly based such as java was before generics.

[http://jmock.org/oopsla2004.pdf](http://jmock.org/oopsla2004.pdf)
```java
mockLoader.expect(once())
    .method("load").with( eq(KEY) )
    .will( returnValue(VALUE) );
```

Method is not refactor safe and no IDE help for parameters. Not very fun. Implementation and project usage can also completely put people 
off a concept. I have a rule, the simple tests the complicated and if a project gets to the point where tests cannot be written first
then the code is really testing the tests. Tests are there to communicate to the people of the future that we really were not
having a brain fart that day and also to allow us to observe what is happening in a controlled fashion. Bugs come from assumptions,
 tests allow us and other to prove our assumptions.

To go with that I wrote the Intellij plugin

[https://github.com/pbyrne84/DynamicReturnTypePlugin](https://github.com/pbyrne84/DynamicReturnTypePlugin)

It was java but auto-switched to kotlin hence the null stuff is quirky as dealing with java nulls is not fun, if it was
still an active project I would have switched to Scala as I prefer Option to ? and Either to throws.

which predates the metadata stuff and grew and grew in use. Almost all the functionality is replaceable by the metadata or 
the generic php doc tags which eventually came into effect. If the phpdoc had been supported then the plugin would never
of existed,

```php
/**
 * @template T
 *
 * @param T $a
 *
 * @return T
 */
function a($a) {
    return $a;
}

a(new DOMDocument())->cloneNode(); //no warnings and auto completes
```

I like generics and ADT's which is why I like scala. PHP was relying on too many __call methods to bolt on functionality
dynamically which was a major thing that drove me away. Most people complain about parameter order etc. but that is just fluff
to me. Too many dictionary structures in a project and too much magic is the killer as too much is tied to an individuals memories.
People come and go, inheriting is always less fun than creating.

In the plugin I added ways via the scriptengine do custom stuff via groovy or javascript. That was marked as deprecated
[https://stackoverflow.com/questions/58179771/nashorn-alternative-for-java-11](https://stackoverflow.com/questions/58179771/nashorn-alternative-for-java-11)

Dealing with internal signature changes within the IDE was always fun and had to be integration tested using PHPStorms/Intellij inspections via

[https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment](https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment)

The inspections should have returned no errors for unresolvable calls. As things grew so did the complexity of calls. If something did not work
then use the [https://plugins.jetbrains.com/plugin/227-psiviewer](https://plugins.jetbrains.com/plugin/227-psiviewer) plugin to get the 
signature string and try to decipher how it has been mangled or is incorrect.

Increasing the support within PHPStorm of the more dynamic elements of PHP made it a game of Whac-A-Mole each release.
Theoretically it was not complicated, it just involved reverse engineering what the PSI signatures should be and the fact they chained 
until a resolution could be made.

e.g. an example signature that had to be created to enable resolution 
```txt
#M#Ђ#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask◮#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?:getServiceWithoutMask◮#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?
```

Internally there is 2 entrance points in the plugin determined by the PhpTypeProvider interface. The first entrance point determines 
whether a call is under the purview of the plugin and then return the signature as a string. The other entrance point uses the passed in 
signature and calls the IDE's PHPIndex (effectively a DB) to build a Collection<PhpNamedElement> that can can be passed back the IDE. Calling the index in the 1st call
(while simplifying it all) can hard lock the GUI thread freezing the IDE.
 