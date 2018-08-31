package com.youngboss.elasticjob.starter.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

import static com.youngboss.elasticjob.starter.core.JobConstans.UPDATE_JOB_NAME;
import static java.util.Objects.nonNull;

/**
 * @author lzt
 */
@Slf4j
public class DeleteRegisterUpdateNodeJob implements SimpleJob {

	@Override
	public void execute(ShardingContext context) {
		JSONObject jsonObject = JSON.parseObject(context.getJobParameter());
		if (nonNull(jsonObject) && nonNull(jsonObject.getString(UPDATE_JOB_NAME))){
			log.info("Delete RegisterUpdateNode===== {}", jsonObject.getString(UPDATE_JOB_NAME));
		}
	}


}
