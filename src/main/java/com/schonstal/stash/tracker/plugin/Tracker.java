package com.schonstal.stash.tracker.plugin;

import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.setting.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;


import java.io.*;
import java.net.URI;


/**
 * Created by Sam Schonstal on 3/14/15. //woot woot pi day
 */

//curl -X POST -H"X-TrackerToken: 35fccc826fa5cd1eb22db6d62b788899" "https://www.pivotaltracker.com/services/v5/source_commits"
//        -H "Content-Type: application/json"
//        -d'{"source_commit": {"commit_id":"825bf5a3772",
//                                "message":"adjust presence status bubble [fixes #87444472]" ,
//                                "url":"http://stash.ds.adp.com/projects/NSCE/repos/yeti/commits/825bf5a3772aff2637259fa9ff40cd28d2c0aab4",
//                                "author":"sam schonstal"}}'

public class Tracker {

    public HookResponse hookResponse;
    private String apiKey;
    private Settings settings;
    private final String sourceCommitUrl = "https://www.pivotaltracker.com/services/v5/source_commits";
    private String stashRepoUrl;
    private CloseableHttpClient httpClient;
    private HttpPost httpPost;

    public Tracker(Settings settings, CloseableHttpClient httpClient, HttpPost httpPost) {
        hookResponse = null;
        this.settings = settings;
        this.httpClient = httpClient;
        this.httpPost = httpPost;

        apiKey = settings.getString("apiKey");
        if (apiKey == null) {
            //throw new IllegalArgumentException("apiKey missing");
            apiKey = "35fccc826fa5cd1eb22db6d62b788899";
        }

        stashRepoUrl = settings.getString("stashRepoUrl");
        if (stashRepoUrl == null) {
            //throw new IllegalArgumentException("stashRepoUrl missing");
            stashRepoUrl = "http:///";
        }
    }

    public void postCommit(Commit commit)  {

        try {
            httpPost.setURI(new URI(sourceCommitUrl));

            StringEntity params = buildParams(commit);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-TrackerToken", apiKey);
            httpPost.setEntity(params);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() != 200) {
                //TODO Log Error
                if (hookResponse != null) {
                    hookResponse.out().println("Post Error " + httpResponse.getStatusLine().getReasonPhrase() + "\n");
                }
            }
        }catch (Exception ex) {
            //TODO Log Error
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                //TODO Log Error
            }
        }

    }

    private StringEntity buildParams(Commit commit) throws UnsupportedEncodingException {

        MessageParser messageParser = new MessageParser(commit.getMessage());
        if (messageParser.getStoryId() == null) {
            return null;
        }

        if (hookResponse != null) {
            hookResponse.out().println("Sam's Plugin commit by " + commit.getAuthor() + " Message = " + commit.getMessage() + " storyID = " + messageParser.getStoryId() + "\n");

        }

        StringEntity params = new StringEntity("{\"source_commit\": {\"commit_id\":\"" + messageParser.getStoryId()  + "\",\n" +
                "\"message\":\"" + commit.getMessage() + "\" ,\n" +
                "\"url\":\"" + stashRepoUrl + "/" + commit.getId() + "\",\n" +
                "\"author\":\""+ commit.getAuthor().getName() +"\"}}");

        return params;
    }
}
