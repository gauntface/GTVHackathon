package com.gtvhackathon.chuggr;

import java.io.InputStream;
import java.util.Vector;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;


public class XMLParser {

	int[] parse(InputStream istream){
		
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
				if(parser.getAttributeCount()>0){
				String value = parser.getAttributeValue(0);
					element.text = value;
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
			} else if(tagName.equalsIgnoreCase(DrinkEvent.DRINK)){
				devent.setDrink(new Drink(tag.text));
			}
			
		}
		
		if(devent!=null){
			devent.setTimes(times);
			events.add(devent);
		}
		
		}catch(Exception e){
			e.toString();
		}
		
		return events.firstElement().times;
	}
	
	class XMLElement{
		String name;
		String text;
		
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
			
		protected Drink drink;
		
		protected int[] times;
		
		void setTimes(Vector<Integer> times){
			this.times = new int[times.size()];
			for(int i = 0;i<times.size();i++){
				this.times[i] = times.get(i);
			}
		}
		
		void setDrink(Drink drink){
			this.drink = drink;
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
					drink.drink();
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
