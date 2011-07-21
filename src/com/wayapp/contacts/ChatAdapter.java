package com.wayapp.contacts;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wayapp.app.R;
import com.wayapp.tools.LogBean;
import com.wayapp.tools.TypeInfo;


/**
 * @author raubreak
 *
 */
public class ChatAdapter extends ArrayAdapter<LogBean> implements TypeInfo {

	private Vector<LogBean> list;
	private Activity context;
	private int id=0;
	TextView txt;
	TextView txtrecept;
	
	@SuppressWarnings("unchecked")
	ChatAdapter(Activity context, Vector<LogBean> list) {
		super(context, R.layout.chat_content, list);
		this.list = list;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		
		
//		Log.i("ChatAdapter","::::::::::::::::::::::::::::::");
		
		LayoutInflater vi = context.getLayoutInflater();
		v = vi.inflate(R.layout.chat_content, null);
		txt =  (TextView) v.findViewById(R.id.postview1);
		txtrecept = (TextView) v.findViewById(R.id.postrecept);

		AbsoluteLayout liz = (AbsoluteLayout) v.findViewById(R.id.linearLayoutsend);
		AbsoluteLayout lder = (AbsoluteLayout) v.findViewById(R.id.linearLayoutrecept);

		LogBean log = list.get(position);
		
//		Log.i("->",log.getTo()+" - " + log.getFrom() +" - " + log.getMessages() +" - " + log.getType());	

		// enviados
		if (log.getType() == 1) {
			v.setId(id);
			txtrecept.setVisibility(View.INVISIBLE);
			txt.setVisibility(View.VISIBLE);
			txt.setGravity(Gravity.LEFT);
			liz.setBackgroundColor(Color.LTGRAY);
	
			ImageView is_post_image = (ImageView) v.findViewById(R.id.status_post);
			is_post_image.setImageResource(R.drawable.no);
			if (log.isOfflineLog()) {
				is_post_image.setImageResource(R.drawable.ok);
			} else {
				is_post_image.setImageResource(R.drawable.no);
			}
			txt.setText(log.getMessages() );
			
		} else if (log.getType()==0) {

			txtrecept.setGravity(Gravity.RIGHT);
			txt.setVisibility(View.INVISIBLE);
			txtrecept.setVisibility(View.VISIBLE);
			txtrecept.setText(log.getMessages());

		}
		

		return v;
	}

}