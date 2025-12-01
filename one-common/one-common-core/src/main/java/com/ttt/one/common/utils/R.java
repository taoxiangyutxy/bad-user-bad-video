package com.ttt.one.common.utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果封装类
 * 基于HashMap实现，支持链式调用
 */
public class 		R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/** 成功状态码 */
	public static final int SUCCESS_CODE = 0;
	/** 默认成功消息 */
	public static final String SUCCESS_MSG = "success";

	/**
	 * 利用fastjson进行类型转换
	 * @param key 数据键
	 * @param typeReference 目标类型
	 * @return 转换后的数据
	 */
	public <T> T getData(String key, TypeReference<T> typeReference){
		Object data = get(key);
		if(data == null){
			return null;
		}
		String jsonStr = JSON.toJSONString(data);
		return JSON.parseObject(jsonStr, typeReference);
	}

	/**
	 * 利用fastjson进行类型转换，默认获取"data"键的值
	 * @param typeReference 目标类型
	 * @return 转换后的数据
	 */
	public <T> T getData(TypeReference<T> typeReference){
		return getData("data", typeReference);
	}

	/**
	 * 设置data字段
	 * @param data 数据
	 * @return 当前对象，支持链式调用
	 */
	public R setData(Object data){
		put("data", data);
		return this;
	}

	/**
	 * 默认构造函数，初始化为成功状态
	 */
	public R() {
		put("code", SUCCESS_CODE);
		put("msg", SUCCESS_MSG);
	}

	/**
	 * 返回默认错误结果
	 * @return 错误结果对象
	 */
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}

	/**
	 * 返回指定消息的错误结果
	 * @param msg 错误消息
	 * @return 错误结果对象
	 */
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}

	/**
	 * 返回指定状态码和消息的错误结果
	 * @param code 错误码
	 * @param msg 错误消息
	 * @return 错误结果对象
	 */
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}
	public static R error(int code, String msg, Object data) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		r.put("data",data);
		return r;
	}

	/**
	 * 返回成功结果
	 * @return 成功结果对象
	 */
	public static R ok() {
		return new R();
	}

	/**
	 * 返回带自定义消息的成功结果
	 * @param msg 成功消息
	 * @return 成功结果对象
	 */
	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	/**
	 * 返回带数据的成功结果
	 * @param map 数据Map
	 * @return 成功结果对象
	 */
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	/**
	 * 重写put方法，支持链式调用
	 * @param key 键
	 * @param value 值
	 * @return 当前对象
	 */
	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	/**
	 * 获取返回码
	 * @return 返回码，如果不存在或类型不匹配则返回null
	 */
	public Integer getCode(){
		Object code = this.get("code");
		if(code instanceof Integer){
			return (Integer) code;
		}
		return null;
	}

	/**
	 * 获取返回消息
	 * @return 返回消息
	 */
	public String getMsg(){
		Object msg = this.get("msg");
		return msg != null ? msg.toString() : null;
	}

	/**
	 * 判断是否成功
	 * @return true-成功，false-失败
	 */
	public boolean isSuccess(){
		Integer code = getCode();
		return code != null && code == SUCCESS_CODE;
	}
}
