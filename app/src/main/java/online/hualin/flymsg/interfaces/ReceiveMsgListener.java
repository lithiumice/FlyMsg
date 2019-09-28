package online.hualin.flymsg.interfaces;

import online.hualin.flymsg.data.ChatMessage;


public interface ReceiveMsgListener {
	boolean receive(ChatMessage msg);

}
