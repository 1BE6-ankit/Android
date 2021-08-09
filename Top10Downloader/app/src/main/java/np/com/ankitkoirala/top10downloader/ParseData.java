package np.com.ankitkoirala.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseData {

    private static final String TAG = "ParseData";
    ArrayList<FeedEntry> entries;

    public ParseData() {
        this.entries = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getEntries() {
        return entries;
    }

    public boolean parse(String xmlString) {
        boolean success = true;
        FeedEntry currentRecord;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            int eventType = parser.getEventType();
            String tagName;

            currentRecord = new FeedEntry();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(inEntry) {
                            if("entry".equalsIgnoreCase(tagName)) {
                                this.entries.add(currentRecord);
                                currentRecord = new FeedEntry();
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }

            for(FeedEntry entry : entries) {
                Log.d(TAG, entry.toString());
                Log.d(TAG, "******************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }
}
