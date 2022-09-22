# Project overviews

Google doesn't seem to like indexing GitHub repos without a kick and I tend to write documentation in repos that could
be useful. Medium is a bit too showy for me.

## zio2playground
[zio2playground](https://github.com/pbyrne84/zio2playground)

The project has been done with tests so parts are runnable in an observable fashion.

(exert from the projects README that has all the instructions)
1. Service layering in tests including shared layering. As mentioned there are some gotchas.
2. How to set up an external tool in intellij, this enables run a test without a plugin without
   having to focus off a coding tab.
3. Tracing through the application using OpenTelemetry, this enables Zipkin etc. This includes setting
   current context from incoming headers. We want to keep the trace across system boundaries.
   Not having this sort of stuff can make a fun day a much less than fun day.
4. How we log the trace in the logging, so we can get some kibana or similar goodness. This implementation uses logback
   as not everything is likely to be pure ZIO.log in an application. There is an example of monkeying around
   with the MDC in **LoggingSL4JExample**. This handles java util and direct SL4J logging which probably simulates a lot
   of production environments. For example, I don't think a functionally pure version of PAC4J is on anyone's todo list.
   Anything security based should be implemented as few times as possible, unless you like crackers.

   <br/>MDC stuff does have its limitations due to issues with copying between threads, ideally async
   is being done by the effect system and the java stuff is not async by nature.

   It is a bit hacky but an idea of how to do it, I had to hijack the zio package to read the fiber ref to get the
   **zio.logging.logContext** where this is held.

   **B3TracingOps.serverSpan** creates a span and add it to the logging context.

5. ZIO.log does add to the MDC but only for that call. The logback.xml config adds all MDC
   to the log hence number **LoggingSL4JExample** is doing something similar for the java logging calls.

## PHPStorm metadata example and the undead plugin

Not having an implementation or at least a way to communicate generics is unfun. Lot of boilerplate for typed languages or broken IDE
functionality for non explicitly typed. 

[https://github.com/pbyrne84/phpstorm-metadata-example](ttps://github.com/pbyrne84/phpstorm-metadata-example)

A long time ago I wrote a version of mockito for PHP that we used to use internally where I worked at the time.

[https://github.com/pbyrne84/phpmockito](https://github.com/pbyrne84/phpmockito)

There was another version but that had issues with equality due to it used serialisation to equal values. PHP at the time
and probably still maybe fatally errors on serialising things like exceptions. This version uses reflection to generate
something that can be equalled. To go with that I wrote the Intellij plugin

[https://github.com/pbyrne84/DynamicReturnTypePlugin](https://github.com/pbyrne84/DynamicReturnTypePlugin)

(it was java but auto-switched to kotlin hence the null stuff is quirky as dealing with java nulls is not fun, if it was
still an active project I would have switched to Scala as I prefer Option to ? and Either to throws)

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

Dealing with internal signature changes within the IDE was always fun and had to be integration tested via
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment
Increasing the support within PHPStorm of the more dynamic elements of PHP made it a game of Whac-A-Mole each release.
Theoretically it was not complicated, just involved reverse engineering what the PSI signatures should be and the fact they chained 
until a resolution could be made

e.g. an example signature that had to be created to enable resolution 
```txt
#M#Ђ#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask◮#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?:getServiceWithoutMask◮#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?
```

Internally there is 2 entrance points in the plugin determined by the PhpTypeProvider interface. The first entrance point determines 
whether a call is under the purview of the plugin and then return the signature as a string. The other entrance point uses the passed in 
signature and calls the IDE's PHPIndex (effectively a DB) to build a Collection<PhpNamedElement> that can can be passed back the IDE. Calling the index in the 1st call
(while simplifying it all) can hard lock the GUI thread freezing the IDE.
 