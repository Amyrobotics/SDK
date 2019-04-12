package com.amy.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.amy.remote.common.constant.RobotAction;
import com.amy.remote.common.constant.RobotNotifyCode;
import com.amy.remote.common.entity.map.MapListEntity;
import com.amy.remote.common.entity.map.MarkPointEntity;
import com.amy.remote.common.entity.map.RobotPositionInfo;
import com.amy.remote.common.utils.LogUtils;
import com.amy.remote.common.utils.StringUtils;
import com.amyrobotics.remote.client.entity.RobotEvent;
import com.amyrobotics.remote.client.listener.ActionEventCallback;
import com.amyrobotics.remote.client.manager.RobotClientMgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo on 2019/2/25.
 */
public class RobotNavTest {
    private static final String TAG = RobotNavTest.class.getSimpleName();

    RobotClientMgr mRobotClientMgr;
    public String dstCid;

    public RobotNavTest(RobotClientMgr clientMgr, String dstCid) {
        this.dstCid = dstCid;
        mRobotClientMgr = clientMgr;
    }

    public void getMapList() {
        String action = RobotAction.GET_MAP_LIST;
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
                    if(robotEvent.notifyParams != null) {
                        MapListEntity mapListEntity = JSON.parseObject(robotEvent.notifyParams, MapListEntity.class);

                        LogUtils.d(TAG, "" + JSON.toJSONString(mapListEntity, SerializerFeature.PrettyFormat));

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

    public void startNav(String mapId) {
        String action = RobotAction.START_NAVIGATION;
        Map<String, String> params = new HashMap<>();
        params.put("mapId", mapId);

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "START_NAVIGATION ok");
                } else {
                    if (RobotNotifyCode.CODE_ERROR_STOP_SWITCH_IS_OPEN == robotEvent.resultCode) {
                        LogUtils.d(TAG, "START_NAVIGATION error CODE_ERROR_STOP_SWITCH_IS_OPEN ");
                    } else {
                        LogUtils.d(TAG, "START_NAVIGATION error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
                    }
                }
            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

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
            case RobotAction.NOTIFY_MSG_MAP_DATA: {
                if (RobotNotifyCode.CODE_SUCCESS == robotEvent.resultCode) {
                    try {
                        String data = robotEvent.notifyParams;

                        JSONObject jsonObject = JSONObject.parseObject(data);
                        String topic = jsonObject.getString("topic");
                        switch (topic) {
                            case RobotNavConstant.MAP_POSE:
                                JSONObject robotPositon = jsonObject.getJSONObject("msg");

                                RobotPositionInfo rpi = new RobotPositionInfo();
                                rpi.x = robotPositon.getDouble("x");
                                rpi.y = robotPositon.getDouble("y");
                                rpi.z = robotPositon.getDouble("z");

                                LogUtils.d(TAG, "onRobotEvent cid=" + robotEvent.cid
                                        + ", x=" + rpi.x
                                        + ", y=" + rpi.y
                                        + ", z=" + rpi.z
                                        + ", degree=" + Math.toDegrees(rpi.z)
                                );
                                break;
                        }

                    } catch (Exception e) {
                        LogUtils.d(TAG, "error " + e);
                    }

                }

            }
            break;
            default:
                break;
        }

    }

    public void stopNav() {
        String action = RobotAction.STOP_NAVIGATION;

        mRobotClientMgr.sendAction(dstCid, action, null, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);
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

    public void getNavState() {
        String action = RobotAction.GET_NAV_STATE;

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
                    LogUtils.d(TAG, "ok " + robotEvent.resultCode + ", " + robotEvent.notifyParams);

                    if(robotEvent.notifyParams != null) {
                        if(robotEvent.notifyParams.equals(IPCResponse.NAVI_START)) {
                            //导航开启
                            LogUtils.d(TAG, "NAVI_START");
                        } else if(robotEvent.notifyParams.equals(IPCResponse.NAVI_STOP)) {
                            //导航停止
                            LogUtils.d(TAG, "NAVI_STOP");
                        }

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

    public void getMarkPointList(String mapId) {
        String action = RobotAction.GET_MARK_POINT_LIST;

        Map<String, String> params = new HashMap<>();
        params.put("mapId", StringUtils.safe(mapId));

        mRobotClientMgr.sendAction(dstCid, action, params, new ActionEventCallback() {
            @Override
            public void onSuccess(RobotEvent robotEvent) {
                if(robotEvent.resultCode == RobotNotifyCode.CODE_OK) {
                    LogUtils.d(TAG, "ok " + robotEvent.resultCode + ", " + robotEvent.notifyInfo);

                    String data = robotEvent.notifyParams;
                    if (!StringUtils.isEmpty(data)) {
                        List<MarkPointEntity> markPointList = null;

                        try {
                            markPointList = JSON.parseArray(data, MarkPointEntity.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (MarkPointEntity markPointEntity : markPointList) {
                            LogUtils.d(TAG, "onRobotEvent name=" + markPointEntity.text
                                    + ", desc=" + markPointEntity.desc
                                    + ", isStartPoint=" + markPointEntity.isStartPoint
                                    + ", realX=" + markPointEntity.realX
                                    + ", realY=" + markPointEntity.realY
                                    + ", realAngle=" + markPointEntity.realAngle
                            );
                        }

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

    public void navToPoint(MarkPointEntity markPointEntity) {
        String action = RobotAction.NAV_TO_POINT;

        Map<String, String> params = new HashMap<>();
        params.put("x", String.valueOf(markPointEntity.realX));
        params.put("y", String.valueOf(markPointEntity.realY));
        params.put("z", String.valueOf(Math.toRadians(markPointEntity.realAngle)));

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
                    LogUtils.d(TAG, "ok IPCResponse.NAVI_ARRIVE ");

                } else {
                    LogUtils.d(TAG, "error " + robotEvent.resultCode + ", " + robotEvent.notifyInfo + ", " + robotEvent.notifyParams);

                    String data = robotEvent.notifyParams;
                    switch (data) {
                        case IPCResponse.NAVI_LOST:
                            break;
                        case IPCResponse.NAVI_GIVEUP:
                            break;
                        case IPCResponse.NAVI_TIMEOUT:
                            break;
                        case IPCResponse.NAVI_CANCLE_SUCCESS:
                            break;
                        case IPCResponse.NAVI_STOP:
                            break;
                    }

                }
            }

            @Override
            public void onFailed(int code, String msg, Throwable e) {
                LogUtils.d(TAG, "error " + code + ", " + msg + ", " + e);
            }
        });

    }

    public void getSystemState() {
        String action = RobotAction.ROBOT_GET_SYSTEM_STATE;

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
                    LogUtils.d(TAG, "ok " + robotEvent.resultCode + ", " + robotEvent.notifyParams);
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
