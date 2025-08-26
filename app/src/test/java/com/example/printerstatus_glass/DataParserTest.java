package com.example.printerstatus_glass;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class DataParserTest {
    @Test
    public void testParseData() {
        String printerInput = "printer_status,printer_name=a bed_temperature_target=60.0,bed_temperature_current=23.12,tool_temperature_target=170.0,tool_temperature_current=26.0,state=\"Connected and Operational\",print_time=0,print_time_left=1593,job_name=\"spachtle-printables V7_90%_0.4n_0.2mm_PLA_MK3.5_27m.gcode\",is_connected=True,detailed_state=\"Printing\",completion=27.394086629153374";
        String uwbInput = "position,tagName=uwb-a positionX=5.02,positionY=2.86,positionZ=0";

        Map<String, String> printerResult = DataParser.parsePrinterData(printerInput);
        Map<String, String> uwbResult = DataParser.parseUwbData(uwbInput);

        assertEquals("a", printerResult.get("printer_name"));
        assertEquals("60.0", printerResult.get("bed_temperature_target"));
        assertEquals("23.12", printerResult.get("bed_temperature_current"));
        assertEquals("170.0", printerResult.get("tool_temperature_target"));
        assertEquals("26.0", printerResult.get("tool_temperature_current"));
        assertEquals("Connected and Operational", printerResult.get("state"));
        assertEquals("1593", printerResult.get("print_time_left"));
        assertEquals("27.394086629153374", printerResult.get("completion"));
        assertEquals("spachtle-printables V7_90%_0.4n_0.2mm_PLA_MK3.5_27m.gcode", printerResult.get("job_name"));

        assertEquals("5.02", uwbResult.get("positionX"));
        assertEquals("2.86", uwbResult.get("positionY"));
    }

    @Test
    public void testInvalidData() {
        Map<String, String> printerResult = DataParser.parsePrinterData("invalid_format");
        assertTrue(printerResult.isEmpty());

        Map<String, String> uwbResult = DataParser.parseUwbData("invalid_format");
        assertTrue(uwbResult.isEmpty());
    }
}
