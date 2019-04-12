package com.amy.test;

import com.alibaba.fastjson.JSON;
import com.amy.remote.common.constant.RobotAction;
import com.amy.remote.common.constant.RobotNotifyCode;
import com.amy.remote.common.entity.task.*;
import com.amy.remote.common.utils.LogUtils;
import com.amyrobotics.remote.client.entity.RobotEvent;
import com.amyrobotics.remote.client.listener.ActionEventCallback;
import com.amyrobotics.remote.client.manager.RobotClientMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo on 2019/3/7.
 */
public class RobotTaskTest {
    private static final String TAG = RobotTaskTest.class.getSimpleName();

    static final String TASK_SAVE = "robot.taskSave";
    static final String TASK_DELETE = "robot.taskDelete";
    static final String TASK_GET_LIST = "robot.getTaskList";

    RobotClientMgr mRobotClientMgr;
    public String dstCid;

    public RobotTaskTest(RobotClientMgr clientMgr, String dstCid) {
        this.dstCid = dstCid;
        mRobotClientMgr = clientMgr;
    }

    public void getTaskList() {
        String action = TASK_GET_LIST;

        mRobotClientMgr.sendAction(dstCid, action, null, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );

                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    if(robotEvent.notifyParams != null) {
                        LogUtils.d(TAG, "ok ");

                        RoamTaskListEntity roamTaskListEntity = JSON.parseObject(robotEvent.notifyParams, RoamTaskListEntity.class);

                        if(roamTaskListEntity.taskEntityList != null) {
                            for (RoamTaskEntity roamTaskEntity : roamTaskListEntity.taskEntityList) {
                                LogUtils.d(TAG, "" + JSON.toJSONString(roamTaskEntity));
                            }
                        }

                    } else {
                        LogUtils.d(TAG, "data error ");
                    }
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    public void createTask() {
        String taskEntityJson = createTaskEntityJSON(null);

        Map<String, String> params = new HashMap<>();
        params.put("mode", "create");
        params.put("taskEntity", taskEntityJson);

        mRobotClientMgr.sendAction(dstCid, TASK_SAVE, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );

                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    private String createTaskEntityJSON(String taskId) {
        String mapId = "069a6c1d-e0fb-4d5b-a8fa-5b4c9a0b2cf5";
        String mapName = "testMap";

        RoamTaskEntity roamTaskEntity = new RoamTaskEntity();
        roamTaskEntity.taskId = taskId;
        roamTaskEntity.name = "taskTest" ;
        roamTaskEntity.mapId = mapId;
        roamTaskEntity.mapName = mapName;
        roamTaskEntity.arriveDo = false;
        roamTaskEntity.cycleTimes = RoamTaskEntity.TASK_CYCLE_INFINITE;

        List<RoamPointEntity> roamPointEntityList = new ArrayList<>();
        roamTaskEntity.roamPointEntityList = roamPointEntityList;

        RoamPointEntity pointEntity;

        pointEntity = new RoamPointEntity();
        pointEntity.pointName = "MarkPoint1";

        pointEntity.action = TaskActions.TASK_ACTION_VOICE;
        pointEntity.strVal = "hello";
        pointEntity.params = new HashMap<>();
        pointEntity.params.put(TaskParams.VOICE_SHOWFACE, "1");

        roamPointEntityList.add(pointEntity);

        pointEntity = new RoamPointEntity();
        pointEntity.pointName = "MarkPoint2";
        pointEntity.action = TaskActions.TASK_ACTION_NONE;

        roamPointEntityList.add(pointEntity);

        pointEntity = new RoamPointEntity();
        pointEntity.pointName = "MarkPoint2";
        pointEntity.action = TaskActions.TASK_ACTION_AUDIO;
        String musicPath = "http://test/1.mp3";
        musicPath = "/sdcard/music/1.mp3";

        pointEntity.strVal = musicPath;
        roamPointEntityList.add(pointEntity);

        pointEntity = new RoamPointEntity();
        pointEntity.pointName = "MarkPoint2";
        pointEntity.action = TaskActions.TASK_ACTION_VIDEO;
        String videoPath = "http://test/1.mp4";
        //videoPath = "/sdcard/video/1.mp4";

        pointEntity.strVal = videoPath;
        roamPointEntityList.add(pointEntity);

        //createSpeechRecognition
        roamPointEntityList.add(createSpeechRecognition());

        String json = JSON.toJSONString(roamTaskEntity);

        return json;
    }

    RoamPointEntity createSpeechRecognition() {
        RoamPointEntity pointEntity;

        pointEntity = new RoamPointEntity();
        pointEntity.pointName = "MarkPoint1";

        pointEntity.action = TaskActions.TASK_ACTION_SPEECH_RECOGNITION;
        pointEntity.strVal = "hello | next | ok";

        return pointEntity;
    }


    public void modifyTask(String taskId) {
        if(taskId == null) {
            return;
        }

        String taskEntityJson = createTaskEntityJSON(taskId);

        Map<String, String> params = new HashMap<>();
        params.put("mode", "modify");
        params.put("taskEntity", taskEntityJson);

        mRobotClientMgr.sendAction(dstCid, TASK_SAVE, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });


    }

    public void deleteTask(String taskId) {
        String action = TASK_DELETE;

        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskId);

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }


    public void startTask(String taskId, int index) {
        String action = RobotAction.ROBOT_TASK_START;

        if(index < 0) {
            index = 0;
        }

        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("index", String.valueOf(index));

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    public void stopTask() {
        String action = RobotAction.ROBOT_TASK_STOP;

        Map<String, String> params = new HashMap<>();

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    public void pauseTask() {
        String action = RobotAction.ROBOT_TASK_PAUSE;

        Map<String, String> params = new HashMap<>();

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    public void resumeTask(String taskId) {
        String action = RobotAction.ROBOT_TASK_START;

        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskId);

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                        + ", act=" + robotEvent.notifyAction
                        + ", resultCode=" + robotEvent.resultCode
                        + ", notifyInfo=" + robotEvent.notifyInfo
                        + ", notifyParams=" + robotEvent.notifyParams
                );
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok ");
                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                }

            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

}
