package com.mooreb.tde.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class StringUtilsTest {
    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Test(groups = "unit")
    public void justZero() {
        final String s = StringUtils.stripLeadingZeroes("0");
        Assert.assertEquals(s, "0");
    }

    @Test(groups = "unit")
    public void twoZeroes() {
        final String s = StringUtils.stripLeadingZeroes("00");
        Assert.assertEquals(s, "0");
    }

    @Test(groups = "unit")
    public void threeZeroes() {
        final String s = StringUtils.stripLeadingZeroes("000");
        Assert.assertEquals(s, "0");
    }

    @Test(groups = "unit")
    public void fiveZeroes() {
        final String s = StringUtils.stripLeadingZeroes("00000");
        Assert.assertEquals(s, "0");
    }

    @Test(groups = "unit")
    public void blank() {
        final String s = StringUtils.stripLeadingZeroes("");
        Assert.assertEquals(s, "");
    }

    @Test(groups = "unit")
    public void zeroOneHundred() {
        final String s = StringUtils.stripLeadingZeroes("0100");
        Assert.assertEquals(s, "100");
    }

    @Test(groups = "unit")
    public void stripLeadingZeroes() {
        final String s = StringUtils.stripLeadingZeroes("0000100033");
        Assert.assertEquals(s, "100033");
    }

    @Test(groups = "unit")
    public void lotsOfZeroesOne() {
        final String s = StringUtils.stripLeadingZeroes("00000001");
        Assert.assertEquals(s, "1");
    }

    @Test(groups = "unit")
    public void oneTwoThreeFourFive() {
        final String s = StringUtils.stripLeadingZeroes("12345");
        Assert.assertEquals(s, "12345");
    }

    @Test(groups = "unit")
    public void testForNull() {
        final String s = null;
        Assert.assertEquals(StringUtils.stripLeadingZeroes(s), null);
    }

    @Test(groups = "unit")
    public void testSafeTrim1() {
        final String s = null;
        final String t = StringUtils.safetrim(s);
        Assert.assertNull(t);
    }

    @Test(groups = "unit")
    public void testSafeTrim2() {
        final String s = "";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "");
    }

    @Test(groups = "unit")
    public void testSafeTrim3() {
        final String s = " ";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "");
    }

    @Test(groups = "unit")
    public void testSafeTrim4() {
        final String s = "  ";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "");
    }

    @Test(groups = "unit")
    public void testSafeTrim5() {
        final String s = " foo ";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "foo");
    }

    @Test(groups = "unit")
    public void testSafeTrim6() {
        final String s = "foo ";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "foo");
    }

    @Test(groups = "unit")
    public void testSafeTrim7() {
        final String s = " foo";
        final String t = StringUtils.safetrim(s);
        Assert.assertEquals(t, "foo");
    }

    @Test(groups = "unit")
    public void testIsObsolete1() {
        Assert.assertTrue(StringUtils.isObsolete("20150320", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete2() {
        Assert.assertFalse(StringUtils.isObsolete("00000000", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete3() {
        Assert.assertFalse(StringUtils.isObsolete("        ", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete4() {
        Assert.assertFalse(StringUtils.isObsolete("", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete5() {
        Assert.assertFalse(StringUtils.isObsolete("unparsable as a date", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete6() {
        final String s = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Assert.assertFalse(StringUtils.isObsolete(s, 3));
    }

    // if this test is still running in 984 years, and you've found it, failing,
    // please delete it; it's harmless.
    @Test(groups = "unit")
    public void testIsObsolete7() {
        Assert.assertFalse(StringUtils.isObsolete("29991231", 3));
    }

    @Test(groups = "unit")
    public void testIsObsolete8() {
        Assert.assertFalse(StringUtils.isObsolete("20150101", "20150101"));
    }

    @Test(groups = "unit")
    public void testIsObsolete9() {
        Assert.assertTrue(StringUtils.isObsolete("20141231", "20150101"));
    }

    @Test(groups = "unit")
    public void myPercentTest() {
        Assert.assertEquals(StringUtils.myPercent(.50), " 50.00%");
        Assert.assertEquals(StringUtils.myPercent(.77769), " 77.77%");
        Assert.assertEquals(StringUtils.myPercent(0), "  0.00%");
        Assert.assertEquals(StringUtils.myPercent(0.09), "  9.00%");
        Assert.assertEquals(StringUtils.myPercent(.10), " 10.00%");
        Assert.assertEquals(StringUtils.myPercent(1), "100.00%");
    }

    @Test(groups = "unit")
    public void myInstantTest() {
        LOG.info("now is {}", StringUtils.myInstant());
    }

    @Test(groups = "unit")
    public void splitTest() {
        final String one = "Now is the time for all good men to come to the aid of their party. ";
        final StringBuffer sb = new StringBuffer();
        for(int i=0; i<100; i++) {
            sb.append(one);
        }
        final String longText = sb.toString();
        String[] chunks = StringUtils.split(longText, 30);
        for (int i=0; i < chunks.length -1; i++) {
            LOG.debug("lines["+i+"]: (len: "+chunks[i].length()+") : "+chunks[i]);
            Assert.assertEquals( chunks[i].length(), 30, "expect initial lines to have 30 length");
        }
        Assert.assertEquals(chunks[chunks.length-1].length(), 20, "expect last line to have length of 20");
    }

    @Test(groups = "unit", expectedExceptions = NumberFormatException.class)
    public void parsePercent() {
        final Double one = Double.parseDouble("100.00%");
        Assert.assertEquals(one, 1);
    }

    @Test(groups = "unit")
    public void parseMyPercent1() {
        final double one = StringUtils.parseMyPercent("100.00%");
        Assert.assertEquals(one, 1.0);
    }

    @Test(groups = "unit")
    public void parseMyPercent2() {
        final double d = StringUtils.parseMyPercent(" 77.77%");
        Assert.assertEquals(d, .7777);
    }

    @Test(groups = "unit")
    public void isReplaceSane() {
        final String s = "aaa";
        Assert.assertEquals(s.replace("aa", "b"), "ba");
    }

    @Test(groups = "unit")
    public void testPatternDotQuote() {
        Assert.assertEquals(Pattern.quote("?"), "\\Q?\\E");
    }

    @Test(groups = "unit")
    public void testNakedQuestionMark() {
        final String s = "W?.";
        Assert.assertEquals(s.replace("?", "_q_"), "W_q_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest1() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W?."), "W_q_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest2() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W\\."), "W_b_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest3() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W/."), "W_s_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest4() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W*."), "W_a_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest5() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W[."), "W_o_.");
    }

    @Test(groups = "unit")
    public void makeSafeForExcelTabNameTest6() {
        Assert.assertEquals(StringUtils.makeSafeForExcelTabName("W]."), "W_e_.");
    }

    @Test(groups = "unit")
    public void chunkTest() {
        final String[] chunks = StringUtils.split("", 32767);
        Assert.assertTrue(0 == chunks.length);
    }

    @Test(groups = "unit")
    public void testReplaceNULWithQuestionMark() {
        final String nul = StringUtils.getASCIINUL();
        Assert.assertEquals(nul.length(), 1, "expect a string with length one");
        final String hopefulQuestionMark = StringUtils.replaceNULWithQuestionMark(nul);
        Assert.assertEquals(hopefulQuestionMark, "?");
        final String nulabcdenulfgh = nul + "abcde" + nul + "fgh";
        Assert.assertEquals(StringUtils.replaceNULWithQuestionMark(nulabcdenulfgh), "?abcde?fgh");
    }

    @Test(groups = "unit")
    public void testMyDay() {
        final String myDay = StringUtils.myDay();
        Assert.assertNotNull(myDay);
        final int myDayAsInt = Integer.parseInt(myDay);
        Assert.assertTrue(myDayAsInt >= 20190912);
    }

    @Test(groups = "unit")
    public void testMax1() {
        final String zero = "000";
        final String ten = "010";
        Assert.assertEquals(ten, StringUtils.max(zero, ten));
    }

    @Test(groups = "unit")
    public void testMax2() {
        final String threeSixtyFive = "365";
        final String ten = "010";
        Assert.assertEquals(threeSixtyFive, StringUtils.max(threeSixtyFive, ten));
    }

    @Test(groups = "unit")
    public void testPad1() {
        final String s = "90";
        final String desiredS = "090";
        final String actual = StringUtils.padWithZeroes(s, 3);
        Assert.assertEquals(desiredS, actual);
    }

    @Test(groups = "unit")
    public void testPad2() {
        final String s = "0";
        final String desiredS = "000";
        final String actual = StringUtils.padWithZeroes(s, 3);
        Assert.assertEquals(desiredS, actual);
    }

    @Test(groups = "unit")
    public void testPad3() {
        final String s = "365";
        final String desiredS = "365";
        final String actual = StringUtils.padWithZeroes(s, 3);
        Assert.assertEquals(desiredS, actual);
    }

    @Test(groups = "unit")
    public void testForNDC59630062830() {
        final boolean isObsolete = StringUtils.isObsolete("20171231", "20170101");
        Assert.assertFalse(isObsolete);
    }
}
