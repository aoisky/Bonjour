package edu.purdue.cs.hineighbor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Create a sidebar fragment
 * @author Yudong Yang
 *
 */
public class SidebarFragment extends ListFragment {


	private ListView listView;
	private List<MenuItem> menuItems;
	private mySidebarAdapter sidebarAdapter;
	
	public static final int POST_ARTICLE = 101;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		View view = inflater.inflate(R.layout.sidebar_fragment,
                container, false);
		
        listView = (ListView) view.findViewById(android.R.id.list);
        menuItems = new ArrayList<MenuItem>();
        
		int width = display.getWidth();
		view.setAlpha(0.70f);
		view.getLayoutParams().width = (int)(width * 0.7);
		view.setBackgroundColor(Color.parseColor("#E5E9EF"));
	    return view;  
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    //MenuItem homeMenu = new MenuItem(new ColorDrawable(Color.parseColor("#00A9FF")), "Home");
	    MenuItem newArticleMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_edit), "Control Panel");
	    MenuItem userMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_person), "User");
	    MenuItem myArticleMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_unread), "My Friends");
	    //MenuItem favoriteMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_favorite), "Favorite");
	    MenuItem helpMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_help), "Help");
	    MenuItem aboutMenu = new MenuItem(getResources().getDrawable(R.drawable.ic_action_about), "About");
	    menuItems.add(newArticleMenu);
	    //menuItems.add(homeMenu);
	    menuItems.add(userMenu);
	    menuItems.add(myArticleMenu);
	    //menuItems.add(favoriteMenu);
	   
	    menuItems.add(helpMenu);
	    menuItems.add(aboutMenu);
	    
        sidebarAdapter = new mySidebarAdapter(getActivity(), android.R.id.list, menuItems);
        listView.setAdapter(sidebarAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemInt,
					long arg3) {

				switch(itemInt){

					case 1: //User Info page
						Intent userInfoIntent = new Intent(getActivity(), UserProfile.class);
						startActivity(userInfoIntent);
					break;
					
					
					case 3:  //Help page
						Intent mainActivityWithHelp = new Intent(getActivity(), MainActivity.class);
						mainActivityWithHelp.putExtra("HELP", true);
						startActivity(mainActivityWithHelp);
					break;
					
					case 4: //About dialog
						showAbout();
					break;
					
				}
			}
        	
        });
	}
	
	private void showAbout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		  builder.setMessage("Bonjour Version 1.0 Beta\n")
		  .setTitle("About Us")
		  .setCancelable(false)
		  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) { }
      });

		  AlertDialog alert = builder.create();
		  alert.show();
	}

	public class MenuItem{
		private Drawable menuDrawable;
		private CharSequence menuName;
		
		public MenuItem(Drawable drawable, CharSequence menuName){
			this.menuDrawable = drawable;
			this.menuName = menuName;
		}
		
		public Drawable getIcon(){
			return menuDrawable;
		}
		
		public CharSequence getMenuName(){
			return menuName;
		}
	}
	
	public class mySidebarAdapter extends ArrayAdapter<MenuItem>{

		Context context;
		public mySidebarAdapter(Context context, int resource, List<MenuItem> menu) {
			super(context, resource, menu);
			this.context = context;
			// TODO Auto-generated constructor stub
		}
		
	    private class ViewHolder {
	        ImageView imageView;
	        TextView txtMenuName;
	    }
		
	    public View getView(int position, View view, ViewGroup parent) {
	    	ViewHolder holder = null;
			MenuItem rowItem = getItem(position);
			
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if(view == null){
				view = mInflater.inflate(R.layout.main_sidebar, null);
				 holder = new ViewHolder();
		            holder.txtMenuName = (TextView) view.findViewById(R.id.menu_name);
		            holder.imageView = (ImageView) view.findViewById(R.id.menu_image);
		            view.setTag(holder);
		        } else
		            holder = (ViewHolder)view.getTag();
			
			
			holder.txtMenuName.setText(rowItem.getMenuName());
			holder.imageView.setImageDrawable(rowItem.getIcon());
	    	return view;
	    }
		
	}
	
}
