package it.com.schonstal.stash.tracker.plugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.schonstal.stash.tracker.plugin.TrackMyStashPluginComponent;
import com.atlassian.sal.api.ApplicationProperties;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class MyComponentWiredTest
{
    private final ApplicationProperties applicationProperties;
    private final TrackMyStashPluginComponent trackMyStashPluginComponent;

    public MyComponentWiredTest(ApplicationProperties applicationProperties,TrackMyStashPluginComponent trackMyStashPluginComponent)
    {
        this.applicationProperties = applicationProperties;
        this.trackMyStashPluginComponent = trackMyStashPluginComponent;
    }

    @Test
    public void testMyName()
    {
        assertEquals("names do not match!", "myComponent:" + applicationProperties.getDisplayName(), trackMyStashPluginComponent.getName());
    }
}