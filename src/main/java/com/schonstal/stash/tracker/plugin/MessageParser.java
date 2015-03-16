package com.schonstal.stash.tracker.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Sam Schonstal on 3/14/15. //woot woot pi day
 */


public class MessageParser {

    private String message;

    public MessageParser(String message)
    {
        this.message = message;
    }


    public  String getStoryId()
    {
        String storyId;
        String bracketedString = getBracketedString(message);

        if(bracketedString == null)
        {
            return null;
        }

        storyId = getStoryNumber(bracketedString);

        return storyId;
    }

    public Boolean isFinished()
    {
        String bracketedString = getBracketedString(message);

        Matcher matcher = Pattern.compile(".*FINISHES.*").matcher(bracketedString.toUpperCase());
        return matcher.matches();
    }



    private String getStoryNumber(String bracketedString) {
        String storyNumber = "";

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
        if (start >= 0)
        {
            int end = message.indexOf(']', start);
            if (end > 0)
            {
                bracketedString = message.substring(start, end);
            }
        }
        return bracketedString;
    }
}

