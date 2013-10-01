/*
 *
 *  * Copyright (c) 2013 ShareThis Inc.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.sharethis.loopy.test;

import android.app.AlertDialog;
import android.app.MockAlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.sharethis.loopy.sdk.*;
import com.sharethis.loopy.sdk.util.AppDataCache;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class ShareDialogAdapterTest extends LoopyActivityTestCase {

    public void testGetView() throws Throwable {

        final Context context = getContext();
        final MockShareClickListener shareDialogListener = Mockito.mock(MockShareClickListener.class);
        final AlertDialog dlg = Mockito.spy(new MockAlertDialog( context ));
        final MockShareConfig config = Mockito.mock(MockShareConfig.class);
        final Intent intent = Mockito.mock(Intent.class);
        final MockItem item = Mockito.mock(MockItem.class);
        final AppDataCache cache = Mockito.mock(AppDataCache.class);

        final String textLeft = "left_foobar";
        final String textRight = "right_foobar";
        final Drawable iconLeft = Mockito.mock(Drawable.class);
        final Drawable iconRight = Mockito.mock(Drawable.class);

        ResolveInfo left = new ResolveInfo() { @Override public String toString() { return "0"; } };
        ResolveInfo right = new ResolveInfo() { @Override public String toString() { return "1"; } };

        final ShareDialogRow row = new ShareDialogRow();
        row.left = left;
        row.right = right;

        Mockito.when(cache.getAppLabel(context, left)).thenReturn(textLeft);
        Mockito.when(cache.getAppLabel(context, right)).thenReturn(textRight);
        Mockito.when(cache.getAppIcon(context, left)).thenReturn(iconLeft);
        Mockito.when(cache.getAppIcon(context, right)).thenReturn(iconRight);

        MockShareDialogAdapter adapter = new MockShareDialogAdapter(getContext(), Arrays.asList(row)) {
            @Override
            public AppDataCache getAppDataCache() {
                return cache;
            }
        };

        adapter.setDialog(dlg);
        adapter.setConfig(config);
        adapter.setShareIntent(intent);
        adapter.setShareItem(item);

        adapter.setOnShareClickListener(shareDialogListener);

        View view = adapter.getView(0, null, null);

        assertNotNull(view);

        Object tag = view.getTag();

        assertNotNull(tag);
        assertTrue(tag instanceof ShareDialogAdapter.ViewHolder);

        final ShareDialogAdapter.ViewHolder holder = (ShareDialogAdapter.ViewHolder) tag;

        assertNotNull(holder.leftText);
        assertNotNull(holder.leftIcon);
        assertNotNull(holder.leftLayout);
        assertNotNull(holder.rightText);
        assertNotNull(holder.rightIcon);
        assertNotNull(holder.rightLayout);

        assertEquals(textLeft, holder.leftText.getText().toString());
        assertEquals(textRight, holder.rightText.getText().toString());
        assertSame(iconLeft, holder.leftIcon.getDrawable());
        assertSame(iconRight, holder.rightIcon.getDrawable());

        final CountDownLatch latch = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(holder.leftLayout.performClick());
                assertTrue(holder.rightLayout.performClick());
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        Mockito.verify(shareDialogListener).onClick(dlg, left, config, item, intent);
        Mockito.verify(shareDialogListener).onClick(dlg, right, config, item, intent);
    }
}
