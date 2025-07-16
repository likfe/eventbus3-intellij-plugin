package com.likfe.ideaplugin.eventbus3.utils;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by likfe on 2018/3/6.
 */
public class Constants {

    public static final Boolean IS_DEBUG = true;

    public static final String ICON_PATH_EGRESS = "/icons/egress.svg";
    public static final String ICON_PATH_INGRESS = "/icons/ingress.svg";
    public static final Icon ICON_EGRESS = IconLoader.getIcon(Constants.ICON_PATH_EGRESS);
    public static final Icon ICON_INGRESS = IconLoader.getIcon(Constants.ICON_PATH_INGRESS);
    public static final int MAX_USAGES = 100;
    public static final String FUN_START = "EventBus.getDefault()";
    public static final String FUN_NAME = "post";
    public static final String FUN_NAME2 = "postSticky";
    public static final String FUN_ANNOTATION = "org.greenrobot.eventbus.Subscribe";
    public static final String FUN_ANNOTATION_KT = "Subscribe";
    public static final String FUN_EVENT_CLASS = "org.greenrobot.eventbus.EventBus";
    public static final String FUN_EVENT_CLASS_NAME = "EventBus";
    public static final String ANNO_POST_CLASS = "com.likfe.ideaplugin.eventbus3.Posting";

}
