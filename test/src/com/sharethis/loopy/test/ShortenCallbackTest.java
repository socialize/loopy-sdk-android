package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.MockItem;
import com.sharethis.loopy.sdk.MockShareCallback;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
public class ShortenCallbackTest extends LoopyAndroidTestCase {

    public void testOnSuccess() throws JSONException {

        MockShareCallback cb = new MockShareCallback() {
            @Override
            public void onResult(Item item, Throwable error) {

            }
        };

        MockItem item = Mockito.mock(MockItem.class);

        cb.setItem(item);

        JSONObject json = new JSONObject();
        json.put("shortlink", "foobar");

        cb.onSuccess(json);

        Mockito.verify(item).setShortlink("foobar");
    }

    public void testOnError() throws JSONException {
        MockShareCallback cb = Mockito.mock(MockShareCallback.class);

        Throwable e = Mockito.mock(Throwable.class);

        Mockito.doCallRealMethod().when(cb).onError(e);

        cb.onError(e);
        Mockito.verify(cb).onResult(null, e);
    }

    public void testShowDialog() {

    }
}
