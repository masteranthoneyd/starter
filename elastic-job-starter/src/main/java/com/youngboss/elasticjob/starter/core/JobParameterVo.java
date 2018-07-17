package com.youngboss.elasticjob.starter.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ybd
 * @date 18-7-13
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true)
public class JobParameterVo {
	protected String distributedJobListener = CommonScheduleJobListener.class.getName();
	protected String jobName;
	protected String jobId;
	@JSONField(serialize = false)
	protected int shardingItem;
	@JSONField(serialize = false)
	protected boolean dynamic = true;


	public String toJson() {
		return JSONObject.toJSONString(this);
	}

	public static JobParameterVo of(String distributedJobListener) {
		return new JobParameterVo().setDistributedJobListener(distributedJobListener);
	}

	public static JobParameterVo of() {
		return new JobParameterVo();
	}
}
