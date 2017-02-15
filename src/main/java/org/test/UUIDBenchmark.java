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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@Threads(256)
@State(Scope.Benchmark)
@Warmup(iterations = 1, time = 60, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 60, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 120, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UUIDBenchmark {

    private NoArgGenerator randomGenerator;

    private NoArgGenerator timeGenerator;

    public UUIDBenchmark() {
        Logger.setLogLevel(Logger.LOG_ERROR_AND_ABOVE);

        this.randomGenerator = Generators.randomBasedGenerator();
        this.timeGenerator = Generators.timeBasedGenerator();
    }

    @Benchmark
    public UUID testTimedUUID() {
        return timeGenerator.generate();
    }

    @Benchmark
    public UUID testRandomUUID() {
        return randomGenerator.generate();
    }

    @Benchmark
    public UUID testJavaUtilUUID() {
        return UUID.randomUUID();
    }

    public static void main(String[] args) throws RunnerException {
        // this runner and options are for debug purpose only
        Options opt = new OptionsBuilder()
                .include(UUIDBenchmark.class.getSimpleName())
                .forks(0)
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
