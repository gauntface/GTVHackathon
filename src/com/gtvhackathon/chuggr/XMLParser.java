package com.gtvhackathon.chuggr;

import java.io.InputStream;
import java.util.Vector;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

public class XMLParser {

    Vector<DrinkEvent> parse(InputStream istream){

        XmlPullParser parser = Xml.newPullParser();

        Vector<DrinkEvent> events = new Vector<DrinkEvent>();

        try{

            parser.setInput(istream, "UTF-8");

            int event = parser.getEventType();

            Vector<XMLElement> tags = new Vector<XMLElement>();

            XMLElement element = null;

            while(event!=XmlPullParser.END_DOCUMENT){

                event = parser.next();
                if(event==XmlPullParser.START_TAG){
                    if(element!=null){
                        tags.add(element);
                    }
                    element = new XMLElement();
                    element.name = parser.getName();
                    if(parser.getAttributeCount()>1){
                        String value = parser.getAttributeValue(0);
                        element.text = value;
                        String reason = parser.getAttributeValue(1);
                        element.otherText = reason;
                    }
                }else if(event==XmlPullParser.TEXT){
                    if(element!=null && element.text!=null){
                        String txt = parser.getText().trim();
                        if(txt.length()>0){
                            element.text = txt;
                        }
                    }
                }
            }


            DrinkEvent devent = null;
            Vector<Integer> times = new Vector<Integer>();
            for(int i = 0;i<tags.size();i++){

                XMLElement tag = tags.get(i);

                String tagName = tag.name;

                if(tagName.equalsIgnoreCase(DrinkEvent.EVENT)){
                    if(devent!=null){
                        devent.setTimes(times);
                        events.add(devent);
                    }
                    devent = new DrinkEvent();
                } else if(tagName.equalsIgnoreCase(DrinkEvent.TIMESTAMP)){
                    times.add(Integer.parseInt(tag.text));
                    devent.addDrink(new Drink(tag.otherText));
                } else if(tagName.equalsIgnoreCase(DrinkEvent.DRINK)){
                    devent.addDrink(new Drink(tag.text));
                }

            }

            if(devent!=null){
                devent.setTimes(times);
                events.add(devent);
            }

        }catch(Exception e){
            e.toString();
        }

        return events;
    }

    class XMLElement{
        String name;
        String text;
        String otherText;

    }

    class Drink{

        private String drinkMessage;

        public Drink(String drinkMessage) {
            super();
            this.drinkMessage = drinkMessage;
        }

        void drink(){
            // display drink graphics
            // vibrate phones
            // etc
        }

    }

    class DrinkEvent{

        public static final String EVENT = "event";
        public static final String GTVVIDEO= "gtvvideo";
        public static final String TIMESTAMP= "timestamp";
        public static final String LYRIC= "lyric";
        public static final String DRINK= "drink";

        protected Vector<Drink> drink = new Vector<Drink>();

        protected int[] times;

        void setTimes(Vector<Integer> times){
            this.times = new int[times.size()];
            for(int i = 0;i<times.size();i++){
                this.times[i] = times.get(i);
            }
        }

        void addDrink(Drink drink){
            this.drink.add(drink);
        }

        String[] getDrinkReasons(){
            String[] reasons = new String[drink.size()];
            for(int i = 0;i<reasons.length;i++){
                reasons[i] = drink.get(i).drinkMessage;
            }
            return reasons;
        }

        String getTimesAsString(){
            String string = new String();
            for(int i = 0;i<times.length;i++){
                string += " " + Integer.toString(times[i]);
            }
            return string;
        }

        void onTime(int second){
            int count = times.length-1;
            for(int i = count;i>-1;i--){
                if(times[i]==second){

                    return;
                }
            }
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
