package com.youngboss.elasticjob.starter.help;


import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * @author ybd
 * @date 18-7-16
 * @contact yangbingdong1994@gmail.com
 */
public class JobNameParser {
	private static final String SPLITER = "_";

	public static String parseName(Class<? extends SimpleJob> jobClass, Serializable jobId) {
		return jobClass.getSimpleName() + SPLITER + jobId;
	}

	@Nullable
	public static String getJobId(String jobName) {
		String[] split = jobName.split(SPLITER);
		if (split.length == 2) {
			return split[1];
		}
		return null;
	}
}
