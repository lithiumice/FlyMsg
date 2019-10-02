package online.hualin.flymsg.utils;

/*
 * 一些用到的常量
 *
 */
public class UsedConst {
	public static final int FILESENDSUCCESS  = 0xFF;	//文件发送成功
	public static final int FILERECEIVEINFO = 0xFE;		//接收文件，包含文件信息
	public static final int FILERECEIVESUCCESS = 0xFD;		//接收文件，包含文件信息

	public static final int FILERECEIVEFAIL = 224;		//接收文件fail
	public static final int UDPPORTFAIL = 225;		//接收文件fail
	public static final int FILESENDINFO = 226;		//接收文件fail
}