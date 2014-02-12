package com.zgan.yckz.activity;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

import com.zgan.yckz.R;
import com.zgan.yckz.fragment.PublicFragment;
import com.zgan.yckz.socket.Constant;
import com.zgan.yckz.socket.SanySocketClient;
import com.zgan.yckz.tcp.Frame;
import com.zgan.yckz.tcp.FrameTools;
import com.zgan.yckz.tools.SheBei;
import com.zgan.yckz.tools.YCKZ_Activity;
import com.zgan.yckz.tools.YCKZ_NetworkDetector;
import com.zgan.yckz.tools.YCKZ_SQLHelper;
import com.zgan.yckz.tools.YCKZ_Static;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class User_Login extends YCKZ_Activity {
	/**
	 * �ֻ���
	 */
	EditText go_tel;
	/**
	 * ����
	 */
	EditText go_pass;
	/**
	 * ��½
	 */
	ImageView go;
	/**
	 * ע��
	 */
	ImageView reg;
	/**
	 * һ���һ�����
	 */
	ImageView call_KF;
	
	public static  boolean success;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private static List<SanySocketClient> clientList;
	
	ProgressDialog dialog;
	List<String> list_id;
	List<String> list_details;
	List<SheBei> list_shebei;

	YCKZ_SQLHelper yckz_SQLHelper;
	SQLiteDatabase db;
	
	String mac = null;
	SheBei sheBei = new SheBei();
	
	public static String MAC=null;

	Handler ClientDatahandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case Constant.DATAERROR:
				System.out.println("NewSocketInfo  Constant.DATAERROR  "
						+ msg.obj);
				break;
			case Constant.CHECKCODEERROR:
				System.out.println("NewSocketInfo  Constant.CHECKCODEERROR "
						+ msg.obj);
				break;
			case Constant.NETERROR:
				System.out.println("NewSocketInfo  Constant.NETERROR  "
						+ msg.obj);
				break;
			case Constant.SERVERERROR:
				System.out.println("NewSocketInfo  Constant.SERVERERROR "
						+ msg.obj);
				break;
			case Constant.DATASUCCESS:
				Log.i("userlogin", "1");

				byte[] buffer;
				// buffer = new byte[Constant.MSG_NUM];
				// Arrays.fill(buffer, (byte)0);
				buffer = (byte[]) msg.obj;

				Log.i("buffer", "" + buffer);
				Frame frame = new Frame(buffer);
				System.out.println("��������...ƽ̨����" + frame.Platform);
				System.out.println("��������...�汾��" + frame.Version);
				System.out.println("��������...������������" + frame.MainCmd);
				System.out.println("��������...�ӹ���������" + frame.SubCmd);
				System.out.println("��������...����" + frame.strData);

				System.out.println("NewSocketInfo  Constant.DATASUCCESS "
						+ buffer);
				if (frame.MainCmd == 1 && frame.SubCmd == 4
						&& frame.Platform == 1) {
					String str = frame.strData.substring(1);

					Log.i("(f.strData).substring(2)", "" + str);
					String Port[] = str.split("-");
					if (Port.length > 1) {

						for (int i = 1; i < Port.length; i++) {

							Log.i("Port[" + i + "]", "" + Port[i]);
							String[] data = Port[i].split(":");
							/*for (int j = 0; j < data.length; j++) {
								
								}*/
							String strip = ("-" + data[0]);
							String ipport = int2ip(Long.parseLong(strip));							
							int nport=Integer.parseInt(data[1]);
							int serverId=Integer.parseInt(data[2].replaceAll("\\D+","").replaceAll("\r", "").replaceAll("\n", "").trim(),10);
							Log.i("ipport", "" + ipport);
							Log.i("nport", "" + nport);
							Log.i("serverId", "" + serverId);
							
							
							try {
								User_Login.startNewClient(ipport, nport, ClientDatahandler,
										11, serverId, Constant.INDEX_cbMessageVer);
								System.out.println("NewSocketInfo" + "21011");
								Thread.sleep(100);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

								System.out.println("NewSocketInfo" + "e.printStackTrace() "
										+ e.getMessage());
							

							}
							
							
							

						}
						if (success) {
							YCKZ_Static.Phone_number=go_tel.getText().toString();
							YCKZ_Static.USER_password=go_pass.getText().toString();
							//�˺����뱣�浽sharedperferce����
							editor.putString("user_name", YCKZ_Static.Phone_number);
							editor.putString("user_pas", YCKZ_Static.USER_password);
							editor.commit();
							Login();
						}
						
					}
					
				}
						
						
						if (frame.MainCmd == 1 && frame.SubCmd == 0
								&& "0".equals(frame.strData)) {
							
						}

						if (frame.MainCmd == 3 && frame.SubCmd == 0
								&& "0".equals(frame.strData)) {
							GetChildList();
						}
						if (frame.Platform == 9) {
						
							if (frame.MainCmd == 1 && frame.SubCmd == 0) {
								Log.i("f.strData", "" + frame.strData);
								Intent intent = new Intent();
								intent.putExtra("strData", frame.strData);
								intent.setAction("com.zgan.yckz");
								sendBroadcast(intent);
							}
							if (frame.MainCmd == 1 && frame.SubCmd == 4
								) {

								YCKZ_Static.ChildDeviceID = frame.strData.split("\t");
								list_id = new ArrayList<String>();
								list_shebei = new ArrayList<SheBei>();
								list_details = new ArrayList<String>();

								
								for (int i = 0; i < YCKZ_Static.ChildDeviceID.length; i++) {
									String userinfo = YCKZ_Static.ChildDeviceID[i];
									Log.i("�豸ID" + i, "" + userinfo);
									
									//list.add(publicFragment);
									String[] user = userinfo.split(",");
									list_id.add(userinfo);
									list_details = new ArrayList<String>();

									for (int j = 0; j < user.length; j++) {
										String SubDev = user[j];
										Log.i("�豸��Ϣ" + j, SubDev);
										list_details.add(SubDev);

									}
									

									if (list_details.size() == 9
											&& list_details.size() > 1) {									
										sheBei.setSubDevid(list_details.get(0));
										sheBei.setMAC(list_details.get(1));
										sheBei.setPort(list_details.get(2));
										sheBei.setProductNo(list_details.get(3));
										sheBei.setDeviceNo(list_details.get(4));
										sheBei.setDeviceName(list_details.get(5));
										sheBei.setStatus(list_details.get(6));
										sheBei.setRegTime(list_details.get(7));
										sheBei.setJobStatus(list_details.get(8));
										Log.i("list_details.get(0)",
												"" + list_details.get(0));
										Log.i("list_details.get(1)",
												"" + list_details.get(1));
										Log.i("list_details.get(2)",
												"" + list_details.get(2));
										Log.i("list_details.get(3)",
												"" + list_details.get(3));
										Log.i("list_details.get(4)",
												"" + list_details.get(4));
										Log.i("list_details.get(5)",
												"" + list_details.get(5));
										Log.i("list_details.get(6)",
												"" + list_details.get(6));
										Log.i("list_details.get(7)",
												"" + list_details.get(7));
										Log.i("list_details.get(8)",
												"" + sheBei.getJobStatus());
										list_shebei.add(sheBei);

									
										mac = list_details.get(1);
										Log.i("888888888888", mac);										
										Log.i("����������", "����������");
										
										InSertSQl(db, list_details.get(0),
												list_details.get(1),
												list_details.get(2),
												list_details.get(3),
												list_details.get(4),
												list_details.get(5),
												list_details.get(6),
												list_details.get(7),
												list_details.get(8));
										Cursor c = db
												.rawQuery(
														"select *from SubDevList where _MAC=?",
														new String[] { list_details
																.get(1) });
										mac=list_details.get(1);
										Log.i("888888888888", "888888888");
								if (c.moveToNext()) {
									Log.i("����������", "����������");

									UpdateSQL1(db, list_details.get(1),
											list_details.get(5));
								} else {
									Log.i("����������", "����������");

									InSertSQl1(db, list_details.get(1),
											list_details.get(5));
								}

								c.close();

									}
									Log.i("list_details.size()",
											"" + list_details.size());

								}
								Log.i("list_id.size()", "" + list_id.size());
								
								Intent intent = new Intent(User_Login.this,
										Index_Activity.class);
								startActivity(intent);
								dialog.dismiss();
								finish();
							}
							
						}

					
						break;

					}

				}

			
		
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		clientList = new ArrayList<SanySocketClient>();
		if (Integer.parseInt(VERSION.SDK) > 14
				|| Integer.parseInt(VERSION.SDK) == 14) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // or
																			// .detectAll()
																			// for
																			// all
																			// detectable
																			// problems
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
		preferences = getSharedPreferences("yckz_user", MODE_PRIVATE);
		editor = preferences.edit();
		
		dialog=new ProgressDialog(User_Login.this);
		yckz_SQLHelper = new YCKZ_SQLHelper(this, "yckz.db3", 1);
		db = yckz_SQLHelper.getReadableDatabase();
		
		db.execSQL("delete from  table_SubDev ");
		
		go_tel = (EditText) findViewById(R.id.go_tel);
		go_pass = (EditText) findViewById(R.id.go_pas);

		go = (ImageView) findViewById(R.id.go);
		reg = (ImageView) findViewById(R.id.reg);
		call_KF = (ImageView) findViewById(R.id.callke);

		go.setOnClickListener(listener);
		reg.setOnClickListener(listener);
		call_KF.setOnClickListener(listener);
		if (YCKZ_Static.Phone_number!=null && YCKZ_Static.USER_password!=null) {
			go_tel.setText(YCKZ_Static.Phone_number);
			go_pass.setText(YCKZ_Static.USER_password);
		}

		//Detectnetwork();
		/*try {
			//cloudlogin1.zgantech.com/192.168.1.72
			startNewClient("cloudlogin1.zgantech.com", 21000,
					ClientDatahandler, 11, Constant.LOGIN_SERVERPLATFROM,
					Constant.INDEX_cbMessageVer);
			System.out.println("NewSocketInfo" + "21000");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("NewSocketInfo" + "e.printStackTrace() "
					+ e.getMessage());
		}*/

	}

	public void SendTestInfo() throws Exception {
		// HandleClient();

		String strLoginInfo = go_tel.getText().toString()+"\t"+go_pass.getText().toString();
		byte[] logininfo = strLoginInfo.getBytes();

		// System.out.println("NewSocketInfo"+"#############################    "+logininfo);
		boolean bSend = SendData(Constant.LOGIN_SERVERPLATFROM, logininfo,
				strLoginInfo.length(), Constant.LOGIN, Constant.LOGINSUBCMD,
				Constant.VERSION);
		if (bSend) {
			System.out.println("NewSocketInfo  " + strLoginInfo);
		}

	}

	public static boolean SendData(int ServerID, byte[] cbData, int nDataSize,
			byte cbMainCmdID, byte cbSubCmdID, byte cbMessageVer) {
		if (clientList.size() > 0) {
			System.out.println("NewSocketInfo " + clientList.size());
			for (int i = 0; i < clientList.size(); i++) {
				// Log.i("for", "for");
				SanySocketClient con = clientList.get(i);

				if (con.getServerID() == ServerID && con.clientSocket != null
						&& !con.clientSocket.isClosed()) {
					System.out.println("NewSocketInfo"
							+ "  int i = 0; i < clientList.size(); i++   "
							+ con.getServerID());
					// System.out.println("NewSocketInfo"+"(con.getServerID() == ServerID && con.clientSocket != null && !con.clientSocket.isClosed()");

					boolean bSend = con.CrateSendBuffer(cbData, nDataSize,
							cbMainCmdID, cbSubCmdID, cbMessageVer);
					// Log.i("bSend", "bSend");
					if (bSend) {
						System.out.println("NewSocketInfo" + "  ���������     "
								+ con.getServerID() + "   ������   " + cbData);
					}
					return bSend;
				} else if (con.getServerID() == ServerID
						&& (con.clientSocket == null || con.clientSocket
								.isClosed())) {// ��������
					// System.out.println("NewSocketInfo"+
					// " int i = 0; i < clientList.size(); i++  con.isClosed   "+con.getServerID());

					System.out
							.println("NewSocketInfo"
									+ " con.getServerID() == ServerID && (con.clientSocket == null || con.clientSocket.isClosed())");

					String strip = con.getServerIp();
					int nPort = con.getServerport();
					Handler lhandler = con.getServerHandler();
					int nServer = con.getServerID();//
					int nplatfrom = con.GetPlatfrom();
					byte cbVersion = con.getVersion();

					HandleClient(i);
					if (nServer > Constant.LOGIN_SERVERPLATFROM) {
						SanySocketClient conn = new SanySocketClient(strip,
								nPort, lhandler);

						conn.setClientID(i);
						conn.setPlatfrom(nplatfrom);// ƽ̨����
						conn.SetServerID(ServerID);
						conn.setVersion(cbVersion);
						clientList.add(conn);
						try {
							success = conn.connect();
						} catch (StackOverflowError e) {
							success=false;
							//Toast.makeText(User_Login.this, "�����ѶϿ��������޷�ʹ�ã��������磡", 1).show();
							Log.e("�������StackOverflowError", "�������");
							e.printStackTrace();
						}catch(Exception e)
						{
							success=false;
							//Toast.makeText(context, "�����ѶϿ��������޷�ʹ�ã��������磡", 1).show();
							Log.e("�������Exception", "�������");
							e.printStackTrace();
						}

						System.out.println("NewSocketInfo" + "��������״̬Ϊ:"
								+ success);
						// ÿ�η���֮����Ҫʱ���� ������֤
						// Thread.sleep(100);
						byte cbMainCmd = 0;
						if (nServer == 1) {
							cbMainCmd = 1;
						} else if (nServer == Constant.MSG_SERVERPLATFROM) {
							cbMainCmd = Constant.MSG_SVRID;
						} else if (nServer == Constant.FILE_SERVERPLATFROM) {
							cbMainCmd = Constant.FILE_SVRID;
						} else if (nServer == 9) {
							cbMainCmd = 0x03;
						}
						String strLoginInfo = "89898989";
						byte[] info = strLoginInfo.getBytes();
						boolean bSend = SendData(nServer, info,
								strLoginInfo.length(), cbMainCmd,
								Constant.USERID, Constant.VERSION);
						if (bSend) {
							System.out
									.println("NewSocketInfo  "
											+ strLoginInfo
											+ "��������     info,strLoginInfo.length(),conn sutdown,Constant.USERID,Constant.VERSION");

							bSend = conn.CrateSendBuffer(cbData, nDataSize,
									cbMainCmdID, cbSubCmdID, cbMessageVer);
							if (bSend)
								System.out.println("NewSocketInfo" + "  ���������"
										+ conn.getServerID() + "   ������   "
										+ cbData);
						}
					}
				}
			}
		}
		return false;
	}

	public static void HandleClient(int i) {
		if (clientList.size() > i) {
			// SanySocketClient con = clientList.get(i);
			clientList.remove(i);
		}
	}

	public static void setHnadler(Handler handler) {
		if (clientList.size() > 0) {
			for (int i = 0; i < clientList.size(); i++) {
				SanySocketClient con = clientList.get(i);
				con.setHandler(handler);
				System.out.println("NewSocketInfo"
						+ "setHnadler(Handler handler)");
			}
		}else{
			
		}
	}

	public static void startNewClient(String strip, int nport, Handler handler,
			int nPlatfrom, int ServerID, byte cbVersion) throws Exception {
		SanySocketClient con = new SanySocketClient(strip, nport, handler);
		if (clientList.size() > 0)
			con.setClientID(clientList.size());
		else
			con.setClientID(0);
		con.setPlatfrom(nPlatfrom);
		con.SetServerID(ServerID);
		con.setVersion(cbVersion);
		clientList.add(con);
		success = con.connect();
		System.out.println("NewSocketInfo" + "��������״̬Ϊ:" + success);
		// ÿ�η���֮����Ҫʱ����
		Thread.sleep(100);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.go:
				dialog.setMessage("���ڵ�½...���Ժ�");
				dialog.show();
				try {
					//cloudlogin1.zgantech.com/192.168.1.72
					startNewClient("cloudlogin1.zgantech.com", 21000,
							ClientDatahandler, 11, Constant.LOGIN_SERVERPLATFROM,
							Constant.INDEX_cbMessageVer);
					System.out.println("NewSocketInfo" + "21000");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					System.out.println("NewSocketInfo" + "e.printStackTrace() "
							+ e.getMessage());
				}
				
				
				if (go_tel.getText() != null && go_pass.getText() != null
						&& !"".equals(go_tel.getText().toString())
						&& !"".equals(go_pass.getText().toString())) {
					Log.i("��½��", "��½��");
					try {
						SendTestInfo();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						System.out.println("NewSocketInfo" + "e.printStackTrace() "
								+ e.getMessage());
					}
				} else {

				}

				break;

			case R.id.reg:
				Intent intent = new Intent(User_Login.this, User_Reg.class);
				startActivity(intent);
				break;
			case R.id.callke:
				Intent intent2 = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + "10086"));
				startActivity(intent2);
				break;
			}
		}
	};
	public static void GetChildList() {
		// TODO Auto-generated method stub

		try {

			User_Login.SendData(9, null, 0, Constant.List_cbMainCmdID,
					Constant.List_cbSubCmdID, Constant.INDEX_cbMessageVer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("NewSocketInfo" + "e.printStackTrace() "
					+ e.getMessage());
		}
	}


	public static String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}
	/**
	 * ��������
	 * @param db
	 * @param _SubDev
	 * @param _MAC
	 * @param _Port
	 * @param _ProductNo
	 * @param _DeviceNo
	 * @param _DeviceName
	 * @param _Status
	 * @param _RegTime
	 * @param _JobStatus
	 */
	public void UpdateSQL(SQLiteDatabase db, String _SubDev, String _MAC,
			String _Port, String _ProductNo, String _DeviceNo,
			String _DeviceName, String _Status, String _RegTime,
			String _JobStatus) {
		Log.i("���ݿ����", "���ڲ�������");
		
		ContentValues cv = new ContentValues();
		cv.put("_SubDvId", _SubDev);
		cv.put("_Port", _Port);
		cv.put("_ProductNo", _ProductNo);
		cv.put("_DeviceNo", _DeviceNo);
		cv.put("_DeviceName", _DeviceName);
		cv.put("_Status", _Status);
		cv.put("_RegTime", _RegTime);
		cv.put("_JobStatus", _JobStatus);
		String[] args = { String.valueOf(_MAC) };
		db.update("table_SubDev", cv, "_MAC=?", args);
		Log.i("���ݿ����", "�����������");

		// TODO Auto-generated method stub

	}
	public void UpdateSQL1(SQLiteDatabase db,  String _MAC,
			String _DeviceName) {
		Log.i("���ݿ����", "���ڲ�������");
		
		ContentValues cv = new ContentValues();	
		cv.put("_DeviceName", _DeviceName);
		
		String[] args = { String.valueOf(_MAC) };
		db.update("SubDevList", cv, "_MAC=?", args);
		Log.i("���ݿ����", "�����������");

		// TODO Auto-generated method stub

	}
	/**
	 * ��������
	 * @param db
	 * @param _SubDev
	 * @param _MAC
	 * @param _Port
	 * @param _ProductNo
	 * @param _DeviceNo
	 * @param _DeviceName
	 * @param _Status
	 * @param _RegTime
	 * @param _JobStatus
	 */
	public  void InSertSQl(SQLiteDatabase db, String _SubDev, String _MAC,
			String _Port, String _ProductNo, String _DeviceNo,
			String _DeviceName, String _Status, String _RegTime,
			String _JobStatus) {
		// TODO Auto-generated method stub
		Log.i("���ݿ����", "���ڲ�������");
		
		db.execSQL("insert into table_SubDev values(null,?,?,?,?,?,?,?,?,?)",
				new String[] { _SubDev, _MAC, _Port, _ProductNo, _DeviceNo,
						_DeviceName, _Status, _RegTime, _JobStatus });
		Log.i("���ݿ����", "�����������");

	}
	public void InSertSQl1(SQLiteDatabase db,  String _MAC,
			String _DeviceName) {
		// TODO Auto-generated method stub
		Log.i("���ݿ����", "���ڲ�������");
		
		db.execSQL("insert into SubDevList values(null,?,?)",
				new String[] {  _MAC,  _DeviceName, });
		Log.i("���ݿ����", "�����������");

	}
	
	public static void Login(){
		try {
			byte[] logininfo;

			String strLoginInfo = YCKZ_Static.Phone_number;
			logininfo = strLoginInfo.getBytes();
			User_Login.SendData(9, logininfo, strLoginInfo.length(),
					Constant.INDEX_cbMainCmdID, /* (byte)0x21 */
					Constant.INDEX_cbSubCmdID, Constant.INDEX_cbMessageVer);

			Thread.sleep(100);
			
			logininfo = strLoginInfo.getBytes();
			User_Login.SendData(7, logininfo, strLoginInfo.length(),
					Constant.INDEX_bjMainCmdID, /* (byte)0x21 */
					Constant.INDEX_bjSubCmdID, Constant.INDEX_cbMessageVer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("NewSocketInfo" + "e.printStackTrace() "
					+ e.getMessage());
		}
	}
	

	  protected void Detectnetwork() { // TODO Auto-generated method stub
	  Boolean networkState = YCKZ_NetworkDetector.detect(User_Login.this);
	 
	 if (!networkState) { new AlertDialog.Builder(this) .setTitle("�������")
	  .setMessage("��������ʧ�ܣ���ȷ����������") .setPositiveButton( "ȷ��", new
	  android.content.DialogInterface.OnClickListener() {
	  
	  @Override public void onClick(DialogInterface arg0, int arg1) {
		  // TODO Auto-generated method stub
	 arg0.dismiss(); } }).show(); } }
	  @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		  if(db!=null){
			  db.close();
		  }
		  if (yckz_SQLHelper!=null) {
			  yckz_SQLHelper.close();
		}
		super.onDestroy();
	}
	
}