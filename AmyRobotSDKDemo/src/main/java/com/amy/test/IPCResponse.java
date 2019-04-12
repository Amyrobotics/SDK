package com.amy.test;

/**
 * Created by Leo on 2019/2/25.
 */
public interface IPCResponse {
    String START_MAP_SUCCESS = "#MAPS";
    String START_MAP_FAIL = "#MAPF";
    String POSE_SUCCESS = "#SETS";
    String POSE_FAILED = "#SETF";
    String SAVE_MAP_OK = "FINDM";
    String SAVE_MAP_ERROR = "NOM";
    String NAVI_ARRIVE = "#NAV01";
    String NAVI_START = "#NAV02";
    String NAVI_LOST = "#NAV03";
    String NAVI_GIVEUP = "#NAV04";
    String NAVI_TIMEOUT = "#NAV05";
    String NAVI_CANCLE_SUCCESS = "#NAV06";
    String NAVI_STOP = "#NAV07";
    String NAVIGATION_STATUS_ON = "#NAV02";
    String NAVIGATION_STATUS_OFF = "#NAV07";
    String BACK_DOCK_OK = "#DRST01";
    String BACK_DOCK_FAIL = "#DRST02";
    String BACK_DOCK_FIND_FAIL = "#DRST03";
    String BACK_DOCK_CANCEL = "#DRST07";
    String BACK_DOCK_TIMEUP = "#DRST08";
    String BACK_DOCK_ALREADY = "#DRST09";
    String CANCEL_BACK_DOCK_N = "#NDCK";
    String CANCEL_BACK_DOCK_OK = "#CDS";
    String STOP_SWITCH_OPEN = "#ESON";
    String STOP_SWITCH_CLOSE = "#ESOFF";
    String RESET_POS_OK = "#INPOS";
    String ERROR_INVALID_CMD = "error";
}
