package com.oil.tool;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CommAction {

	// 获取当前请求IP
	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return Tool.getClientIp(request);
	}

	// 获取当前请求user-agent
	public String getUserAgent() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return Tool.getUserAgent(request);
	}

	public Object success(Object data, String msg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "0");
		map.put("msg", msg);
		map.put("data", data);
		return map;
	}
	
	public Object success(Object data){
		return success(data, "ok");
	}
	
	public Object success(){
		return success("", "ok");
	}
	
	public Object failure(Object data, String msg, Integer status){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", status + "");
		map.put("msg", msg);
		map.put("data", data);
		return map;
	}
	
	public Object failure(Object data){
		return failure(data, "failure", -1);
	}
	
	public Object failure(){
		return failure("", "failure", -1);
	}
}
