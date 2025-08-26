package com.example.printerstatus_glass;

import org.junit.Test;
import static org.junit.Assert.*;

public class PrinterTopicSelectorTest {
    @Test
    public void testSelectPrinterTopic_RegionA() {
        String topic = PrinterTopicSelector.selectPrinterTopic(0.5, 3.0);
        assertEquals("sf/printer/a", topic);
    }

    @Test
    public void testSelectPrinterTopic_RegionB() {
        String topic = PrinterTopicSelector.selectPrinterTopic(0.5, 2.0);
        assertEquals("sf/printer/b", topic);
    }

    @Test
    public void testSelectPrinterTopic_RegionC() {
        String topic = PrinterTopicSelector.selectPrinterTopic(6.0, 3.0);
        assertEquals("sf/printer/c", topic);
    }

    @Test
    public void testSelectPrinterTopic_Boarder() {
        String topic = PrinterTopicSelector.selectPrinterTopic(1, 2.6);
        assertEquals("sf/printer/b", topic);
    }

    @Test
    public void testSelectPrinterTopic_InvalidRegion() {
        String topic = PrinterTopicSelector.selectPrinterTopic(10.0, 10.0);
        assertNull(topic);
    }
}