package ut.com.schonstal.stash.tracker.plugin;

import org.junit.Test;
import com.schonstal.stash.tracker.plugin.MyPluginComponent;
import com.schonstal.stash.tracker.plugin.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}