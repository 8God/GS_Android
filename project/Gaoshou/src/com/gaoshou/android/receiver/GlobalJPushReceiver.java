package com.gaoshou.android.receiver;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.gaoshou.android.activity.ExpertAcceptOrderActivity;
import com.gaoshou.android.activity.WaitExpertActivity;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.network.CommonJSONParser;
import com.gaoshou.common.utils.TypeUtil;

public class GlobalJPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("cth", "intent.getAction = " + action);
        Bundle extraBundle = intent.getExtras();
        if (null != extraBundle) {
            String title = extraBundle.getString(JPushInterface.EXTRA_TITLE);
            String message = extraBundle.getString(JPushInterface.EXTRA_MESSAGE);
            String contentType = extraBundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String extra = extraBundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.i("cth", "title = " + title);
            Log.i("cth", "message = " + message);
            Log.i("cth", "contentType = " + contentType);
            Log.i("cth", "extra = " + extra);

            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
                CommonJSONParser parser = new CommonJSONParser();
                Map<String, Object> extraMap = parser.parse(extra);
                Log.i("cth", "extraMap = " + extraMap);
                if (null != extraMap) {
                    String extraAction = TypeUtil.getString(extraMap.get(APIKey.COMMON_ACTION));
                    Log.i("cth", "extraAction = " + extraAction);
                    int id = TypeUtil.getInteger(extraMap.get(APIKey.COMMON_ID), -1);
                    if (!TextUtils.isEmpty(extraAction)) {
                        if ("consultation".equals(extraAction)) {
                            Log.i("cth", "extraAction.equals(consultation)");
                            Intent updateMyConsultation = new Intent();
                            updateMyConsultation.setAction(ExpertAcceptOrderActivity.ACTION_UPDATE_MY_CONSULTATION_LIST);
                            updateMyConsultation.putExtra(APIKey.COMMON_ID, id);
                            context.sendBroadcast(updateMyConsultation);
                        } else if ("order".equals(extraAction)) {
                            Intent updateWatingExpertList = new Intent();
                            updateWatingExpertList.setAction(WaitExpertActivity.FILTER_UPDATE_WATING_EXPERT_LIST);
                            context.sendBroadcast(updateWatingExpertList);
                        }
                    }
                }
            }
        }

    }
}
