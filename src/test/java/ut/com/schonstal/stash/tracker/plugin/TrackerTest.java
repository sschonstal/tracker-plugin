package ut.com.schonstal.stash.tracker.plugin;


import com.atlassian.stash.commit.Commit;
import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.user.Person;
import com.schonstal.stash.tracker.plugin.Tracker;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;


public class TrackerTest {

    private CloseableHttpClient mockHttpClient;
    private HttpPost httpPost;
    private CloseableHttpResponse mockHttpResponse;
    private StatusLine mockStatusLine;
    private Settings mockSettings;
    private Person mockPerson;
    private Commit mockCommit;

    @Test
    public void postCommit() throws Exception
    {
        prepareMocks();
        httpPost = new HttpPost();
        Tracker tracker = new Tracker(mockSettings, mockHttpClient, httpPost);
        tracker.postCommit(mockCommit, "http://schonstal.com/projects/tracker/repos/test/825bf5a3772aff2637259fa9ff40cd28d2c0aab4");

        HttpEntity httpEntity = httpPost.getEntity();

        if (httpEntity != null) {
            JSONObject parameters = new JSONObject(EntityUtils.toString(httpEntity));
            JSONObject sourceCommit = parameters.getJSONObject("source_commit");
            assertEquals("Wrong Commit Id", "825bf5a3772aff2637259fa9ff40cd28d2c0aab4", sourceCommit.getString("commit_id"));
            assertEquals("Wrong Author", "Sam Schonstal", sourceCommit.getString("author"));
            assertEquals("Wrong Stash Url", "http://schonstal.com/projects/tracker/repos/test/825bf5a3772aff2637259fa9ff40cd28d2c0aab4", sourceCommit.getString("url"));
            assertEquals("Wrong Commit Message", "This is a comment [#87444472]", sourceCommit.getString("message"));
        }
        verify(mockHttpClient).execute(any(HttpPost.class));
        assertEquals("api Key doesn't match", "123456789abcdefghij", httpPost.getHeaders("X-TrackerToken")[0].getValue());
    }


    private void prepareMocks() throws Exception
    {
        mockSettings = mock(Settings.class);
        when(mockSettings.getString("apiKey")).thenReturn("123456789abcdefghij");

        mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        mockHttpResponse = mock(CloseableHttpResponse.class);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);

        mockHttpClient = mock(CloseableHttpClient.class);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);

        mockPerson = mock(Person.class);
        when(mockPerson.getName()).thenReturn("Sam Schonstal");

        mockCommit = mock(Commit.class);
        when(mockCommit.getAuthor()).thenReturn(mockPerson);
        when(mockCommit.getMessage()).thenReturn("This is a comment [#87444472]");
        when(mockCommit.getId()).thenReturn("825bf5a3772aff2637259fa9ff40cd28d2c0aab4");
    }

}
