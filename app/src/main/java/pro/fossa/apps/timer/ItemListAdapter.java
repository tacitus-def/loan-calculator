package pro.fossa.apps.timer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ItemListAdapter extends ArrayAdapter<ItemData> {
	private final Context context;
	
	public ItemListAdapter (Context context, int textViewResourceId, List<ItemData> objects) {
		super(context, textViewResourceId, objects);
		 
		this.context = context;
	}
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View rowView = inflater.inflate(R.layout.item_row, parent, false);
      TextView textOffset = (TextView) rowView.findViewById(R.id.offset);
      TextView textTitle  = (TextView) rowView.findViewById(R.id.title);
      TextView textTime   = (TextView) rowView.findViewById(R.id.time);
      
      ItemData item = getItem(position);
      
      textOffset.setText(Helpers.getTimeText(item.offset));
      if (item.delta != 0) {
    	  textTime.setText('+' + Helpers.getTimeText(item.delta));
      }
      if (item.title != null) {
    	  textTitle.setText(item.title);
      }
      
      return rowView;
    }    
}
