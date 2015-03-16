package ut.com.schonstal.stash.tracker.plugin;

import com.schonstal.stash.tracker.plugin.MessageParser;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by Sam Schonstal on 3/15/15.
 */

public class MessageParserTest {

    @Test
    public void testMessageAndStoryId()
    {
        String testMessage = "This is a comment [#123456]";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", false, messageParser.isFinished());
    }

    @Test
    public void testNoMessageAndStoryId()
    {
        String testMessage = "[#123456]";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", false, messageParser.isFinished());
    }

    @Test
    public void testFinishes()
    {
        String testMessage = "This is a comment [Finishes #123456]";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", true, messageParser.isFinished());
    }

    @Test
    public void testTrailingSpaces()
    {
        String testMessage = "This is a comment [#123456 ] ";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", false, messageParser.isFinished());
    }

    @Test
    public void testLeadingSpaces()
    {
        String testMessage = " [ #123456 ] ";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", false, messageParser.isFinished());
    }

    @Test
    public void testLeadingSpaceAndFinishes()
    {
        String testMessage = "This is a comment [ Finishes #123456]";
        MessageParser messageParser = new MessageParser(testMessage);
        assertEquals(testMessage + " - StoryIds don't match!", "123456", messageParser.getStoryId());
        assertEquals(testMessage + " - Does not Finish!", true, messageParser.isFinished());
    }
}
