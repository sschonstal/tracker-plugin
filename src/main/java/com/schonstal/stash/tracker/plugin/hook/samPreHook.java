package com.schonstal.stash.tracker.plugin.hook;

import com.atlassian.stash.hook.*;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.commit.*;
import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.setting.RepositorySettingsValidator;
import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.setting.SettingsValidationErrors;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import com.schonstal.stash.tracker.plugin.Tracker;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collection;

public class samPreHook implements PreReceiveRepositoryHook, RepositorySettingsValidator
{
    private final CommitService commitService;

    public samPreHook( CommitService commitService)
    {
        this.commitService = commitService;
    }


    @Override
    public boolean onReceive(RepositoryHookContext context, Collection<RefChange> refChanges, HookResponse hookResponse)
    {

        for (RefChange refChange : refChanges)
        {
            CommitsBetweenRequest.Builder commitsBetweenBuilder = new CommitsBetweenRequest.Builder(context.getRepository() );
            commitsBetweenBuilder.exclude(refChange.getFromHash()); //Starting with
            commitsBetweenBuilder.include(refChange.getToHash()); // ending with

            PageRequest pageRequest = new PageRequestImpl(0,3);
            Tracker tracker = new Tracker(context.getSettings(), HttpClientBuilder.create().build(), new HttpPost());
            tracker.hookResponse = hookResponse;

            Page<Commit> commits;
            do {
                commits = commitService.getCommitsBetween(commitsBetweenBuilder.build(), pageRequest);
                for (Commit commit : commits.getValues()) {
                    tracker.postCommit(commit);
                }
                pageRequest = commits.getNextPageRequest();
            } while (!commits.getIsLastPage());
        }
        return true;
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
        if (settings.getString("apiKey", "").isEmpty())
        {
            errors.addFieldError("apiKey", "Tracker API Key is blank, please supply one");
        }

        if (settings.getString("stashRepoUrl", "").isEmpty())
        {
            errors.addFieldError("stashRepoUrl", "Stash URL is blank, please supply one");
        }
    }
}
