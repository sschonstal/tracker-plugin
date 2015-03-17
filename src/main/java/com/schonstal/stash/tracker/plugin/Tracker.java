package com.schonstal.stash.tracker.plugin;

import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.setting.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final String sourceCommitUrl = "https://www.pivotaltracker.com/services/v5/source_commits";
    private String stashRepoUrl;
    private CloseableHttpClient httpClient;
    private HttpPost httpPost;

    private static final Logger log = LoggerFactory.getLogger(Tracker.class);


    public Tracker(Settings settings, CloseableHttpClient httpClient, HttpPost httpPost) {
        hookResponse = null;
        this.httpClient = httpClient;
        this.httpPost = httpPost;

        apiKey = settings.getString("apiKey");
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey missing");
            //apiKey = "35fccc826fa5cd1eb22db6d62b788899";
        }

        stashRepoUrl = settings.getString("stashRepoUrl");
        if (stashRepoUrl == null) {
            throw new IllegalArgumentException("stashRepoUrl missing");
        }
    }

    public void postCommit(Commit commit)  {

        try {
            httpPost.setURI(new URI(sourceCommitUrl));

            StringEntity params = buildParams(commit);
            if(params == null) {
                return;
            }

            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-TrackerToken", apiKey);
            httpPost.setEntity(params);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() != 200) {
                log.info("Post Error for commit {} Reason {}", commit.getId(),  httpResponse.getStatusLine().getReasonPhrase());
            }
        }catch (Exception ex) {
            log.info("Post Commit Exception ()", ex);
        } finally {
            try {
                httpClient.close();
            } catch (Exception ex) {
                log.info("Post Commit Exception closing httpClient ()", ex);
            }
        }

    }

    private StringEntity buildParams(Commit commit) throws Exception {

        MessageParser messageParser = new MessageParser(commit.getMessage());
        if (messageParser.getStoryId() == null) {
            return null;
        }

        log.info("Tracker Plugin commit by {} - {}", commit.getAuthor(), commit.getMessage());

        JSONObject commitParams = new JSONObject();
        commitParams.put("commit_id", commit.getId());
        commitParams.put("message", commit.getMessage());
        commitParams.put("url", stashRepoUrl + "/" + commit.getId());
        commitParams.put("author", commit.getAuthor().getName());
        JSONObject sourceCommit = new JSONObject();
        sourceCommit.put("source_commit", commitParams);

        return new StringEntity(sourceCommit.toString());

    }
}
