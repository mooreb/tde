package com.mooreb.tde.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalTime;

import static java.lang.Double.NaN;

public class TimeUtils {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void watchTheClock(final String label, final long startTimeMillis, final String unitOfWorkLabel, final String unitOfWork, final double percentDone) {
        final long currentTime = System.currentTimeMillis();
        final long elapsedTimeMillis = currentTime - startTimeMillis;
        final double elapsedTimeMinutes = elapsedTimeMillis / 1000.0 / 60.0;
        final double elapsedTimeHours = elapsedTimeMinutes / 60;
        double estTimeMinutes = NaN;
        double estTimeHours = NaN;
        if (percentDone > 0.0) {
            estTimeMinutes = elapsedTimeMinutes / percentDone;
            estTimeHours = estTimeMinutes / 60;
        }
        final double remainingMinutes = estTimeMinutes - elapsedTimeMinutes;
        final double remainingHours = remainingMinutes / 60;
        LOG.info("{}: working on {} {}; {} complete", label, unitOfWorkLabel, unitOfWork, StringUtils.myPercent(percentDone));
        LOG.info("{}: elapsed time: {} minutes ({} hours) ; estimated required time {} minutes ({} hours) ; estimated remaining time {} minutes, ({} hours)",
                label,
                StringUtils.my62(elapsedTimeMinutes), StringUtils.my62(elapsedTimeHours),
                StringUtils.my62(estTimeMinutes), StringUtils.my62(estTimeHours),
                StringUtils.my62(remainingMinutes), StringUtils.my62(remainingHours));
        if (percentDone > 0.0) {
            LOG.info("{}: ETA: {}", label, LocalTime.now().plusSeconds((long) (remainingMinutes * 60)));
        }
    }

    public static void watchTheClock(final String label, final long startTimeMillis, final String unitOfWorkLabel, final String unitOfWork, final int thisCount, final int totalCount) {
        final double percentDone = (thisCount - 1.0) / totalCount;
        final String enhancedLabel = label + ": " + unitOfWork + " " + thisCount + " of " + totalCount;
        watchTheClock(enhancedLabel, startTimeMillis, unitOfWorkLabel, unitOfWork, percentDone);
    }
}
