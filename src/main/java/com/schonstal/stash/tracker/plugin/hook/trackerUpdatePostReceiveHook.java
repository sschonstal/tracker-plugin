package com.schonstal.stash.tracker.plugin.hook;

import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.commit.CommitsBetweenRequest;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import com.schonstal.stash.tracker.plugin.Tracker;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collection;

public class trackerUpdatePostReceiveHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator
{
    private final CommitService commitService;

    public trackerUpdatePostReceiveHook( CommitService commitService)
    {
        this.commitService = commitService;
    }

    @Override
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges)
    {
        for (RefChange refChange : refChanges)
        {
            CommitsBetweenRequest.Builder commitsBetweenBuilder = new CommitsBetweenRequest.Builder(context.getRepository() );
            commitsBetweenBuilder.exclude(refChange.getFromHash()); //Starting with
            commitsBetweenBuilder.include(refChange.getToHash()); // ending with

            PageRequest pageRequest = new PageRequestImpl(0,3);
            Tracker tracker = new Tracker(context.getSettings(), HttpClientBuilder.create().build(), new HttpPost());

            Page<Commit> commits;
            do {
                commits = commitService.getCommitsBetween(commitsBetweenBuilder.build(), pageRequest);
                for (Commit commit : commits.getValues()) {
                    tracker.postCommit(commit);
                }
                pageRequest = commits.getNextPageRequest();
            } while (!commits.getIsLastPage());
        }
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