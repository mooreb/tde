package com.mooreb.tde.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils {

    public static boolean isEmpty( final String s ) {
        return((null == s) || (s.trim().isEmpty()));
    }

    public static String stripLeadingZeroes(final String s) {
        if(null == s) return null;
        final int len = s.length();
        if(0 == len) return s;
        int end=0;
        for(int i=0; i<len; i++) {
            if('0' != s.charAt(i)) {
                break;
            }
            end++;
        }
        if(len == end) {
            return "0";
        }
        else if(end > 0) {
            return s.substring(end,len);
        }
        else {
            return s;
        }
    }
    public static String safetrim(final String s) {
        if(null == s) return null;
        return s.trim();
    }

    public static boolean safeEquals(final String a, final String b) {
        if(null == a) {
            return (null == b);
        }
        else {
            // a not null
            return a.equals(b);
        }
    }

    // Potential BUG: we've baked in some application logic (obsolete == n years ago)
    // It seems harmless enough, but, if you're reading this, you know how these things go...
    public static boolean isObsolete(final String s, final int obsoleteYears) {
        if("00000000".equals(s)) return false;
        try {
            final LocalDate timestamp = LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
            final LocalDate today = LocalDate.now();
            final LocalDate threeYearsAgo = today.minusYears(obsoleteYears);
            final int cmp = timestamp.compareTo(threeYearsAgo);
            return (cmp < 0);
        }
        catch(DateTimeParseException e) {
            return false;
        }
    }

    // Potential BUG: we've baked in some application logic (obsolete == n years ago)
    // It seems harmless enough, but, if you're reading this, you know how these things go...
    public static boolean isObsolete(final String questionableDate, final String obsoleteReferenceDate) {
        if("00000000".equals(questionableDate)) return false;
        try {
            final LocalDate timestamp = LocalDate.parse(questionableDate, DateTimeFormatter.BASIC_ISO_DATE);
            final LocalDate obsoleteBeforeThisDate = LocalDate.parse(obsoleteReferenceDate, DateTimeFormatter.BASIC_ISO_DATE);
            final int cmp = timestamp.compareTo(obsoleteBeforeThisDate);
            return (cmp < 0);
        }
        catch(DateTimeParseException e) {
            return false;
        }
    }

    public static String myPercent(double d) {
        final String retval = String.format("%6.2f%%", d*100);
        return retval;
    }

    public static String my62(double d) {
        final String retval = String.format("%6.2f", d);
        return retval;
    }

    public static double parseMyPercent(final String p) {
        if(null == p) throw new IllegalArgumentException("p cannot be null");
        if(!p.endsWith("%")) throw new IllegalArgumentException("p must end in a percent sign");
        final int len = p.length();
        final String s = p.substring(0, len-1);
        double d = Double.parseDouble(s);
        return (d/100);
    }

    public static String myInstant() {
        final ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nx");
        return now.format(dateTimeFormatter);
    }

    public static String myDay() {
        final ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return now.format(dateTimeFormatter);
    }

    public static String[] split(String text, int chunkSize) {
        final char[] data = text.toCharArray();
        final int remainder = data.length % chunkSize;
        final int extraChunk = ((0 == remainder) ? 0 : 1);
        final int numChunks = data.length/chunkSize + extraChunk;
        final String[] retval = new String[numChunks];
        int begin = 0;
        int pos = 0;
        int remaining = data.length;
        while(remaining > 0) {
            final int numCharsToCopy = Math.min(remaining, chunkSize);
            retval[pos++] = new String(data, begin, numCharsToCopy);
            begin += numCharsToCopy;
            remaining -= numCharsToCopy;
        }
        return retval;
    }


/*
    illegal characters in excel tab names: \ / ? * [ ]
 */
    public static String makeSafeForExcelTabName(final String s) {
        return s.replace("?", "_q_")
                .replace(":", "_c_")
                .replace("/", "_s_")
                .replace("\\", "_b_")
                .replace("*", "_a_")
                .replace("[", "_o_")
                .replace("]", "_e_");
    }

    public static String getASCIINUL() {
        final char[] charArray = new char[1];
        charArray[0] = 0;
        final String asciiNUL = new String(charArray);
        return asciiNUL;
    }

    public static String getASCIISOH() {
        final char[] charArray = new char[1];
        charArray[0] = 1;
        final String asciiSOH = new String(charArray);
        return asciiSOH;
    }

    public static String getASCIISTX() {
        final char[] charArray = new char[1];
        charArray[0] = 2;
        final String asciiSOH = new String(charArray);
        return asciiSOH;
    }

    public static String getASCIIETX() {
        final char[] charArray = new char[1];
        charArray[0] = 3;
        final String asciiSOH = new String(charArray);
        return asciiSOH;
    }

    public static String generateUniqueExcelName(final String prefix) {
        return prefix + "-" + myInstant() + ".xlsx";
    }

    public static String generateUniqueTildeTextName(final String prefix) {
        return prefix + "-" + myInstant() + ".txt";
    }

    public static String replaceNULWithQuestionMark(final String s) {
        if(null == s) return null;
        return s.replaceAll("\0", "?");
    }

    public static String replaceNULWithSpace(final String s) {
        if(null == s) return null;
        return s.replaceAll("\0", " ");
    }

    public static String maxLen(final String s, final int maxLength) {
        if(null == s) return null;
        if(s.length() < maxLength) return s;
        return s.substring(0, maxLength);
    }

    public static String max(final String... strings) {
        int maxI = Integer.MIN_VALUE;
        String maxS = null;
        for(final String candidate : strings) {
            if(null == candidate) continue;
            int candidateI =  Integer.parseInt(candidate, 10);
            if(candidateI > maxI) {
                maxS = candidate;
                maxI = candidateI;
            }
        }
        return maxS;
    }

    /**
     *
     * @param a
     * @param b
     * @return true if a is greater than b
     */
    public static boolean greaterThan(final String a, final String b) {
        if((null == a) && (null == b)) return false;
        if(null == b) return true;
        final int aInt = Integer.parseInt(a, 10);
        final int bInt = Integer.parseInt(b, 10);
        return (aInt > bInt);
    }

    /**
     *
     * @param a
     * @param b
     * @return true if a is greater than, or equal to, b
     */
    public static boolean greaterThanOrEqualTo(final String a, final String b) {
        if((null == a) && (null == b)) return false;
        if(null == b) return true;
        final int aInt = Integer.parseInt(a, 10);
        final int bInt = Integer.parseInt(b, 10);
        return (aInt >= bInt);
    }

    public static String padWithZeroes(String s, int desiredWidth) {
        int width = s.length();
        while(width < desiredWidth) {
            s = "0" + s;
            width++;
        }
        return s;
    }

    public static boolean onlySpacesAndDigits(final String s) {
        final int len = s.length();
        final CharSequence sequence = s.subSequence(0, len);
        for(int i=0; i<len; i++) {
            final char c = sequence.charAt(i);
            if(Character.isWhitespace(c)) continue;
            if(Character.isDigit(c)) continue;
            return false;
        }
        return true;
    }

    public static Set<String> setOf(String ... varargs) {
        final Set<String> retval = new HashSet<>();
        for(final String s : varargs) {
            retval.add(s);
        }
        return Collections.unmodifiableSet(retval);
    }

    public static List<String> listOf(String ... varargs) {
        final List<String> retval = new ArrayList<>();
        for(final String s : varargs) {
            retval.add(s);
        }
        return Collections.unmodifiableList(retval);
    }

}
