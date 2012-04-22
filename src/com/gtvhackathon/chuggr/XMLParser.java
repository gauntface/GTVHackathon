package com.gtvhackathon.chuggr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

public class XMLParser {

    private ArrayList<timeAndReason> mEventTimes =new ArrayList<timeAndReason>();

        ArrayList<timeAndReason> parse(InputStream istream){

        XmlPullParser parser = Xml.newPullParser();

        try{

            parser.setInput(istream, "UTF-8");

            int event = parser.getEventType();

            while(event!=XmlPullParser.END_DOCUMENT){

                event = parser.next();
                if(event==XmlPullParser.START_TAG){
                    if(parser.getAttributeCount()>1){
                        String value = parser.getAttributeValue(0);
                        String reason = parser.getAttributeValue(1);
                        mEventTimes.add(new timeAndReason(Integer.parseInt(value),reason));
                    }
                }
            }

        }catch(Exception e){
            e.toString();
        }

        return mEventTimes;
    }

    static class timeAndReason{
        public Integer time;
        public String reason;

        timeAndReason(Integer time, String reason){
           // Log.e("xxx", ""+time+" "+reason);
            this.time=time;
            this.reason=reason;
        }
    }

    static InputStream loadFile(String filename, Context context){
        try {
            return context.getResources().getAssets().open("test.xml");

        }catch(Exception e){
            e.toString();
        }
        return null;
    }


}
