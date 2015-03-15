package com.schonstal.stash.tracker.plugin;

import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.setting.Settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Tracker(Settings settings)
    {
        hookResponse = null;
        this.settings = settings;

        String apiKey = settings.getString("apiKey");
        if (apiKey == null)
        {
            //throw new IllegalArgumentException("apiKey missing");
        }

        String stashRepoUrl = settings.getString("stashRepoUrl");
        if (stashRepoUrl == null)
        {
            //throw new IllegalArgumentException("stashRepoUrl missing");
        }
    }

    public void postCommit(Commit commit)
    {


        String storyId =  getStoryId(commit.getMessage());
        if(storyId == null)
        {
            return;
        }

        Boolean finishes = false;

        if(hookResponse != null)
        {
            hookResponse.out().println("Sams Plugin commit by " + commit.getAuthor() + " Message = " + commit.getMessage() + " storyID = " + storyId+ "\n");

        }
    }

    private String getStoryId(String message)
    {
        String storyId = null;
        String bracketedString = getBracketedString(message);

        if(bracketedString == null)
        {
            return null;
        }

        storyId = getStoryNumber(bracketedString);

        return storyId;
    }

    private String getStoryNumber(String bracketedString) {
        String storyNumber = null;

        int start = bracketedString.indexOf('#');
        if (start > 0) {
            start++;
            int end = bracketedString.indexOf(' ', start);
            if (end > 0)
            {
                storyNumber = bracketedString.substring(start, end);
            } else {
                storyNumber = bracketedString.substring(start);
            }
        }

        Matcher matcher = Pattern.compile("[0-9]*").matcher(storyNumber);
        if(!matcher.matches())
        {
            return null;
        }

        return storyNumber;
    }


    private String getBracketedString(String message)
    {
        String bracketedString = null;

        int start = message.indexOf('[');
        if (start > 0)
        {
            int end = message.indexOf(']', start);
            if (end > 0)
            {
                bracketedString = message.substring(start, end);
            }
        }
        return bracketedString;
    }

    private Boolean isFinished(String message)
    {
        String bracketedString = getBracketedString(message);

        Matcher matcher = Pattern.compile("[Ff]inishes").matcher(bracketedString);
        if(matcher.matches())
        {
            return true;
        }
        return false;
    }
}
