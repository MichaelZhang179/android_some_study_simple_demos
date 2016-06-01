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
	// ɨ��������������б�
	private List<ScanResult> mWifiList;
	// ���������б�
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;
	
	private static final int MIN_RSSI = -100;  
	private static final int MAX_RSSI = -55;  
	
	// ���弸�ּ��ܷ�ʽ��һ����WEP��һ����WPA������û����������
	public final static int WIFICIPHER_NOPASS = 1;
	public final static int WIFICIPHER_WEP = 2;
	public final static int WIFICIPHER_WPA = 3;
	
	public WifiAdmin(Context context) {
		// ȡ��WifiManager����
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// ȡ��WifiInfo����
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	// ��wifi
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	public boolean getWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	// �ر�wifi
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// ��鵱ǰwifi״̬
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// ����wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// ����wifiLock
	public void releaseWifiLock() {
		// �ж��Ƿ�����
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// ����һ��wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// �õ����úõ�����
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// ָ�����úõ������������
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// �������ú�ָ��ID������
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}

	public void startScan() {
		mWifiManager.startScan();
		// �õ�ɨ����
		mWifiList = mWifiManager.getScanResults();
		// �õ����úõ���������
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
	}

	// �õ������б�
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// �鿴ɨ����
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// ��ScanResult��Ϣת����һ���ַ�����, ���аѰ�����BSSID��SSID��capabilities��frequency��level
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

	// �õ����ӵ�ID
	public int getNetWorkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// �õ�wifiInfo��������Ϣ
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

	// ���һ�����粢����
	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		disConnectionWifi(getNetWorkId());
		boolean enable = mWifiManager.enableNetwork(wcgId, true);
		Log.d(TAG, "addNetWork -> enable = " + enable);
	}

	// �Ͽ�ָ��ID������
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
	
	// �鿴��ǰ�Ƿ�Ҳ���ù��������
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
