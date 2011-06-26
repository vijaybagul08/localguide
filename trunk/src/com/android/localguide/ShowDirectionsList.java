package com.android.localguide;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.localguide.GetDirectionsList.DirectionItem;

public class ShowDirectionsList extends LinearLayout implements GetDirectionsList.SearchResultCallBack{
	
	private TextView mNoContentDisplay;
	private ListView mListView;
	private ArrayList<String> mData;
	private EfficientAdapter mListAdapter;
	
	public ShowDirectionsList(Context context,Bundle bundle) {
		super(context);
		
		mNoContentDisplay = new TextView(context);
		mData = new ArrayList<String>();
		mListAdapter = new EfficientAdapter(context);
		
		mNoContentDisplay.setTextColor(Color.rgb(0x12, 0x10, 0x35e));
		mNoContentDisplay.setTextSize(40);
		mNoContentDisplay.setGravity(Gravity.CENTER);
		mNoContentDisplay.setText("Loading the directions....");
	
		mListView = new ListView(context);
		mListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		mListView.setDividerHeight(5);
		ColorDrawable d = new ColorDrawable(0x00000000);
		mListView.setDivider(d);
		mListView.setDrawSelectorOnTop(false);
		mListView.setSelector(context.getResources().getDrawable(R.color.app_background_color));
		
		
		LinearLayout.LayoutParams levelParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		
		mNoContentDisplay.setLayoutParams(levelParams);
		this.addView(mNoContentDisplay);
		
		GetDirectionsList obj = new GetDirectionsList(bundle.getString("location"),bundle.getString("currentaddress"),this);
		obj.searchRoutes();
		this.setBackgroundDrawable(context.getResources().getDrawable(R.color.app_background_color));
		
		}
	public void OnSearchCompleted(ArrayList<DirectionItem> list)
	{
		System.out.println("on show directions search completed ****************** ");
		if(list != null)
		{
			this.removeView(mNoContentDisplay);
		    for(int i =0;i<list.size();i++)
		    {
		    	
		    	String infoString;
		    	infoString = "Distance :"+list.get(i).distance+"\n";
		    	infoString += "Instruction :"+list.get(i).instructions;
		    	mData.add(infoString);
		    }
		    LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		    this.addView(mListView,Params);
		    mListView.setAdapter(mListAdapter);
		}
		else {
			
		}
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
	            	if((position+1)%2 == 0 )
	            		convertView = mInflater.inflate(R.layout.showdirectionlist_right_icon, null);
	            	else
	            		convertView = mInflater.inflate(R.layout.showdirectionlist_left_icon, null);
	            	
	                holder = new ViewHolder();
	                holder.direction = (TextView) convertView.findViewById(R.id.direction);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            
	            holder.direction.setText(mData.get(position));
	            return convertView;
	        }
	         class ViewHolder {
	            ImageView icon;
	            TextView direction;
	        }
	    }
	   
	   
	   private class MarkerIcon extends View {
		   
		   private Bitmap mMarker;
		   private Bitmap mRequiredMarker;
		   String markers[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		   Rect  mMarkerRect;
		   Paint mTextPaint;
		   
		   MarkerIcon(Context context,int pos ) {
			   super(context);
				mMarkerRect = new Rect(0,0,18,32);
				mMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
				mTextPaint = new Paint();
				mTextPaint.setAntiAlias(true);
				mTextPaint.setDither(true);
				mTextPaint.setColor(Color.rgb(0x12, 0x10, 0x5E));
				createMarkerIcon(pos);
		   }
		
		   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			   
			    int width = MeasureSpec.getSize(widthMeasureSpec);	
			    int height = MeasureSpec.getSize(heightMeasureSpec);	
				this.setMeasuredDimension(width, height);   
		   }
		   
		   public void createMarkerIcon(int mOveryLayItemsCount ) {
			    mRequiredMarker = Bitmap.createBitmap(18,32, Config.ARGB_8888);
				Canvas drawcanvas = new Canvas(mRequiredMarker);
				drawcanvas.drawBitmap(mMarker,null,mMarkerRect,mTextPaint);

				if(mOveryLayItemsCount == 8 ||mOveryLayItemsCount == 9 ||mOveryLayItemsCount == 14 ||mOveryLayItemsCount == 15)
					drawcanvas.drawText(markers[mOveryLayItemsCount], 8, 16, mTextPaint);
				else
					drawcanvas.drawText(markers[mOveryLayItemsCount], 6, 16, mTextPaint);
			   
			   
		   }
		   public void onDraw(Canvas canvas ) {
			   canvas.drawBitmap(mRequiredMarker,0,0,mTextPaint);
		   }
		   
	   }
}
