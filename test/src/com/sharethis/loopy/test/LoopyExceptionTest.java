package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.LoopyException;

/**
 * @author Jason Polites
 */
public class LoopyExceptionTest extends LoopyAndroidTestCase {

    public void testExceptionConstructors() {
        int code = 69;
        assertEquals(code, new LoopyException(code).getCode());
        assertEquals(code, new LoopyException("foobar", code).getCode());
        assertEquals(code, new LoopyException("foobar", new Exception(), code).getCode());
        assertEquals(code, new LoopyException(new Exception(), code).getCode());
    }
}
