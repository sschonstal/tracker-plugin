package com.schonstal.stash.tracker.plugin.hook;

import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.commit.CommitsBetweenRequest;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.nav.NavBuilder;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import com.schonstal.stash.tracker.plugin.Tracker;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class trackerUpdatePostReceiveHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator {
    private final CommitService commitService;
    private final NavBuilder navBuilder;

    private static final Logger log = LoggerFactory.getLogger(Tracker.class);

    public trackerUpdatePostReceiveHook(CommitService commitService, NavBuilder navBuilder) {
        this.commitService = commitService;
        this.navBuilder = navBuilder;
    }

    @Override
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            for (RefChange refChange : refChanges) {
                CommitsBetweenRequest.Builder commitsBetweenBuilder = new CommitsBetweenRequest.Builder(context.getRepository());
                commitsBetweenBuilder.exclude(refChange.getFromHash()); //Starting with
                commitsBetweenBuilder.include(refChange.getToHash()); // Ending with

                PageRequest pageRequest = new PageRequestImpl(0, 3);
                Tracker tracker = new Tracker(context.getSettings(), httpClient, new HttpPost());

                Page<Commit> commits;
                do {
                    commits = commitService.getCommitsBetween(commitsBetweenBuilder.build(), pageRequest);
                    for (Commit commit : commits.getValues()) {
                        tracker.postCommit(commit, navBuilder.repo(context.getRepository()).changeset(commit.getId()).buildAbsolute());
                    }
                    pageRequest = commits.getNextPageRequest();
                } while (!commits.getIsLastPage());
            }
        } catch (Exception ex) {
            log.error("Post Commit Exception ()", ex);
        } finally {
            try {
                httpClient.close();
            } catch (Exception ex) {
                log.error("Post Commit Exception closing httpClient ()", ex);
            }
        }
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository) {
        if (settings.getString("apiKey", "").isEmpty()) {
            errors.addFieldError("apiKey", "Tracker API Key is blank, please supply one");
        }
    }
}