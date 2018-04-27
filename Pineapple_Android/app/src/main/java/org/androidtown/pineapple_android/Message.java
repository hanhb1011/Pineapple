package org.androidtown.pineapple_android;

/**
 * Created by hanhb on 2018-04-12.
 */

public class Message {

    private int messageType;
    private String messageContent;
    private long timeStamp;

    public Message(int messageType, String messageContent, long timeStamp) {
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.timeStamp = timeStamp;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        messageType = messageType;
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
