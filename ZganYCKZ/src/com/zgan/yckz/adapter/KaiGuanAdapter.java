package com.zgan.yckz.adapter;

import java.util.List;

import com.zgan.yckz.R;
import com.zgan.yckz.tcp.Frame;
import com.zgan.yckz.tcp.FrameTools;
import com.zgan.yckz.tools.SheBei;
import com.zgan.yckz.tools.YCKZ_SQLHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class KaiGuanAdapter extends BaseAdapter {
	List<SheBei> list;
	// List<SheBei> list;
	Context context;
	Button delete;
	Button selector;
	TextView ID;
	/**
	 * 按下位置水平坐标
	 */
	float x;
	/**
	 * 放手位置水平坐标。
	 */
	float dx;
	
	

	YCKZ_SQLHelper yckz_SQLHelper;

	SQLiteDatabase db;

	public KaiGuanAdapter(Context context, List<SheBei> list) {
		this.context = context;
		this.list = list;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.list_item,
					null);
			viewHolder.shebei_name = (TextView) view
					.findViewById(R.id.shebei_name);
			viewHolder.shebei_id = (TextView) view.findViewById(R.id.shebei_id);
			viewHolder.btnDel = (Button) view.findViewById(R.id.del);
			viewHolder.btnSel = (Button) view.findViewById(R.id.sel);
			viewHolder.shebei_name.setText(list.get(position).getDeviceName());
			viewHolder.shebei_id.setText(list.get(position).getMAC());
		
			/*yckz_SQLHelper = new YCKZ_SQLHelper(context.getApplicationContext(), "yckz.db3", 1);
			db = yckz_SQLHelper.getReadableDatabase();*/
			
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();

		}
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				final ViewHolder holder = (ViewHolder) v.getTag();
				// 按下时处理
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					x = event.getX();

					// 判断之前是否出现了删除按钮
					if (selector != null || delete != null) {
						selector.setVisibility(View.GONE);
						delete.setVisibility(View.GONE);
						ID.setVisibility(View.VISIBLE);
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					dx = event.getX();
					if (holder.btnDel != null || holder.btnSel != null) {
						if (Math.abs(x - dx) > 20) {
							holder.btnDel.setVisibility(View.VISIBLE);
							delete = holder.btnDel;
							holder.btnSel.setVisibility(View.VISIBLE);
							selector = holder.btnSel;
							holder.shebei_id.setVisibility(View.GONE);
							ID = holder.shebei_id;
						}
					}

				}

				return true;
			}
		});
		viewHolder.btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (delete != null || selector != null) {
					delete.setVisibility(View.GONE);
					selector.setVisibility(View.GONE);
					ID.setVisibility(View.VISIBLE);
					/*db.delete("table_SubDev", "_MAC like?", new String[]{list.get(position).getMAC()});
					Frame f = new Frame();
					Log.i("获取离网设备信息", "获取离网设备信息");

					f.Platform = 4;
					f.MainCmd = 1;
					f.SubCmd = 3;
					f.strData = list.get(position).getMAC();
					FrameTools.toSendTcpData(f);*/
					
					list.remove(position);				
					
					notifyDataSetChanged();
				}
			}
		});
		return view;
	}
	
	


	final static class ViewHolder {
		TextView shebei_name;
		TextView shebei_id;

		Button btnDel;
		Button btnSel;
	}

}
