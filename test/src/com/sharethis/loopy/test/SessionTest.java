package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.Config;
import com.sharethis.loopy.sdk.LoopyState;
import com.sharethis.loopy.sdk.MockSession;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
public class SessionTest extends LoopyAndroidTestCase {

    public void testStart() {

        final LoopyState state = Mockito.mock(LoopyState.class);
        final Config config = Mockito.mock(Config.class);

        MockSession session = new MockSession() {

            @Override
            public LoopyState newLoopyState() {
                return state;
            }

            @Override
            public Config newConfig() {
                return config;
            }
        };

        session.start(getContext());
        session.waitForStart();

        Mockito.verify(state).load(getContext());
        Mockito.verify(config).load(getContext());
    }
}
