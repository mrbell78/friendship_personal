package ngo.friendship.satellite.loadname;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ngo.friendship.satellite.R;


// TODO: Auto-generated Javadoc

/**
 * The Class FileArrayAdapter.
 */
public class FileArrayAdapter extends ArrayAdapter<Item>{

	/** The c. */
	private Context c;
	
	/** The id. */
	private int id;
	
	/** The items. */
	private List<Item>items;
	
	/**
	 * Instantiates a new file array adapter.
	 *
	 * @param context the context
	 * @param textViewResourceId the text view resource id
	 * @param objects the objects
	 */
	public FileArrayAdapter(Context context, int textViewResourceId,
			List<Item> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	public Item getItem(int i)
	 {
		 return items.get(i);
	 }
	 
 	/* (non-Javadoc)
 	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
 	 */
 	@Override
       public View getView(int position, View convertView, ViewGroup parent) {
               View v = convertView;
               if (v == null) {
                   LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   v = vi.inflate(id, null);
               }
               
               /* create a new view of my layout and inflate it in the row */
       		
               final Item o = items.get(position);
               if (o != null) {
                       TextView t1 = v.findViewById(R.id.TextView01);
                       TextView t2 = v.findViewById(R.id.TextView02);
                       TextView t3 = v.findViewById(R.id.TextViewDate);
                       /* Take the ImageView from layout and set the city's image */
	               		ImageView imageCity = v.findViewById(R.id.fd_Icon1);
	               		String uri = "drawable/" + o.getImage();
	               	    int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
	               	    Drawable image = c.getResources().getDrawable(imageResource);
	               	    imageCity.setImageDrawable(image);
                       
                       if(t1!=null)
                       	t1.setText(o.getName());
                       if(t2!=null)
                          	t2.setText(o.getData());
                       if(t3!=null)
                          	t3.setText(o.getDate());
                       
               }
               return v;
       }

}
