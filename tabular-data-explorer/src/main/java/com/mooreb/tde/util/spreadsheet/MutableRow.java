package com.mooreb.tde.util.spreadsheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MutableRow {
    private final Row rowToMutate;
    private final Set<String> columnsToDelete = new HashSet<>();
    private final Map<String, String> additionalValues = new HashMap<>();

    public MutableRow(Row row) {
        this.rowToMutate = row;
    }

    public void deleteColumn(final String header) {
        columnsToDelete.add(header);
        additionalValues.remove(header);
    }

    public void addKV(final String header, final String value) {
        columnsToDelete.remove(header);
        additionalValues.put(header, value);
    }

    public String getContents(final String header) {
        if(columnsToDelete.contains(header)) return null;
        if(additionalValues.containsKey(header)) {
            return additionalValues.get(header);
        }
        return rowToMutate.getContentsUpper(header);
    }

    private Map<String, String> resolve() {
        final Map<String, String> retval = new HashMap<>(rowToMutate.getValues());
        for(final String key : additionalValues.keySet()) {
            retval.put(key, additionalValues.get(key));
        }
        for(final String key : columnsToDelete) {
            retval.remove(key);
        }
        return retval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutableRow that = (MutableRow) o;
        return that.resolve().equals(this.resolve());
    }

    @Override
    public int hashCode() {
        return resolve().hashCode();
    }

    @Override
    public String toString() {
        return resolve().toString();
    }
}
