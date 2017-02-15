package org.test;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDType;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PooledUUIDLiveGenerator extends NoArgGenerator implements AutoCloseable {

    private static final int CACHE_COUNT = 10_000_000;

    private final Thread thread;

    private final NoArgGenerator delegate;

    private final Queue<UUID> queue;

    private final AtomicInteger counter;

    public PooledUUIDLiveGenerator() {
        this.delegate = Generators.timeBasedGenerator();

        // unbounded CAS queue with standalone counter
        this.queue = new ConcurrentLinkedQueue<>();
        this.counter = new AtomicInteger(CACHE_COUNT);

        for (int i = 0; i < CACHE_COUNT; i++) {
            this.queue.add(delegate.generate());
        }

        this.thread = new Thread(this::loop);
        this.thread.setName("PooledUUIDLiveGenerator generator thread");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    @Override
    public void close() throws Exception {
        this.thread.interrupt();
        this.thread.join(1000);
    }

    @Override
    public UUIDType getType() {
        return delegate.getType();
    }

    private void loop() {
        while (!Thread.currentThread().isInterrupted()) {
            if (counter.get() < CACHE_COUNT) {
                final UUID v = delegate.generate();
                queue.add(v);

                counter.incrementAndGet();
            } else {
                // we are expecting that CACHE_COUNT items can't be consumed in 10 milliseconds
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    @Override
    public UUID generate() {
        UUID v;

        // live loop until we get some UUID or thread is dead
        do {
            v = queue.poll();
        } while (v == null && thread.isAlive());

        if (v != null) {
            counter.decrementAndGet();
            return v;
        } else {
            return fallback();
        }
    }

    private UUID fallback() {
        System.err.printf("FALLBACK to the delegate");
        return delegate.generate();
    }

}
