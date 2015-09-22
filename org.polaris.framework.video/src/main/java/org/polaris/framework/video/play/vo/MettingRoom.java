package org.polaris.framework.video.play.vo;

/**
 * 会议室(内存中保持,不作持久化)
 * 
 * @author wang.sheng
 * 
 */
public class MettingRoom
{
	/**
	 * 主键ID
	 */
	private String id;
	/**
	 * 开始时间(绝对时间,单位毫秒)
	 */
	private Long startTime;
	/**
	 * 结束时间(绝对时间,单位毫秒)
	 */
	private Long endTime;
	/**
	 * GarsiSession表主键
	 */
	private String sessionId;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	public Long getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Long endTime)
	{
		this.endTime = endTime;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

}
