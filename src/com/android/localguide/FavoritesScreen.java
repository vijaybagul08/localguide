package com.android.localguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.LocalGuideApplication.favoriteItem;

public class FavoritesScreen extends Activity{
	
	LocalGuideApplication app;
	ArrayList<favoriteItem> mList;
	ListView mListView;
	BaseAdapter mAdapter;
	
	public void onCreate(Bundle savedInstance)
	{
		System.out.println("On create favorites screen ********* ");
		super.onCreate(savedInstance);
		setContentView(R.layout.favorites);
		app = (LocalGuideApplication)this.getApplication();
		
		mList = app.getFavoritesList();

		mListView = (ListView)findViewById(R.id.list);
		mAdapter = new ListAdapter(this);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            	Intent intent = new Intent();
        		intent.setClass(FavoritesScreen.this, FavoritesResults.class);
            	//intent.setClass(FavoritesScreen.this, OptionsScreen.class);
            	intent.setAction("com.mani.favorites");
        		Bundle bun = new Bundle();
                bun.putInt("position", position);
                
                intent.putExtras(bun);
        		startActivity(intent);
        	}
        	
        }); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Favorites screen ********** on resume");
		mList = app.getFavoritesList();
		mAdapter.notifyDataSetChanged();
	}
	
	public void onBackPressed ()
	{
		System.out.println("On back key pressed ************* ");
		 app.saveToDataBase();
		 this.finish();
	}

	private  class ListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
 
        public ListAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mList.size();
        }
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mList.get(position).title);
            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));

            holder.address.setText(mList.get(position).streetAddress);
            holder.address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
        }
    }
}
