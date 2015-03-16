package com.schonstal.stash.tracker.plugin.hook;

import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;
import java.util.Collection;

public class trackerUpdatePostReceiveHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator
{
    /**
     * Connects to a configured URL to notify of all changes.
     */
    @Override
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges)
    {

//       String url = context.getSettings().getString("url");
//        if (url != null)
//        {
//            try
//            {
//                System.out.print("Test");
//                new URL(url).openConnection().getInputStream().close();
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
//        if (settings.getString("url", "").isEmpty())
//        {
//            errors.addFieldError("url", "Url field is blank, please supply one");
//        }
    }
}