package com.mani.localguide;

import java.util.ArrayList;

import com.mani.localguide.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryListDialog extends Dialog {
	
		public interface onCategoryItemSelected {
			void onItemSelected(String category);
		};

		Context mContext;
		Dialog dialog;
		Typeface fontface;
		int screenWidth;
		CheckBox locationCheckbox;
		EditText locationTextbox;
		boolean isLocationChkBoxChecked;
		TextView mTitle;
		TextView mMessage;
		ArrayList<String> categoryContent;
		static Typeface mFont;
		static Typeface mFont1;	
		final String FONT_TTF = "quicksand_bold.ttf";	
		onCategoryItemSelected mCB;
		
		public CategoryListDialog(Context context,onCategoryItemSelected aCB )
		{
			super(context, R.style.getdirectionsdialog);	
			mContext = context;
			mCB = aCB;
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.categorydialog);
			Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
			screenWidth = display.getWidth();  
			int height = display.getHeight();

			categoryContent = new ArrayList<String>();
	        categoryContent.add(mContext.getString(R.string.pubs));
	        categoryContent.add(mContext.getString(R.string.restuarant));
			categoryContent.add(mContext.getString(R.string.shopping));
			categoryContent.add(mContext.getString(R.string.theatre));
			categoryContent.add(mContext.getString(R.string.train));
			categoryContent.add(mContext.getString(R.string.taxi));
			categoryContent.add(mContext.getString(R.string.gas));
			categoryContent.add(mContext.getString(R.string.police));
			categoryContent.add(mContext.getString(R.string.hospital));
			
            GridView gridview = (GridView)findViewById(R.id.gridview);   
            gridview.setAdapter(new ImageAdapter(mContext));   
            TextView title = (TextView)findViewById(R.id.text1);
            title.setText(mContext.getString(R.string.choose_category));
            title.setTypeface(getTypeface(mContext,FONT_TTF));
            
            gridview.setOnItemClickListener(new OnItemClickListener()   
       	    {
                public void onItemClick(AdapterView<?> parent, View v,int position, long id) {  
                	mCB.onItemSelected(categoryContent.get(position));
                    CategoryListDialog.this.dismiss();
                	}   
            });
            
            ImageView close = (ImageView)findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v){
					CategoryListDialog.this.dismiss();
				}
            });

			
			this.setCancelable(false);
		         
		}

		public static Typeface getTypeface(Context context, String typeface) {
		    if (mFont == null) {
		        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
		    }
		    return mFont;
		}

		public static Typeface getTypeface1(Context context, String typeface) {
		    if (mFont1 == null) {
		        mFont1 = Typeface.createFromAsset(context.getAssets(), typeface);
		    }
		    return mFont1;
		}
		
		public void show()
		{
			super.show();
		    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		    lp.width = screenWidth-10;
		    this.getWindow().setAttributes(lp);
	    }		

		   public class ImageAdapter extends BaseAdapter {   
		         private Context mContext;
		         private LayoutInflater mInflater;
		         public ImageAdapter(Context c) { 
		        	 mInflater = LayoutInflater.from(c);
		             mContext = c;   
		         }   
		         public int getCount() {   
		             return mThumbIds.length;   
		         }   
		         public Object getItem(int position) {   
		             return null;   
		         }   
		         public long getItemId(int position) {   
		             return 0;   
		         }   
		         // create a new ImageView for each item referenced by the   
		         public View getView(int position, View convertView, ViewGroup parent) {   
		        	 ViewHolder holder;
		             if (convertView == null) { 
		                  convertView = mInflater.inflate(R.layout.categorycontent, null);
		             	  convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		            	  holder = new ViewHolder();
		                  holder.title = (TextView) convertView.findViewById(R.id.categoryText);
		                  holder.icon = (ImageView )convertView.findViewById(R.id.categoryimage);
		                  convertView.setTag(holder);
		              } else {
		                  holder = (ViewHolder) convertView.getTag();
		              }
						holder.icon.setAdjustViewBounds(true);
						holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);   
						holder.icon.setPadding(8, 8, 8, 8);
						holder.title.setText(categoryContent.get(position));
						holder.title.setTypeface(getTypeface(mContext,"quicksand_book.ttf"));
						holder.icon.setImageResource(mThumbIds[position]);
						return convertView;   
		         }   
		         class ViewHolder {
		             TextView title;
		             ImageView icon;
		         }
		         // references to our images   
		         private Integer[] mThumbIds = {   
		                 R.drawable.beer, R.drawable.hotel,R.drawable.shopping, 
		                 R.drawable.theatre,R.drawable.train, R.drawable.taxi,   
		                 R.drawable.gas, R.drawable.police,R.drawable.hospital
		                 };
		     	}   
		
}
	
