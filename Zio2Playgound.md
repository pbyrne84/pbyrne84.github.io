#  zio2playground (ZIO 2/Scala/tracing/layering/logging/testing)

Full readme and code can be found at [https://github.com/pbyrne84/zio2playground](https://github.com/pbyrne84/zio2playground)

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

