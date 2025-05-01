package com.mooreb.tde.util.spreadsheet.mbmims;

import com.mooreb.tde.util.spreadsheet.POIBackedMutableInMemorySpreadsheet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateCell implements Cell {
    private final Date contents;

    public DateCell(final Date contents) {
        this.contents = contents;
    }

    @Override
    public String getContentsAsString() {
        final Instant instant = contents.toInstant();
        final DateTimeFormatter format = DateTimeFormatter.ISO_INSTANT;
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        final String retval = zonedDateTime.format(format);
        return retval;
    }

    @Override
    public void addCellTo(POIBackedMutableInMemorySpreadsheet mims) {
        mims.addCell(contents);
    }
}
