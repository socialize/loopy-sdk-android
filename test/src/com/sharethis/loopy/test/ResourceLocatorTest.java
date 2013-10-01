package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.util.ClassLoaderProvider;
import com.sharethis.loopy.sdk.util.ResourceLocator;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Polites
 */
public class ResourceLocatorTest extends LoopyAndroidTestCase {

    public void testLocateInAssets() throws IOException {
        // We can't mock the AssetManager because it's final,
        // but this test project has assets so we'll just integration test
        ResourceLocator locator = new ResourceLocator();
        InputStream notNull = locator.locateInAssets(getLocalContext(), "loopy.properties");
        InputStream isNull = locator.locateInAssets(getLocalContext(), "does.not.exist");
        assertNotNull(notNull);
        assertNull(isNull);
    }

    public void testLocateInClassPath() throws IOException {
        String name = "foobar";
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        ClassLoaderProvider classLoaderProvider = Mockito.mock(ClassLoaderProvider.class);
        InputStream in = Mockito.mock(InputStream.class);

        Mockito.when(classLoaderProvider.getClassLoader()).thenReturn(classLoader);
        Mockito.when(classLoader.getResourceAsStream(name)).thenReturn(in);

        ResourceLocator locator = new ResourceLocator();
        locator.setClassLoaderProvider(classLoaderProvider);

        assertSame(in, locator.locateInClassPath(name));
    }

}
