package com.mooreb.tde.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicStats {
    private final String statName;
    private final List<Double> numbers;
    private final int n;
    private final double min;
    private final double max;
    private final double sum;
    private final double mean;
    private final double median;
    private final double stddev;

    public BasicStats (final String statName, final List<Double> numbers) {
        if(null == numbers) {
            throw new IllegalArgumentException("cannot do basic stats on a null list of numbers");
        }
        this.statName = statName;
        this.numbers = new ArrayList<Double>(numbers);
        this.n = numbers.size();
        Collections.sort(this.numbers);
        min = this.numbers.get(0);
        max = this.numbers.get(n-1);
        double localsum=0;
        for(final Double d : numbers) {
            localsum += d;
        }
        sum = localsum;
        mean = sum/n;
        median = compute_median();
        stddev = compute_stddev();
    }

    private double compute_median() {
        if(0 == (n%2)) {
            // n is even
            final double firstHalf = this.numbers.get(n/2-1);
            final double secondHalf = this.numbers.get(n/2);
            return ((firstHalf+secondHalf)/2);
        }
        else {
            // n is odd
            return this.numbers.get(n/2);
        }
    }

    private double compute_stddev() {
        double sumOfSquareDiffs = 0.0;
        for(final double d : numbers) {
            double squareDiff = (d - mean)*(d - mean);
            sumOfSquareDiffs += squareDiff;
        }
        final double stddevSquared = sumOfSquareDiffs/n;
        return Math.sqrt(stddevSquared);
    }

    public String getStatName() {
        return statName;
    }

    public int getN() {
        return n;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getSum() {
        return sum;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getStddev() {
        return stddev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicStats that = (BasicStats) o;

        if (n != that.n) return false;
        if (Double.compare(that.min, min) != 0) return false;
        if (Double.compare(that.max, max) != 0) return false;
        if (Double.compare(that.sum, sum) != 0) return false;
        if (Double.compare(that.mean, mean) != 0) return false;
        if (Double.compare(that.median, median) != 0) return false;
        if (Double.compare(that.stddev, stddev) != 0) return false;
        if (statName != null ? !statName.equals(that.statName) : that.statName != null) return false;
        return numbers != null ? numbers.equals(that.numbers) : that.numbers == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = statName != null ? statName.hashCode() : 0;
        result = 31 * result + (numbers != null ? numbers.hashCode() : 0);
        result = 31 * result + n;
        temp = Double.doubleToLongBits(min);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(max);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sum);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(median);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stddev);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public String toString() {
        return "BasicStats{" +
                "statName='" + statName + '\'' +
                ", n=" + n +
                ", min=" + min +
                ", max=" + max +
                ", sum=" + sum +
                ", mean=" + mean +
                ", median=" + median +
                ", stddev=" + stddev +
                '}';
    }
}
