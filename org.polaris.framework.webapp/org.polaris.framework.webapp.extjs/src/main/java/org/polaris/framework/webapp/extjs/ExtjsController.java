package org.polaris.framework.webapp.extjs;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extjs")
public class ExtjsController
{

	@RequestMapping(method = RequestMethod.GET)
	public Object test()
	{
		return "gagaga";
	}
}
