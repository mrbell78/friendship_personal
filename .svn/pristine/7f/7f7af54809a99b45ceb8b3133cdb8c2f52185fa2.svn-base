package ngo.friendship.satellite.loadname;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ngo.friendship.satellite.R;

// TODO: Auto-generated Javadoc

/**
 * The Class FileChooser.
 */
public class FileChooser extends Activity implements OnItemClickListener {

	/** The current dir. */
	private File currentDir;
	
	/** The adapter. */
	private FileArrayAdapter adapter;
	
	/** The lv file list. */
	ListView lvFileList;
	
	/** The tv dir name. */
	TextView tvDirName;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		setContentView(R.layout.file_chooser_layout);
		lvFileList = findViewById(R.id.lv_file_list);
		tvDirName = findViewById(R.id.tv_path);
		
		View v = findViewById(R.id.btn_close);
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FileChooser.this.finish();
				
			}
		});

		currentDir = Environment.getExternalStorageDirectory();

		fill(currentDir); 
		
		
	}
	
	/**
	 * Fill.
	 *
	 * @param f the f
	 */
	private void fill(File f)
	{
		File[]dirs = f.listFiles(); 
		tvDirName.setText(f.getAbsolutePath());
		List<Item>dir = new ArrayList<Item>();
		List<Item>fls = new ArrayList<Item>();
		try{
			for(File ff: dirs)
			{ 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){


					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";

					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else
				{

					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
			}
		}catch(Exception e)
		{    

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase(Environment.getExternalStorageDirectory().getName()))
			dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
		lvFileList.setAdapter(adapter);
		lvFileList.setOnItemClickListener(this);
	}
	
	/**
	 * On file click.
	 *
	 * @param o the o
	 */
	private void onFileClick(Item o)
	{
		//MHealthAppToast.showToast(this, "Folder Clicked: "+ currentDir);
		Intent intent = new Intent();
		intent.putExtra("GetPath",currentDir.toString());
		intent.putExtra("GetFileName",o.getName());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
			currentDir = new File(o.getPath());
			fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {}
}
