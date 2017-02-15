package org.test;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.Logger;
import com.fasterxml.uuid.NoArgGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Fork(1)
@Threads(256)
@State(Scope.Benchmark)
@Warmup(iterations = UUIDBenchmark.ITERATION_COUNT, time = UUIDBenchmark.ITERATION_SEC, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = UUIDBenchmark.ITERATION_COUNT, time = UUIDBenchmark.ITERATION_SEC, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 5 * UUIDBenchmark.ITERATION_SEC, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UUIDBenchmark {

    public static final int ITERATION_COUNT = 1;

    public static final int ITERATION_SEC = 60;

    private NoArgGenerator randomGenerator1;

    private NoArgGenerator randomGenerator2;

    private NoArgGenerator timeGenerator;

    private PooledUUIDBlockingGenerator pooledBlockingGenerator;

    private PooledUUIDLiveGenerator pooledLiveGenerator;

    public UUIDBenchmark() {
        // custom logger in com.fasterxml.*
        Logger.setLogLevel(Logger.LOG_ERROR_AND_ABOVE);
    }

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        // java.security.SecureRandom is behind the scene by default
        this.randomGenerator1 = Generators.randomBasedGenerator();
        // shared java.util.Random is used
        this.randomGenerator2 = Generators.randomBasedGenerator(new Random());

        this.timeGenerator = Generators.timeBasedGenerator();

        this.pooledBlockingGenerator = new PooledUUIDBlockingGenerator();
        this.pooledLiveGenerator = new PooledUUIDLiveGenerator();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        this.pooledBlockingGenerator.close();
        this.pooledLiveGenerator.close();
    }

    @Benchmark
    public UUID testTimeBasedUUID() {
        return timeGenerator.generate();
    }

    @Benchmark
    public UUID testRandomBased1UUID() {
        return randomGenerator1.generate();
    }

    @Benchmark
    public UUID testRandomBased2UUID() {
        return randomGenerator2.generate();
    }

    @Benchmark
    public UUID testBlockingPooledUUID() {
        return pooledBlockingGenerator.generate();
    }

//    @Benchmark
//    public UUID testLivePooledUUID() {
//        return pooledLiveGenerator.generate();
//    }

    @Benchmark
    public UUID testJavaUtilUUID() {
        return UUID.randomUUID();
    }

    public static void main(String[] args) throws RunnerException {
        // this runner and options are for debug purpose only
        Options opt = new OptionsBuilder()
                .include(UUIDBenchmark.class.getSimpleName())
                .forks(1)
                .threads(64)
                .mode(Mode.Throughput)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(1)
                .measurementTime(TimeValue.seconds(3))
                .timeUnit(TimeUnit.MILLISECONDS)
                .build();

        new Runner(opt).run();
    }

}
