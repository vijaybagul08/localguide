package com.android.localguide;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.localguide.GetDirectionsList.DirectionItem;

public class ShowDirectionsList extends LinearLayout implements GetDirectionsList.SearchResultCallBack{
	
	private TextView mNoContentDisplay;
	private ListView mListView;
	private ArrayList<String> mData;
	private EfficientAdapter mListAdapter;
	private LinearLayout mMainLayout;
	private Context mContext;
	static Typeface mFont;	
	final String FONT_TTF = "quicksand_bold.ttf";	
	
	public ShowDirectionsList(Context context,Bundle bundle) {
		super(context);
		mContext = context;
		mNoContentDisplay = new TextView(context);
		mData = new ArrayList<String>();
		mListAdapter = new EfficientAdapter(context);

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(
		        Context.LAYOUT_INFLATER_SERVICE);		 
		mMainLayout = (LinearLayout)inflater.inflate(R.layout.directions_list,mMainLayout);
		mListView = (ListView)mMainLayout.findViewById(R.id.directions_list);
		
		mNoContentDisplay.setTextColor(Color.rgb(0x12, 0x10, 0x35e));
		mNoContentDisplay.setTextSize(40);
		mNoContentDisplay.setGravity(Gravity.CENTER);
		mNoContentDisplay.setText(context.getString(R.string.load_direction));
		mNoContentDisplay.setTypeface(getTypeface(context,FONT_TTF));
		
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				
			}
			
		});
		
		LinearLayout.LayoutParams levelParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		
		mNoContentDisplay.setLayoutParams(levelParams);
		this.addView(mNoContentDisplay);
		
		GetDirectionsList obj = new GetDirectionsList(bundle.getString("location"),bundle.getString("currentaddress"),this);
		obj.searchRoutes();
		this.setBackgroundDrawable(context.getResources().getDrawable(R.color.app_background_color));
		
		}
	public void OnSearchCompleted(ArrayList<DirectionItem> list,int code)
	{
		if(list != null)
		{
			this.removeView(mNoContentDisplay);
		    for(int i =0;i<list.size();i++)
		    {
		    	
		    	String infoString;
		    	infoString = mContext.getString(R.string.distance)+" :"+list.get(i).distance+"\n";
		    	infoString += mContext.getString(R.string.instruction)+" :"+list.get(i).instructions;
		    	mData.add(infoString);
		    }
		    LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		    this.addView(mMainLayout,Params);
		    mListView.setAdapter(mListAdapter);
		}
		else {
			System.out.println("on show directions search completed ******************LIST NULL "+list);
		}
	}
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	
	   private  class EfficientAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;

	        public EfficientAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	        }

	        public int getCount() {
	            return mData.size();
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
	            	//if((position+1)%2 == 0 )
	            		//convertView = mInflater.inflate(R.layout.showdirectionlist_right_icon, null);
	            	//else
	            		convertView = mInflater.inflate(R.layout.showdirectionlist_left_icon, null);
	            	
	                holder = new ViewHolder();
	                holder.direction = (TextView) convertView.findViewById(R.id.direction);
	                holder.direction.setTypeface(getTypeface(mContext,FONT_TTF));
	                holder.icon = (MarkerIcon)convertView.findViewById(R.id.markerIcon);
	                
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            holder.icon.createMarkerIcon(position);
	            holder.direction.setText(Html.fromHtml(mData.get(position)));
	            return convertView;
	        }
	         class ViewHolder {
	            //ImageView icon;
	            MarkerIcon icon;
	            TextView direction;
	        }
	    }
}
