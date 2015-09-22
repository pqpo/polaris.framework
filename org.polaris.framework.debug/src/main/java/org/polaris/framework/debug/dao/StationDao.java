package org.polaris.framework.debug.dao;

import javax.annotation.Resource;

import org.polaris.framework.common.dao.HibernateTemplate;
import org.polaris.framework.common.rest.PagingResult;
import org.polaris.framework.debug.vo.Station;
import org.springframework.stereotype.Repository;

@Repository
public class StationDao
{
	@Resource
	private HibernateTemplate hibernateTemplate;

	public void insert(Station station)
	{
		hibernateTemplate.save(station);
	}

	public void update(Station station)
	{
		hibernateTemplate.update(station);
	}

	public void delete(String id)
	{
		hibernateTemplate.executeUpdate("delete from Station t where t.id=?", new Object[] { id });
	}

	public Station getStation(String id)
	{
		return hibernateTemplate.queryForObject("from Station t where t.id=?", new Object[] { id }, Station.class);
	}

	public Station find(int mcc, int mnc, int lac, int ci)
	{
		return hibernateTemplate.queryForObject("from Station t where t.mcc=? and t.mnc=? and t.lac=? and t.ci=?", new Object[] { mcc, mnc, lac, ci },
				Station.class);
	}

	public PagingResult<Station> getStations(int start, int length)
	{
		String hql = "from Station t order by t.mcc,t.mnc,t.lac,t.ci";
		long total = hibernateTemplate.getTotalCount(hql, null);
		Station[] results = hibernateTemplate.queryForArray(hql, start, length, null, Station.class);
		return new PagingResult<Station>(total, results);
	}
}
