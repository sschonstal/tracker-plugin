package ut.com.schonstal.stash.tracker.plugin;

import org.junit.Test;
import com.schonstal.stash.tracker.plugin.TrackMyStashPluginComponent;
import com.schonstal.stash.tracker.plugin.TrackMyStashPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        TrackMyStashPluginComponent component = new TrackMyStashPluginComponentImpl(null);
        assertEquals("names do not match!", "trackMyStash",component.getName());
    }
}