package com.schonstal.stash.tracker.plugin;

import com.atlassian.sal.api.ApplicationProperties;

public class TrackMyStashPluginComponentImpl implements TrackMyStashPluginComponent
{
    private final ApplicationProperties applicationProperties;

    public TrackMyStashPluginComponentImpl(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "trackMyStash:" + applicationProperties.getDisplayName();
        }
        
        return "trackMyStash";
    }
}