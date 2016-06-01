package com.example.simples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simples.util.DropdownListView;
import com.example.simples.util.WifiAdmin;
import com.example.simples.util.DropdownListView.OnRefreshListener;

/**
 * 使用下拉刷新搜索wifi
 * @author Administrator
 */
public class DropDownListviewActivity extends Activity {
	private Context context = DropDownListviewActivity.this;
	
	private DropdownListView listView;
	private WifiAdmin wifiAdmin;
	private List<ScanResult> wifiList;
	private MyAdapter adapter;
	
	private List<String> group;
	private List<List<ScanResult>> child;
	private List<ScanResult> peopleFreeList, freeList, pswList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropdown_listview);
		wifiAdmin = new WifiAdmin(this);
		wifiList = new ArrayList<ScanResult>();
        group = new ArrayList<String>();
        group.add("码码密免费wifi");
        group.add("其他wifi");
        
        child = new ArrayList<List<ScanResult>>();
        peopleFreeList = new ArrayList<ScanResult>();
        freeList = new ArrayList<ScanResult>();
        pswList = new ArrayList<ScanResult>();

        RefreshList();

		listView = (DropdownListView) findViewById(R.id.dropdown_listview);
		adapter = new MyAdapter(); 
		listView.setAdapter(adapter);

		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}
						child.clear();
						wifiList.clear();
						peopleFreeList.clear();
						freeList.clear();
						pswList.clear();

						RefreshList();
						return null;
					}

					@Override
					protected void onPostExecute(Void para) {
						if(wifiList == null || wifiList.size() <= 0)
							return ;
						
						for (ScanResult result : wifiList) {
							if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]"))
								continue;
							if (result.SSID.toLowerCase().contains("mamami"))
								peopleFreeList.add(result);
							else if(!result.capabilities.toLowerCase().contains("wpa") && !result.capabilities.toLowerCase().contains("wep") )
								freeList.add(result);
							else
								pswList.add(result);
						}
						Collections.sort(peopleFreeList, new ScanResultComparator());
						Collections.sort(freeList, new ScanResultComparator());
						Collections.sort(pswList, new ScanResultComparator());

						child.add(peopleFreeList);
						pswList.addAll(0, freeList);
						child.add(pswList);
	        			for(int i = 0; i < group.size(); i++)
	        				listView.expandGroup(i);
						adapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}
				}.execute();
			}
		});
	}

	private void RefreshList() {
		wifiAdmin.startScan(); // 扫描wifi热点，前提是wifi已经打开
		wifiList = wifiAdmin.getWifiList();
	}
	
	class MyAdapter extends BaseExpandableListAdapter {
		@Override
		public ScanResult getChild(int groupPosition, int childPosition) {
			return child.get(groupPosition).get(childPosition) == null ? null : child.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewChildHolder holder = null;
			ScanResult scanResult = getChild(groupPosition, childPosition);
			if(scanResult != null) {
				if (convertView == null) {
					holder = new ViewChildHolder();
					convertView = LayoutInflater.from(context).inflate(R.layout.wifi_item, null);
					holder.img_wifi_type = (ImageView) convertView.findViewById(R.id.img_wifi_type);
					holder.tv_ssid = (TextView) convertView.findViewById(R.id.tv_ssid);
					holder.tv_psw_tag = (TextView) convertView.findViewById(R.id.tv_psw_tag);
					holder.img_signal = (ImageView) convertView.findViewById(R.id.img_signal);
					holder.tv_signal = (TextView) convertView.findViewById(R.id.tv_signal);
					convertView.setTag(holder);
				} else {
					holder = (ViewChildHolder) convertView.getTag();
				}
				holder.tv_psw_tag.setText("无密码");
				holder.img_wifi_type.setBackgroundResource(R.drawable.type_wifi_free);

				int nSigLevel = WifiAdmin.calculateSignalLevel(scanResult.level, 101);
				if(nSigLevel > 0 && nSigLevel <= 25)
					holder.img_signal.setBackgroundResource((R.drawable.ic_wifi_signal_1));
				else if(nSigLevel > 25 && nSigLevel <= 50)
					holder.img_signal.setBackgroundResource((R.drawable.ic_wifi_signal_2));
				else if(nSigLevel > 50 && nSigLevel <= 75)
					holder.img_signal.setBackgroundResource((R.drawable.ic_wifi_signal_3));
				else if(nSigLevel > 75 && nSigLevel <= 100)
					holder.img_signal.setBackgroundResource((R.drawable.ic_wifi_signal_4));
				
				holder.tv_signal.setText(nSigLevel + "%");
				holder.tv_ssid.setText(scanResult.SSID);
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return child.get(groupPosition) == null ? 0 : child.get(groupPosition).size();	
		}

		@Override
		public String getGroup(int groupPosition) {
			return group.get(groupPosition) == null ? null : group.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return group == null ? 0 : group.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ViewGroupHolder holder = new ViewGroupHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null);
			holder.img_wifi_type = (ImageView) convertView.findViewById(R.id.img_wifi_type);
			if(groupPosition == 0)
				holder.img_wifi_type.setBackgroundResource(R.drawable.type_people_wifi);
			else if(groupPosition == 1)
				holder.img_wifi_type.setBackgroundResource(R.drawable.type_other_wifi);
			
			holder.tv_wifi_group = (TextView) convertView.findViewById(R.id.tv_wifi_group);
			holder.tv_wifi_group.setText(getGroup(groupPosition).toString());
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	class ScanResultComparator implements Comparator<ScanResult> {
		@Override
		public int compare(ScanResult s1, ScanResult s2) {
			int signal1 = WifiAdmin.calculateSignalLevel(s1.level, 101);
			int signal2 = WifiAdmin.calculateSignalLevel(s2.level, 101);
			if(signal1 < signal2)
				return 1;
			else
				return -1;
		}
	}
	
	private class ViewGroupHolder {
		public ImageView img_wifi_type;
		public TextView tv_wifi_group;	// 目前就2个   码码密免费wifi && 其它wifi
	}
	
	private class ViewChildHolder {
		public ImageView img_wifi_type;
		public TextView tv_ssid;
		public TextView tv_psw_tag;
		public ImageView img_signal;
		public TextView tv_signal;
	}

}
