package com.mooreb.tde.util.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Row {
    private final Map<String,String> values;

    public Row(final Map<String,String>values) {
        this.values = values;
    }

    public String getContentsUpper(final String header) {
        if(null == values) return null;
        if(null == header) return null;
        if(!header.equals(header.toUpperCase())) throw new IllegalArgumentException("expected header to be upper case: " + header);
        return getStringContents(header);
    }

    public String getContentsLower(final String header) {
        if(null == values) return null;
        if(null == header) return null;
        if(!header.equals(header.toLowerCase())) throw new IllegalArgumentException("expected header to be lower case: " + header);
        return getStringContents(header);
    }

    public String getContentsMixed(final String header) {
        return getStringContents(header);
    }

    private String getStringContents(final String header) {
        if(null == values) return null;
        return values.get(header);
    }

    public double getDoubleContents(final String header) {
        return Double.parseDouble(values.get(header));
    }

    public int getIntegerContents(final String header) {
        return Integer.parseInt(values.get(header));
    }

    public double getPercentContents(final String header) {
        String value = values.get(header);
        value = value.replace("%", "");
        double d = Double.parseDouble(value);
        return d/100;
    }

    protected Map<String, String> getValues() {
        return values;
    }

    public List<String> getColumnsValued(final String needle) {
        if(null == needle) throw new NullPointerException("not valid to search for null needle");
        if(StringUtils.isEmpty(needle)) throw new IllegalArgumentException("not valid to search for empty needle");
        final List<String> retval = new ArrayList<>();
        for(final Map.Entry<String,String> entry : values.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if(needle.equals(value)) {
                retval.add(key);
            }
        }
        Collections.sort(retval); // potential BUG: sorted alphabetically instead of by containing spreadsheet column order
        return Collections.unmodifiableList(retval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        return values != null ? values.equals(row.values) : row.values == null;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Row{" +
                "values=" + values +
                '}';
    }
}
