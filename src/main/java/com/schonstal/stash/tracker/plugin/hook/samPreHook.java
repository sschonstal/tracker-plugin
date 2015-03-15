package com.schonstal.stash.tracker.plugin.hook;

import com.atlassian.stash.hook.*;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.commit.*;
import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import com.schonstal.stash.tracker.plugin.Tracker;

import java.util.Collection;

public class samPreHook implements PreReceiveRepositoryHook
{
    private final CommitService commitService;

    public samPreHook( CommitService commitService)
    {
        this.commitService = commitService;
    }


    @Override
    public boolean onReceive(RepositoryHookContext context, Collection<RefChange> refChanges, HookResponse hookResponse)
    {

        //CommitRequest cr = new CommitRequest();
        hookResponse.out().println("Count = " + refChanges.size());



        for (RefChange refChange : refChanges)
        {

            CommitsBetweenRequest.Builder commitsBetweenBuilder = new CommitsBetweenRequest.Builder(context.getRepository() );
            commitsBetweenBuilder.exclude(refChange.getFromHash()); //Starting with
            commitsBetweenBuilder.include(refChange.getToHash()); // ending with

            PageRequest pageRequest = new PageRequestImpl(0,3);
            Tracker tracker = new Tracker(context.getSettings());
            tracker.hookResponse = hookResponse;

            Page<Commit> commits;
            do {
                commits = commitService.getCommitsBetween(commitsBetweenBuilder.build(), pageRequest);
                for (Commit commit : commits.getValues()) {
                    hookResponse.out().println( "Sams Plugin commit id = " + commit.getId() + " by " + commit.getAuthor() + " Message = " + commit.getMessage() + "\n");

                    tracker.postCommit(commit);
                }
                pageRequest = commits.getNextPageRequest();
            } while (!commits.getIsLastPage());

        }
        return false;
    }
}
