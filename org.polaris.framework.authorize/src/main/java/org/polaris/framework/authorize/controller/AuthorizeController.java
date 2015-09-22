package org.polaris.framework.authorize.controller;

import javax.annotation.Resource;

import org.polaris.framework.authorize.service.AuthorizeService;
import org.polaris.framework.authorize.vo.User;
import org.polaris.framework.common.rest.PagingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
public class AuthorizeController
{
	@Resource
	private AuthorizeService authorizeService;
	
	public PagingResult<User> userList()
	{
		return null;
	}
}
