package pro.fossa.apps.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PlainText {
	private ArrayList<ItemData> items;
	private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
	
	public PlainText (ArrayList<ItemData> items) {
		this.items = items;
	}
	
	public String export() {
    	String ret = "";
    	int count = 0;
        for (int i=items.size() - 1; i >= 0; i--) {
        	ItemData item = items.get(i);
    		count ++;
        	ret += String.valueOf(count) + ". ";
        	ret += df.format(item.time) + "\n";
        	ret += "    ";
        	if (item.delta > 0)
        		ret += "+" + Helpers.getTimeText(item.delta) + ", ";
        	ret += Helpers.getTimeText(item.offset);
        	ret += "\n";
        	ret += "    " + item.title;
        	
        	ret += "\n";
        }

        return ret;
    }

}
