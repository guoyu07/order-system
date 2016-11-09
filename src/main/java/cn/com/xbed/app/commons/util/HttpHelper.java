package cn.com.xbed.app.commons.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.enu.HTTPMethod;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

public class HttpHelper {
	private static final String DEFAULT_COMMUNICATE_CHARSET = PropertiesReader.consvars.get("charset.ws");// 约定请求参数和响应都使用这种字符编码
	private static final String DEFAULT_KEY = "json_data";
	private static ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(HttpHelper.class));

	/**
	 * 发出一个HTTP POST请求，使用默认的请求参数的key值(服务端用此key获得请求参数)和默认的字符编码解码.
	 * 
	 * @param url
	 *            请求的URL
	 * @param jsonMsg
	 *            请求value值
	 * @return 服务端返回值
	 */
	public static String requestByPostWithJsonParam(String url, String jsonMsg) {
		return requestByPostWithJsonParam(url, DEFAULT_KEY, jsonMsg, DEFAULT_COMMUNICATE_CHARSET);

	}

	/**
	 * 发出一个HTTP POST请求
	 * 
	 * @param url
	 *            请求的URL
	 * @param jsonMsgKey
	 *            请求key值
	 * @param jsonMsg
	 *            请求value值
	 * @param charset
	 *            value值的字符编码以及返回值的解码
	 * @return 服务端返回值
	 */
	public static String requestByPostWithJsonParam(String url, String jsonMsgKey, String jsonMsg, String charset) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair(jsonMsgKey, jsonMsg));
			// httpPost.setEntity(new UrlEncodedFormEntity(formparams,
			// charset));
			StringEntity stringEntity = new StringEntity(jsonMsgKey + "=" + jsonMsg, charset);
			httpPost.setEntity(stringEntity);// 这里用什么编码，在服务端req.getCharacterEncoding()或req.getContentType()都会体现这里的编码
			response = httpclient.execute(httpPost);
			if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity, charset);
				EntityUtils.consume(entity);
				return ret;
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw exceptionHandler.logToolException(e);
			}
		}
		return null;
	}

	/**
	 * 默认必传参数
	 * @param req
	 * @param optional
	 * @return
	 */
	public static String getDefaultJsonString(HttpServletRequest req, boolean...optional) {
		try {
			String s = req.getParameter(AppConstants.JSON_KEY);
			exceptionHandler.getLog().info("请求JSON入参(before):" + s);
			if (optional.length == 0 || !optional[0]) {
				if (s == null || s.length() == 0) {
					throw new RuntimeException("json_data传参为空:" + s);// 省得老问什么原因
				}
			}
			if (s != null) {
				s = URLDecoder.decode(s, "UTF-8");
			}
			return s;
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}

	/**
	 * 用ISO-8859-1的形式过来的
	 * 
	 * @param req
	 * @return
	 */
	public static String getJsonStringEncodedbyIso(HttpServletRequest req) {
		try {
			String before = req.getParameter(AppConstants.JSON_KEY);
			String after = new String(before.getBytes("ISO-8859-1"), "UTF-8");
			return after;
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}

	/**
	 * 用ISO-8859-1的形式过来的
	 * 
	 * @param req
	 * @return
	 */
	public static void setRespAccessControl(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
	}

	/**
	 * 组装URL<br>
	 * 
	 * @param url
	 *            原始的请求url,不要带问号后的参数部分
	 * @param params
	 *            键值对的字符串,键值都会进行URL编码,所以key支持有空格的特殊key,value也支持中文. 没有参数可以传null
	 * @return
	 */
	public static String getFullUrl(String url, Map<String, Object> params) {
		StringBuffer fullUrl;
		try {
			fullUrl = new StringBuffer(url);
			if (params != null) {
				Set<String> set = params.keySet();
				if (!set.isEmpty()) {
					fullUrl.append("?");
					for (String key : set) {
						Object value = params.get(key);
						fullUrl.append(URLEncoder.encode(key, DEFAULT_COMMUNICATE_CHARSET)).append("=")
								.append(URLEncoder.encode(value.toString(), DEFAULT_COMMUNICATE_CHARSET)).append("&");
					}
					if (fullUrl.length() > 0) {
						fullUrl.deleteCharAt(fullUrl.length() - 1);
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
		return fullUrl.toString();
	}

	/**
	 * GET请求某路径<br>
	 * 已经测试带中文特殊字符的情况
	 * 
	 * @param url
	 *            不能带问号后的参数
	 * @param params
	 *            无参数可以传null
	 * @return
	 */
	protected static String callRemoteMethodGET(String url, Map<String, Object> params, Header... headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			url = getFullUrl(url, params);
			HttpGet httpGet = new HttpGet(url);
			for (Header header : headers) {
				httpGet.setHeader(header);
			}
			response = httpclient.execute(httpGet);
			//if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
				EntityUtils.consume(entity);
				return ret;
			//}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw exceptionHandler.logToolException(e);
			}
		}
		//return null;
	}

	/**
	 * POST请求某路径<br>
	 * 已经测试中文参数问题
	 * 
	 * @param url
	 *            不能带问号后的参数
	 * @param params
	 *            无参数可以传null
	 * @return
	 */
	protected static String callRemoteMethodPOST(String url, Map<String, Object> params, Header... headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			for (Header header : headers) {
				httpPost.setHeader(header);
			}
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String key : params.keySet()) {
					Object value = params.get(key);
					formparams.add(new BasicNameValuePair(key, value.toString()));
				}
			}
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(formparams, DEFAULT_COMMUNICATE_CHARSET);
			httpPost.setEntity(encodedFormEntity);
			response = httpclient.execute(httpPost);
			//if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
				EntityUtils.consume(entity);
				return ret;
				//}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw exceptionHandler.logToolException(e);
			}
		}
		//return null;
	}

	/**
	 * PUT请求某路径<br>
	 * 
	 * @param url
	 *            不能带问号后的参数
	 * @param params
	 *            无参数可以传null
	 * @return
	 */
	protected static String callRemoteMethodPUT(String url, Map<String, Object> params, Header... headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			url = getFullUrl(url, params);
			HttpPut httpPut = new HttpPut(url);
			for (Header header : headers) {
				httpPut.setHeader(header);
			}
			response = httpclient.execute(httpPut);
			//if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
				EntityUtils.consume(entity);
				return ret;
			//}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw exceptionHandler.logToolException(e);
			}
		}
		//return null;
	}

	protected static String callRemoteMethodDELETE(String url, Map<String, Object> params, Header... headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			url = getFullUrl(url, params);
			HttpDelete httpDelete = new HttpDelete(url);
			for (Header header : headers) {
				httpDelete.setHeader(header);
			}
			response = httpclient.execute(httpDelete);
			//if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
				EntityUtils.consume(entity);
				return ret;
				//}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw exceptionHandler.logToolException(e);
			}
		}
		//return null;
	}

	public static String callRemoteMethod(String url, Map<String, Object> params, HTTPMethod httpMethod, HeaderPair... headerPairs) {
		Header[] headers = new Header[headerPairs.length];
		int i = 0;
		for (HeaderPair headerPair : headerPairs) {
			Header header = new BasicHeader(headerPair.getHeaderName(), headerPair.getHeaderValue());
			headers[i] = header;
			i++;
		}
		String ret = null;
		switch (httpMethod) {
		case GET:
			ret = callRemoteMethodGET(url, params, headers);
			break;
		case POST:
			ret = callRemoteMethodPOST(url, params, headers);
			break;
		case PUT:
			ret = callRemoteMethodPUT(url, params, headers);
			break;
		case DELETE:
			ret = callRemoteMethodDELETE(url, params, headers);
			break;
		}
		return ret;
	}

	public static class HeaderPair {
		private String headerName;
		private String headerValue;

		public HeaderPair(String headerName, String headerValue) {
			super();
			this.headerName = headerName;
			this.headerValue = headerValue;
		}

		public String getHeaderName() {
			return headerName;
		}
		public String getHeaderValue() {
			return headerValue;
		}

	}
	public static void main(String[] args) {
		String url = "http://localhost:8080/xbedservice/test/test";
		Map<String, Object> params = new HashMap<>();
		params.put("json_data", "{lodgerId:2,name:\"王永锋\"}");
		params.put("inte", 1);

		String s1 = callRemoteMethodGET(url, params);
		System.out.println(s1);
		System.out.println("--------------------------");
		String s2 = callRemoteMethod(url, params, HTTPMethod.POST);
		System.out.println(s2);
		System.out.println("--------------------------");
		String s3 = callRemoteMethod(url, params, HTTPMethod.PUT);
		System.out.println(s3);
		System.out.println("--------------------------");
		String s4 = callRemoteMethod(url, params, HTTPMethod.DELETE);
		System.out.println(s4);
		System.out.println("--------------------------");
	}
}


