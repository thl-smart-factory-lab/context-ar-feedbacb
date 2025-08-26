package com.example.printerstatus_glass;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

public class DataHolderTest {
    @Test
    public void testThreadSafeUpdates() {
        DataHolder holder = DataHolder.getInstance();
        Map<String, String> testData = Map.of("key", "value");

        new Thread(() -> holder.updatePrinterData(testData)).start();
        new Thread(() -> holder.updateUwbData(testData)).start();

        assertNotNull(holder.getPrinterData());
    }
}
