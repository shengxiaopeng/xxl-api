package com.xxl.api.admin.controller;

import com.xxl.api.admin.controller.annotation.PermessionLimit;
import com.xxl.api.admin.core.model.ReturnT;
import com.xxl.api.admin.core.model.XxlApiProject;
import com.xxl.api.admin.core.model.XxlApiUser;
import com.xxl.api.admin.dao.IXxlApiBizDao;
import com.xxl.api.admin.dao.IXxlApiDocumentDao;
import com.xxl.api.admin.dao.IXxlApiProjectDao;
import com.xxl.api.admin.service.impl.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	@Resource
	private LoginService loginService;

	@Autowired
	private IXxlApiProjectDao projectDao;

	@Autowired
	private IXxlApiDocumentDao documentDao;

	@Autowired
	private IXxlApiBizDao xxlApiBizDao;

	@RequestMapping("/")
	public String index(Model model, HttpServletRequest request) {
		XxlApiUser loginUser = loginService.ifLogin(request);
		if (loginUser == null) {
			return "redirect:/toLogin";
		}

		int projectCount = projectDao.count();
		List<XxlApiProject> allProjects = projectDao.loadAll();
		int interfaceCount = documentDao.count();
		int operatorCount = documentDao.countOperator();
		List<String> operatorList = documentDao.allOperator();

        //大盘数据
		model.addAttribute("projectCount",projectCount);
		model.addAttribute("interfaceCount",interfaceCount);
		model.addAttribute("operatorCount",operatorCount);
		model.addAttribute("projectList",allProjects);
		model.addAttribute("operatorList",operatorList);

		// 业务线ID
		//model.addAttribute("bizId", bizId);
		// 业务线列表
		//List<XxlApiBiz> bizList = xxlApiBizDao.loadAll();
		//model.addAttribute("bizList", bizList);

		return "index";
	}
	
	@RequestMapping("/toLogin")
	@PermessionLimit(limit=false)
	public String toLogin(Model model, HttpServletRequest request) {
		XxlApiUser loginUser = loginService.ifLogin(request);
		if (loginUser != null) {
			return "redirect:/";
		}
		return "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String ifRemember, String userName, String password){
		// param
		boolean ifRem = false;
		if (StringUtils.isNotBlank(ifRemember) && "on".equals(ifRemember)) {
			ifRem = true;
		}

		// do login
		ReturnT<String> loginRet = loginService.login(response, userName, password, ifRem);
		return loginRet;
	}
	
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		loginService.logout(request, response);
		return ReturnT.SUCCESS;
	}
	
	@RequestMapping("/help")
	public String help() {
		return "help";
	}
	
}
