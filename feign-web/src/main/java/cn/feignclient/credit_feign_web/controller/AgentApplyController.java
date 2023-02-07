package cn.feignclient.credit_feign_web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.AgentService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AgentApplyInfoDomain;
import main.java.cn.untils.DateUtils;

@RestController
@RequestMapping("/web/agent")
public class AgentApplyController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(AgentApplyController.class);

	@Autowired
	private AgentService agentService;

	//代理商申请信息
	@RequestMapping("/agentApply")
	@ResponseBody
	public BackResult<String> agentApply(HttpServletRequest request, HttpServletResponse response,
			String companyName, String person,String phone ,String mail,String position) {
		//返回结果
		BackResult<String> result = new BackResult<String>();
		
		if (CommonUtils.isNotString(companyName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("公司名称不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(person)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("联系人不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(phone)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("电话不能为空");
			return result;
		}
		
		AgentApplyInfoDomain aaid = new AgentApplyInfoDomain();
		aaid.setCompanyName(companyName);
		aaid.setPerson(person);
		aaid.setPhone(phone);
		aaid.setMail(mail);
		aaid.setPosition(position);
		aaid.setStatus(0);
		aaid.setCreateTime(DateUtils.getNowDate());
		aaid.setUpdateTime(DateUtils.getNowDate());
		
		result = agentService.agentApply(aaid);
		return result;
	}
}
