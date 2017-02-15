package org.test;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDType;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PooledUUIDBlockingGenerator extends NoArgGenerator implements AutoCloseable {

    private static final int CACHE_COUNT = 10_000_000;

    private final Thread thread;

    private final NoArgGenerator delegate;

    private final BlockingQueue<UUID> queue;

    public PooledUUIDBlockingGenerator() {
        this.delegate = Generators.timeBasedGenerator();

        // bounded blocking queue
        this.queue = new ArrayBlockingQueue<>(CACHE_COUNT);

        for (int i = 0; i < CACHE_COUNT; i++) {
            this.queue.add(delegate.generate());
        }

        this.thread = new Thread(this::loop);
        this.thread.setName("PooledUUIDBlockingGenerator generator thread");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    @Override
    public void close() throws Exception {
        if (this.thread.isAlive()) {
            this.thread.interrupt();
            this.thread.join(1000);
        }

        this.queue.clear();
    }

    @Override
    public UUIDType getType() {
        return delegate.getType();
    }

    private void loop() {
        while (!Thread.currentThread().isInterrupted()) {
            final UUID v = delegate.generate();

            try {
                queue.put(v);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public UUID generate() {
        final UUID v = queue.poll();

        if (v != null) {
            return v;
        } else {
            return fallback();
        }
    }

    private UUID fallback() {
        // System.err.printf("FALLBACK to the delegate");
        return delegate.generate();
    }
}
