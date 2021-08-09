package np.com.ankitkoirala.toprssfeeds;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class XmlParser {

    private static final String TAG = "XmlParser";

    ArrayList<FeedEntry> entries;

    public boolean parse(String xmlString) {
        try {
            entries = new ArrayList<>();
            boolean inEntry = false;
            String tagName;
            FeedEntry entry = new FeedEntry();
            String text = "";

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xmlString));
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                tagName = xpp.getName();

                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            entry = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                inEntry = false;
                                entries.add(entry);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                entry.setArtist(text);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                entry.setImage(text);
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                entry.setName(text);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                entry.setSummary(text);
                                break;
                            }
                        }
                        break;

                    default:
                        // do nothing
                }

                xpp.next();
            }

            return true;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "parse: XmlPUllParserException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "parse: IOException: " + e.getMessage());
        }

        return false;
    }

    public ArrayList<FeedEntry> getEntries() {
        return entries;
    }

}
