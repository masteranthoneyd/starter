package com.youngboss.util;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;


/**
 * @author ybd
 * @date 18-8-10
 * @contact yangbingdong1994@gmail.com
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 4, time = 2)
@Threads(10)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SnowflakeTest {
	private static final Snowflake[] SNOWFLAKES = IntStream.rangeClosed(1, 8)
														   .mapToObj(Snowflake::create)
														   .toArray(value -> new Snowflake[8]);

	private static final AtomicLong ATOMIC_LONG = new AtomicLong(0);

	@Benchmark
	public long getId() {
		return SNOWFLAKES[(int) (ATOMIC_LONG.incrementAndGet() & (1 << 3) - 1)].nextId();
	}


	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(SnowflakeTest.class.getSimpleName())
											  .build();
		new Runner(options).run();
	}
}
