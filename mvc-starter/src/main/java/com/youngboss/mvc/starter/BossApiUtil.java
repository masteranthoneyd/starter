package com.youngboss.mvc.starter;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ybd
 * @date 18-7-13
 * @contact yangbingdong1994@gmail.com
 */
public class BossApiUtil {

	private static final String RESULT_CODE = "resultCode";
	private static final String MESSAGE = "message";
	private static final String RESP_HEADER = "respHeader";
	private static final String RESP_BODY = "respBody";
	private static final String CUR_DATE = "curDate";
	private static JSONObject respHeader = new JSONObject();

	static {
		respHeader.put(MESSAGE, "正确执行");
		respHeader.put(RESULT_CODE, 0);
	}

	public static JSONObject wrapper(Object o){
		JSONObject json = new JSONObject();
		if (o != null) {
			JSONObject body = (JSONObject) JSONObject.toJSON(o);
			body.put(CUR_DATE, System.currentTimeMillis());
			json.put(RESP_BODY, body);
		}
		json.put(RESP_HEADER, respHeader);
		return json;
	}

	public static JSONObject wrapperError(String errorMsg){
		JSONObject json = new JSONObject();
		JSONObject errorRespHeader = new JSONObject();
		errorRespHeader.put(RESULT_CODE, 9999);
		errorRespHeader.put(MESSAGE, errorMsg);
		json.put(RESP_HEADER, errorRespHeader);
		return json;
	}
}
