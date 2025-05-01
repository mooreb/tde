package com.mooreb.tde.util;

import com.mooreb.tde.util.spreadsheet.MutableInMemorySpreadsheet;
import java.util.ArrayList;
import java.util.List;

public class BasicStatsUtils {

    public static void addBasicStatsTab(final MutableInMemorySpreadsheet ss, final List<BasicStats> basicStatsList) {
        ss.addSheet(getBasicStatsHeaders(), "basic stats");
        for (final BasicStats basicStats : basicStatsList) {
            addBasicStatToSheet(ss, basicStats);
        }
    }

    // Potential BUG: consider moving this into the BasicStats class
    private static void addBasicStatToSheet(
            final MutableInMemorySpreadsheet mutableInMemorySpreadsheet,
            final BasicStats basicStats
    ) {
        mutableInMemorySpreadsheet.addRow();
        mutableInMemorySpreadsheet.addCell(basicStats.getStatName());
        mutableInMemorySpreadsheet.addCell(basicStats.getN());
        mutableInMemorySpreadsheet.addCell(basicStats.getMin());
        mutableInMemorySpreadsheet.addCell(basicStats.getMax());
        mutableInMemorySpreadsheet.addCell(basicStats.getSum());
        mutableInMemorySpreadsheet.addCell(basicStats.getMean());
        mutableInMemorySpreadsheet.addCell(basicStats.getMedian());
        mutableInMemorySpreadsheet.addCell(basicStats.getStddev());
    }

    // Potential BUG: consider moving this into the BasicStats class
    private static List<String> getBasicStatsHeaders() {
        final List<String> basicStatsHeaders = new ArrayList<String>();
        basicStatsHeaders.add("stat name");
        basicStatsHeaders.add("n");
        basicStatsHeaders.add("min");
        basicStatsHeaders.add("max");
        basicStatsHeaders.add("sum");
        basicStatsHeaders.add("mean");
        basicStatsHeaders.add("median");
        basicStatsHeaders.add("stddev");
        return basicStatsHeaders;
    }
}
