package com.axon.icloud.framework.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class LogTest
{
	/**
	 * write to HDFS successful! local file: /opt/hadoop/temp/3_add.log.2015-06-02-11, hdfs:/location/location-db4.csv
	 */
	private static final Pattern pattern = Pattern.compile("write to HDFS successful! local file: /opt/hadoop/temp/(.+), hdfs:/location/location-db4.csv");

	public static void main(String[] args)
	{
		BufferedReader reader = null;
		List<String> fileNameList = new LinkedList<String>();
		try
		{
			reader = new BufferedReader(new FileReader(new File("f:/import.log")));
			String line = null;
			while (true)
			{
				line = reader.readLine();
				if (line == null)
				{
					break;
				}
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
				{
					fileNameList.add(matcher.group(1));
				}
			}
			Collections.sort(fileNameList);
			for (String fileName : fileNameList)
			{
				System.out.println(fileName);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}
}
