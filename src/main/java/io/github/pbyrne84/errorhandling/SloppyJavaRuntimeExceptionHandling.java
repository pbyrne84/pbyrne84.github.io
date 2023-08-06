package io.github.pbyrne84.errorhandling;

import java.rmi.AccessException;
import java.rmi.ConnectException;

public class SloppyJavaRuntimeExceptionHandling {


    // Just wrap it all in RunTime
    public void run() {
        checkedExceptionMethod1();
        checkedExceptionMethod2();

    }

    // These are api calls that hide there errors from the signature
    public void checkedExceptionMethod1() {
        try {
            throw new AccessException("sss");
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkedExceptionMethod2() {
        try {
            if (true) {
                throw new AccessException("sss");
            } else {
                throw new ConnectException("sass");
            }
        } catch (AccessException | ConnectException e) {
            throw new RuntimeException(e);
        }
    }
}
