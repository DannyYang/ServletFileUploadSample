package com.webcomm.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadRespVo {
	@JsonProperty
	private String success = "N";
	
	@JsonProperty
	private String msg = "";
	
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "UploadRespVo [success=" + success + ", msg=" + msg + "]";
	}
}
