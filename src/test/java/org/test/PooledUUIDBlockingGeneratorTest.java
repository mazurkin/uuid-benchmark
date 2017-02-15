package org.test;

import com.fasterxml.uuid.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class PooledUUIDBlockingGeneratorTest {

    private PooledUUIDBlockingGenerator generator;

    @Before
    public void setUp() throws Exception {
        Logger.setLogLevel(Logger.LOG_ERROR_AND_ABOVE);
        generator = new PooledUUIDBlockingGenerator();
    }

    @After
    public void tearDown() throws Exception {
        generator.close();
    }

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 10_000_000; i++) {
            final UUID v = generator.generate();
            Assert.assertNotNull(v);
        }
    }


}