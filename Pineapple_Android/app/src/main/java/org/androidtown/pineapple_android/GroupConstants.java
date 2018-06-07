package org.androidtown.pineapple_android;

/**
 * Created by hanhb on 2018-04-12.
 */

public class GroupConstants {

    //Messages to Handler
    public static final int MSG_TOAST = 10;
    public static final int MSG_TEST = 11;
    public static final int MSG_BLUETOOTH_CONNECTED = 12;
    public static final int MSG_BLUETOOTH_DISCONNECTED = 13;
    public static final int MSG_TEST_BT = 14;


    //Activity Request Codes
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static final int REQ_CODE_BLUETOOTH_CONN= 101;

    //Speech codes
    public static final int INTENTION_DESTINATION = 1001;
    public static final int INTENTION_CANCELLATION = 1002;
    public static final int INTENTION_INVALID = 1003;
    public static final int INTENTION_CHATLOG = 1004;
    public static final int INTENTION_MAP = 1005;
    public static final int INTENTION_SEND_MESSAGE = 1006;

    //Message Type
    public static final int MY_MESSAGE = 1000;
    public static final int BOT_MESSAGE = 1001;
}
