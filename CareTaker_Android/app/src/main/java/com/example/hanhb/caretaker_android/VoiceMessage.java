package com.example.hanhb.caretaker_android;

import java.util.Calendar;

//시각장애인이 보낸 음성메시지 모델
public class VoiceMessage {

    private String message;
    private long timestamp;

    public VoiceMessage(){

    }

    public VoiceMessage(String message) {
        this.message = message;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
