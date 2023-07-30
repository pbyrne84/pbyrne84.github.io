package io.github.pbyrne84.errorhandling;

import java.rmi.AccessException;
import java.rmi.ConnectException;

public class SloppyJavaCheckedExceptionHandling {


    // Pretend we have no control of what calls this method, we cannot change the exception signature. We have not stated there
    // is one. Everything that calls this assumes that there is likely RuntimeExceptions. Sharks swimming under the surface
    // of the sea.
    //
    // Java is very verbose, creating the ideal of having checked exceptions with class hierarchies ultimately fell short of
    // the nirvana fallacy. When discussing things like this, we always have to take into account the nuance of implementation
    // as it can be the implementation causing frustration negatively infecting the concept of the idea.
    public void run(){
        try {
            checkedExceptionMethod1();
            checkedExceptionMethod2();
        } catch (AccessException | ConnectException e) {
            // do logging or something later. (later never comes)
        }
    }

    // pretend we have no control of these signatures, so they have to be AccessException etc.
    public void checkedExceptionMethod1() throws AccessException {
        throw new AccessException("sss");
    }


    public void checkedExceptionMethod2() throws AccessException, ConnectException {
        if(true){
            throw new AccessException("sss");
        }else {
            throw new ConnectException("sass");
        }
    }
}
