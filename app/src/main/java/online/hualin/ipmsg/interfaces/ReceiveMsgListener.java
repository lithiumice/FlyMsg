package online.hualin.ipmsg.interfaces;

import online.hualin.ipmsg.data.ChatMessage;


public interface ReceiveMsgListener {
	boolean receive(ChatMessage msg);

}
