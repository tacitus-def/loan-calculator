package pro.fossa.apps.timer;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class CSV {
	private Context context;
	private ArrayList<ItemData> items;
	private final SimpleDateFormat df = new SimpleDateFormat("\"dd.MM.yyyy HH:mm:ss\"", Locale.getDefault());
	
	public CSV (Context context, ArrayList<ItemData> items) {
		this.context = context;
		this.items = items;
	}
	
	public void export(final String path) throws IOException {
        {
            // show waiting screen
            CharSequence contentTitle = context.getResources().getString(R.string.app_name);
            final ProgressDialog progDialog = ProgressDialog.show(
            		context, contentTitle, "even geduld aub...",
                    true);//please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                }
            };

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(path);

                        fw.append("time");
                        fw.append(',');

                        fw.append("offset");
                        fw.append(',');

                        fw.append("delta");
                        fw.append(',');

                        fw.append("human_time");
                        fw.append(',');

                        fw.append("human_offset");
                        fw.append(',');

                        fw.append("human_delta");
                        fw.append(',');

                        fw.append("checkpoint");
                        fw.append(',');

                        fw.append("title");
                        fw.append('\n');

                        for (int i=0; i < items.size(); i++) {
                        	ItemData item = items.get(i);
                        	fw.append(String.valueOf(item.time));
                        	fw.append(",");
                        	
                        	fw.append(String.valueOf(item.offset));
                        	fw.append(",");
                        	
                        	fw.append(String.valueOf(item.delta));
                        	fw.append(",");
                        	
                        	fw.append(df.format(item.time));
                        	fw.append(",");
                        	
                        	fw.append(Helpers.getTimeText(item.offset));
                        	fw.append(",");
                        	
                        	fw.append(Helpers.getTimeText(item.delta));
                        	fw.append(",");
                        	
                        	fw.append(String.valueOf(item.checkpoint));
                        	fw.append(",");

                        	fw.append('"' + item.title.replaceAll("\"", "\"\"") + '"');
                        	fw.append("\n");
                        }

                        // fw.flush();
                        fw.close();

                    } catch (Exception e) {
                    }
                    handler.sendEmptyMessage(0);
                    progDialog.dismiss();
                }
            }.start();
        }

    }
}
