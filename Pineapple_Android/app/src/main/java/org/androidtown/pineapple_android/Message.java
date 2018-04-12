package org.androidtown.pineapple_android;

/**
 * Created by hanhb on 2018-04-12.
 */

public class Message {

    private int MessageType;
    private String messageContent;
    private long timeStamp;

    public Message(int messageType, String messageContent) {
        MessageType = messageType;
        this.messageContent = messageContent;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
