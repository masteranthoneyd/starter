package com.youngboss.elasticjob.starter.help;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.youngboss.elasticjob.starter.core.Job;
import com.youngboss.elasticjob.starter.core.JobParameterVo;
import com.youngboss.elasticjob.starter.util.CronUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.youngboss.elasticjob.starter.core.JobType.SIMPLE;


/**
 * @author ybd
 * @date 18-7-16
 * @contact yangbingdong1994@gmail.com
 */
public class DynamicJobHelper<T extends SimpleJob> {
	private Class<T> jobClass;

	private DynamicJobHelper(Class<T> jobClass) {
		this.jobClass = jobClass;
	}

	public static <T extends SimpleJob> DynamicJobHelper<T> of(Class<T> jobClass) {
		return new DynamicJobHelper<>(jobClass);
	}

	public String getJobName(Serializable jobId) {
		return JobNameParser.parseName(jobClass, jobId);
	}

	public JobInfo buildJobInfo() {
		return new JobInfo().jobClass(jobClass);
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class JobInfo {
		private Class<? extends SimpleJob> jobClass;
		private LocalDateTime scheduleTime;
		private JobParameterVo jobParameterVo;
		private Serializable jobId;
		private int shard = 1;
		private boolean override = false;

		public Job build() {
			Objects.requireNonNull(scheduleTime);
			Objects.requireNonNull(jobParameterVo);
			Objects.requireNonNull(jobId);
			return new Job().setCron(CronUtils.getCron(scheduleTime))
							.setJobName(JobNameParser.parseName(jobClass, jobId))
							.setJobParameterVo(jobParameterVo)
							.setJobClass(jobClass)
							.setJobType(SIMPLE)
							.setShardingTotalCount(shard)
							.setOverwrite(override);
		}
	}
}
