package com.mani.localguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.mani.localguide.R;
import com.mani.localguide.LocalGuideApplication.favoriteItem;

public class FavoritesScreen extends Activity{
	
	LocalGuideApplication app;
	ArrayList<favoriteItem> mList;
	ListView mListView;
	BaseAdapter mAdapter;
	TextView mNoElements;
	final String FONT_TTF = "quicksand_bold.ttf";
	Context mContext;
    static Typeface mFont;
    
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.favorites);
		app = (LocalGuideApplication)this.getApplication();
		mContext = this;
		
		mList = app.getFavoritesList();
		mNoElements = (TextView) findViewById(R.id.no_element);
		mListView = (ListView)findViewById(R.id.fav_list);
		mNoElements.setTypeface(getTypeface(this,FONT_TTF));

		mAdapter = new ListAdapter(this);
		mListView.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            	Intent intent = new Intent();
        		intent.setClass(FavoritesScreen.this, FavoritesResults.class);
            	intent.setAction("com.mani.favorites");
        		Bundle bun = new Bundle();
                bun.putInt("position", position);
                
                intent.putExtras(bun);
        		startActivity(intent);
        	}
        	
        }); 

		if(mList.size() == 0) {
			mNoElements.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}else {
			mNoElements.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(mAdapter);
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		mList = app.getFavoritesList();

		if(mList.size() == 0) {
			mNoElements.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}else {
			mNoElements.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(mAdapter);
		}
	}
	
	public void onBackPressed ()
	{
		 app.saveToDataBase();
		 this.finish();
	}

	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
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
            holder.title.setTypeface(getTypeface(mContext,FONT_TTF));
            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));

            holder.address.setText(mList.get(position).streetAddress);
            holder.address.setTypeface(getTypeface(mContext,FONT_TTF));
            holder.address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
        }
    }
}
