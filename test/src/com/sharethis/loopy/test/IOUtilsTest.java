package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Jason Polites
 */
public class IOUtilsTest extends LoopyAndroidTestCase {

    public void testRead() throws IOException {
        String testData = "the quick brown fox jumps over the lazy dog";
        ByteArrayInputStream in0 = new ByteArrayInputStream(testData.getBytes());
        ByteArrayInputStream in1 = new ByteArrayInputStream(testData.getBytes());
        String out0 = IOUtils.read(in0, 1024);
        String out1 = IOUtils.read(in1, 4);
        assertEquals(testData, out0);
        assertEquals(testData, out1);
    }

}
