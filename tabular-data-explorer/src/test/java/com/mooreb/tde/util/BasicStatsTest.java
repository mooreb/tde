package com.mooreb.tde.util;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BasicStatsTest {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test(groups = "unit")
    public void identicalOnes() {
        final List<Double> l = new ArrayList<Double>();
        for (int i = 0; i < 15; i++) {
            l.add(1.0);
        }
        final BasicStats basicStats = new BasicStats("identicalOnes", l);
        LOG.info("{}", basicStats);
        Assert.assertEquals(basicStats.getN(), 15);
        Assert.assertEquals(basicStats.getMin(), 1.0);
        Assert.assertEquals(basicStats.getMax(), 1.0);
        Assert.assertEquals(basicStats.getSum(), 15.0);
        Assert.assertEquals(basicStats.getMean(), 1.0);
        Assert.assertEquals(basicStats.getMedian(), 1.0);
        Assert.assertEquals(basicStats.getStddev(), 0.0);
    }

    // https://www.khanacademy.org/math/probability/data-distributions-a1/summarizing-spread-distributions/a/calculating-standard-deviation-step-by-step
    @Test(groups = "unit")
    public void khan() {
        final List<Double> l = new ArrayList<Double>();
        l.add(6.0);
        l.add(2.0);
        l.add(3.0);
        l.add(1.0);
        final BasicStats basicStats = new BasicStats("khan", l);
        LOG.info("{}", basicStats);
        Assert.assertEquals(basicStats.getN(), 4);
        Assert.assertEquals(basicStats.getMin(), 1.0);
        Assert.assertEquals(basicStats.getMax(), 6.0);
        Assert.assertEquals(basicStats.getSum(), 12.0);
        Assert.assertEquals(basicStats.getMean(), 3.0);
        Assert.assertEquals(basicStats.getMedian(), 2.5);
        Assert.assertEquals(basicStats.getStddev(), 1.8708286933869707);
    }
}
