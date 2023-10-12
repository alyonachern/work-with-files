package alyonachern;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FileParsingTest {

    ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    public void readZipFileTest() throws Exception {
        try (ZipInputStream zip = new ZipInputStream(cl.getResourceAsStream("archive.zip"))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.contains("Soft.pdf")) {
                    PDF pdf = new PDF(zip);
                    assertAll(
                            () -> assertNotNull(pdf.content),
                            () -> assertEquals("Why Software Sucks.book", pdf.title,
                                    "Title of PDF is different"),
                            () -> assertEquals(244, pdf.numberOfPages,
                                    "Number of pages is different")
                    );
                } else if (name.contains("Birthday.csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zip, StandardCharsets.UTF_8));
                    List<String[]> content = reader.readAll();
                    final String[] firstRaw = content.get(0);
                    final String[] secondRaw = content.get(1);
                    final String[] thirdRaw = content.get(2);
                    final String[] fourthRaw = content.get(3);
                    assertAll(
                            () -> assertEquals(content.size(), 4),
                            () -> assertArrayEquals(new String[]{"Irina", "05 May 1991"}, firstRaw,
                            "First raw is different"),
                            () -> assertArrayEquals(new String[]{"Maria", "18 August 1995"}, secondRaw,
                                    "Second raw is different"),
                            () -> assertArrayEquals(new String[]{"Dmitrii", "15 September 1988"}, thirdRaw,
                                    "Third raw is different"),
                            () -> assertArrayEquals(new String[]{"Andrey", "22 December 1993"}, fourthRaw,
                                    "Fourth raw is different")
                    );
                } else if (name.contains("Home Language Codes.xlsx")) {
                    XLS xls = new XLS(zip);
                    String firstColumnName = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                    String secondColumnName = xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
                    String thirdColumnName = xls.excel.getSheetAt(0).getRow(0).getCell(2).getStringCellValue();

                    assertAll(
                            () -> assertEquals(2, xls.excel.getNumberOfSheets(),
                                    "Number of sheets is different"),
                            () -> assertEquals("English Name of Language", firstColumnName,
                                    "Name of first column is different"),
                            () -> assertEquals("Home Language Code", secondColumnName,
                                    "Name of second column is different"),
                            () -> assertEquals("World Languages Assessed Value Only", thirdColumnName,
                                    "Name of third column is different")
                    );
                }
            }
        }
    }
}
