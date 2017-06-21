package org.apache.commons.configuration2;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link YAMLConfiguration}
 */
public class TestYAMLConfiguration
{
    /** The files that we test with. */
    private String testYaml =
            ConfigurationAssert.getTestFile("test.yaml").getAbsolutePath();
    private File testSaveConf = ConfigurationAssert.getOutFile("testsave.yaml");

    private YAMLConfiguration yamlConfiguration;

    @Before
    public void setUp() throws Exception
    {
        yamlConfiguration = new YAMLConfiguration();
        yamlConfiguration.read(new FileReader(testYaml));
        removeTestFile();
    }

    @Test
    public void testGetProperty_simple()
    {
        assertEquals("value1", yamlConfiguration.getProperty("key1"));
    }

    @Test
    public void testGetProperty_nested()
    {
        assertEquals("value23", yamlConfiguration.getProperty("key2.key3"));
    }

    @Test
    public void testGetProperty_nested_with_list()
    {
        assertEquals(Arrays.asList("col1", "col2"),
                yamlConfiguration.getProperty("key4.key5"));
    }

    @Test
    public void testGetProperty_subset()
    {
        Configuration subset = yamlConfiguration.subset("key4");
        assertEquals(Arrays.asList("col1", "col2"), subset.getProperty("key5"));
    }

    @Test
    public void testGetProperty_very_nested_properties()
    {
        Object property =
                yamlConfiguration.getProperty("very.nested.properties");
        assertEquals(Arrays.asList("nested1", "nested2", "nested3"), property);
    }

    @Test
    public void testGetProperty_integer()
    {
        Object property = yamlConfiguration.getProperty("int1");
        assertTrue("property should be an Integer",
                property instanceof Integer);
        assertEquals(37, property);
    }

    @Test
    public void testSave() throws IOException, ConfigurationException
    {
        // save the YAMLConfiguration as a String...
        StringWriter sw = new StringWriter();
        yamlConfiguration.write(sw);
        String output = sw.toString();

        // ..and then try parsing it back as using SnakeYAML
        Map parsed = new Yaml().loadAs(output, Map.class);
        assertEquals(6, parsed.entrySet().size());
        assertEquals("value1", parsed.get("key1"));

        Map key2 = (Map) parsed.get("key2");
        assertEquals("value23", key2.get("key3"));

        List<String> key5 =
                (List<String>) ((Map) parsed.get("key4")).get("key5");
        assertEquals(2, key5.size());
        assertEquals("col1", key5.get(0));
        assertEquals("col2", key5.get(1));
    }

    @Test
    public void testGetProperty_dictionary()
    {
        assertEquals("Martin D'vloper",
                yamlConfiguration.getProperty("martin.name"));
        assertEquals("Developer", yamlConfiguration.getProperty("martin.job"));
        assertEquals("Elite", yamlConfiguration.getProperty("martin.skill"));
    }

    /**
     * Removes the test output file if it exists.
     */
    private void removeTestFile()
    {
        if (testSaveConf.exists())
        {
            assertTrue(testSaveConf.delete());
        }
    }
}