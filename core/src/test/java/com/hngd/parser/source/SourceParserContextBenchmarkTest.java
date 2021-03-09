package com.hngd.parser.source;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class SourceParserContextBenchmarkTest {

    @Test
    public void test() throws RunnerException {
        Options opts=new OptionsBuilder()
                .include(SourceParserContextBenchmarkTest.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opts).run();
                
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testWithCached() {
        
        CachedSourceParserContext c=new CachedSourceParserContext("test-output");
        c.initSourceInJar(Arrays.asList(new File("./test-data/postgresql-42.2.16-sources.jar")));
        c.initSource(new File("./src/test/java"));
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testWithoutCache() {
        
        SourceParserContext c=new SourceParserContext();
        c.initSourceInJar(Arrays.asList(new File("./test-data/postgresql-42.2.16-sources.jar")));
        c.initSource(new File("./src/test/java"));
    }
}
