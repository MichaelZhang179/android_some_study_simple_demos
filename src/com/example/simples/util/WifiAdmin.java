package com.example.simples.util;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.util.Log;

public class WifiAdmin {
	private final String TAG = "wifiUtil";
	
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;
	
	private static final int MIN_RSSI = -100;  
	private static final int MAX_RSSI = -55;  
	
	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public final static int WIFICIPHER_NOPASS = 1;
	public final static int WIFICIPHER_WEP = 2;
	public final static int WIFICIPHER_WPA = 3;
	
	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	// 打开wifi
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	public boolean getWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	// 关闭wifi
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前wifi状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}

	public void startScan() {
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包, 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		return mWifiManager.getConnectionInfo().getBSSID();
//		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetWorkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到wifiInfo的所有信息
	public WifiInfo getWifiInfo() {
		return (mWifiInfo == null) ? null : mWifiInfo;
	}
	
	public String getConnectedWifiSSid() {
		String ssid = mWifiManager.getConnectionInfo().getSSID();
		if(!TextUtils.isEmpty(ssid))
			return ssid.replace('\"', ' ').trim();
		else
			return null;
	}

	// 添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		disConnectionWifi(getNetWorkId());
		boolean enable = mWifiManager.enableNetwork(wcgId, true);
		Log.d(TAG, "addNetWork -> enable = " + enable);
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	public WifiConfiguration createWifiInfo(String SSID, String BSSID, String Password, int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		
		WifiConfiguration tempConfig = isExsits(SSID, BSSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		
		if (Type == WIFICIPHER_NOPASS) {
//			config.wepKeys[0] = "";
//			config.wepTxKeyIndex = 0;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		} else if (Type == WIFICIPHER_WEP) {
//			config.preSharedKey = "\"" + Password + "\"";
			config.wepKeys[0]= "\""+Password+"\"";  
			config.hiddenSSID = true;
			config.BSSID = BSSID;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (Type == WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.BSSID = BSSID;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		} else {
			return null;
		}
		return config;
	}
	
	// 查看以前是否也配置过这个网络
	public WifiConfiguration isExsits(String ssid, String BSSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		try {
			for (WifiConfiguration existingConfig : existingConfigs) {
//				Log.i(TAG, "ssid = " + existingConfig.SSID + ", existingConfig.bssid = " + existingConfig.BSSID);
				if(existingConfig == null)
					continue;
				if(TextUtils.isEmpty(existingConfig.BSSID) || TextUtils.isEmpty(existingConfig.SSID))
					continue;
				if (existingConfig.BSSID.equals(BSSID) && existingConfig.SSID.equals("\"" + ssid + "\"")) {
					return existingConfig;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		try {
			for (WifiConfiguration existingConfig : existingConfigs) {
				if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
					return existingConfig;
				}
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * truen if removed success
	 * @param netId
	 * @return
	 */
	public boolean removeNetWork(String ssid) {
		WifiConfiguration tempConfig = isExsits(ssid);
		if (tempConfig != null)
			return mWifiManager.removeNetwork(tempConfig.networkId);
		else
			return false;
	}
	
	public void disconnectNetwork(String ssid) {
		WifiConfiguration tempConfig = isExsits(ssid);
		if (tempConfig != null) {
			mWifiManager.disableNetwork(tempConfig.networkId);
			mWifiManager.disconnect();
		}
	}
	
	public String getScanResultNetworkID(String ssid, String bssid) {
		WifiConfiguration tempConfig = isExsits(ssid, bssid);
		if(tempConfig != null)
			return "" +tempConfig.networkId;
		else
			return null;
	}
	
	public static int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			float inputRange = (MAX_RSSI - MIN_RSSI);
			float outputRange = (numLevels - 1);

			return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
		}
	}
		
}
