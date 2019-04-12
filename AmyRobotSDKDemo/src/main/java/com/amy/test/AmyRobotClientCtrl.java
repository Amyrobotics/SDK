package com.amy.test;

import com.amy.remote.client.BaseDataClientListener;
import com.amy.remote.common.constant.RemoteClientType;
import com.amy.remote.common.constant.RobotAction;
import com.amy.remote.common.constant.RobotNotifyCode;
import com.amy.remote.common.packet.PacketCode;
import com.amy.remote.common.utils.LogUtils;
import com.amyrobotics.remote.client.entity.RobotEvent;
import com.amyrobotics.remote.client.listener.RobotEventListener;
import com.amyrobotics.remote.client.manager.RobotClientMgr;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Leo on 2019/2/22.
 */
public class AmyRobotClientCtrl {
    private static final String TAG = AmyRobotClientCtrl.class.getSimpleName();

    public static final String DATA_HANDLER_CALLBACK_ID = "1000";

    public static final String ROBOT_IP = "192.168.1.178";
    public static final int ROBOT_PORT = 8889;

    RobotClientMgr mRobotClientMgr;

    String mRobotIP = ROBOT_IP;
    int mRobotPort = ROBOT_PORT;
    String mUserName = "amy";
    String cid = "20001001"; //my cid
    String dstCid = "5659846280901748"; //robot cid
    String passwd = null;

    RobotNavTest navTest;
    RobotTaskTest mRobotTaskTest;

    public void init() {
        //设置log打印
        LogUtils.setDebug(true);
        //设置打印所有
        LogUtils.setLogLevel(LogUtils.LOG_LEVEL_VERBOSE);

        mRobotClientMgr = RobotClientMgr.getInstance();

        mRobotClientMgr.addDataClientListener(DATA_HANDLER_CALLBACK_ID, new BaseDataClientListener() {

            @Override
            public void onConnected() {
                LogUtils.d(TAG, "onConnected");
            }

            @Override
            public void onRegisterResult(String srcCId, int code, String errInfo) {
                handleRegisterResult(srcCId, code, errInfo);
            }

            @Override
            public void onDisconnected() {
                LogUtils.d(TAG, "onDisconnected");
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(TAG, "onError", e);
            }

        });

        cid = UUID.randomUUID().toString();

        mRobotClientMgr.setClientType(RemoteClientType.CLIENT_TYPE_WEB);

        mRobotClientMgr.init(mRobotIP, mRobotPort, mUserName, cid, dstCid, passwd);

        LogUtils.d(TAG, "cid =" + cid + ", dstCid=" + dstCid);
        LogUtils.d(TAG, "mRobotIP =" + mRobotIP + ", " + mRobotPort);

        String mapRootDir = ".amy/robot/";

        mRobotClientMgr.setUseMapDir(mapRootDir);

        navTest = new RobotNavTest(mRobotClientMgr, dstCid);

        mRobotTaskTest = new RobotTaskTest(mRobotClientMgr, dstCid);

    }

    public void run() {
        init();

        start();

        Scanner sc = new Scanner(System.in);
        while(true) {
            String input = sc.nextLine();
            break;
        }

        stop();
    }

    public void start() {

        if (mRobotClientMgr.isRun()) {
            LogUtils.d(TAG, "isRun = true, must stop");
            return;
        }


        mRobotClientMgr.setRobotEventListener(new RobotEventListener() {
            @Override
            public void onRobotEvent(RobotEvent robotEvent) {
                handleRobotEvent(robotEvent);
            }
        });

        mRobotClientMgr.start();

        LogUtils.d(TAG, "start ok");
    }

    public void stop() {

        mRobotClientMgr.stop();

        while (mRobotClientMgr.isRun()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LogUtils.d(TAG, "stop ok");

    }

    private void handleRegisterResult(String cid, int code, String errInfo) {
        LogUtils.d(TAG, "handleRegisterResult " + cid + ", " + code + "," + errInfo);

        if (code == PacketCode.SUCCESS) {
            //注册成功
            LogUtils.d(TAG, "ok " + cid + ", " + code + "," + errInfo);

            sendAction();

        } else {
            //注册失败
            LogUtils.d(TAG, "failed " + cid + ", " + code + "," + errInfo);
        }

    }

    private void handleRobotEvent(RobotEvent robotEvent) {
        if (robotEvent.notifyAction == null) {
            return;
        }

        LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                + ", act=" + robotEvent.notifyAction
                + ", resultCode=" + robotEvent.resultCode
                + ", notifyInfo=" + robotEvent.notifyInfo
                + ", notifyParams=" + robotEvent.notifyParams
        );

        switch (robotEvent.notifyAction) {
            case RobotAction.START_NAVIGATION: {
                if (RobotNotifyCode.CODE_SUCCESS == robotEvent.resultCode) {
                    //成功
                    LogUtils.d(TAG, "START_NAVIGATION OK");

                } else {
                    if (RobotNotifyCode.CODE_ERROR_STOP_SWITCH_IS_OPEN == robotEvent.resultCode) {
                        LogUtils.d(TAG, "START_NAVIGATION OK");
                    } else {
                        //失败
                        LogUtils.d(TAG, "START_NAVIGATION error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                    }
                }
            }
            break;

            case RobotAction.NAV_TO_POINT: {
                if (RobotNotifyCode.CODE_SUCCESS == robotEvent.resultCode) {
                    LogUtils.d(TAG, "NAV_TO_POINT OK");

                } else {
                    LogUtils.d(TAG, "NAV_TO_POINT error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }
            }
            break;

            default:
                break;
        }
    }


    private void sendAction() {

        headReset();

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        headLeft();

//        navTest.getMapList();

//        navTest.getNavState();

        navTest.getSystemState();

        mRobotTaskTest.getTaskList();


    }

    public boolean sendAction(String action) {
        return sendAction(action, null);
    }

    public boolean sendAction(String action, Map<String, String> params) {
        return mRobotClientMgr.sendAction(action, params, null);
    }

    public boolean headReset() {
        return sendAction(RobotAction.TURN_HEAD_RESET);
    }

    public boolean headLeft() {
        return sendAction(RobotAction.TURN_HEAD_LEFT);
    }


    public static void main(String[] args) {
        new AmyRobotClientCtrl().run();

    }

}
