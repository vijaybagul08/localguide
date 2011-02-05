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
import android.widget.AbsListView;
import android.widget.AdapterView;
import   android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.LocalGuideApplication.favoriteItem;

public class FavoritesScreen extends Activity{
	
	LocalGuideApplication app;
	ArrayList<favoriteItem> mList;
	
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.favorites);
		app = (LocalGuideApplication)this.getApplication();
		
		mList = app.getFavoritesList();

		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(new ListAdapter(this));
		
        list.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            	Intent intent = new Intent();
        		intent.setClass(FavoritesScreen.this, FavoritesResults.class);
        		Bundle bun = new Bundle();
                bun.putInt("position", position); 
                intent.putExtras(bun);
        		startActivity(intent);
        	}
        	
        }); 
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
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 100);
                convertView.setLayoutParams(params);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mList.get(position).title);
            holder.title.setTextColor(Color.rgb(0xbf, 0x6e, 0x46));
            holder.title.setTextSize(20);

            holder.address.setText(mList.get(position).streetAddress);
            holder.address.setTextColor(Color.rgb(0xbf, 0x6e, 0x46));
            holder.address.setTextSize(18);
            
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
        }
    }
}
