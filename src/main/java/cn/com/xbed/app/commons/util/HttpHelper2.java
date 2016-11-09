package cn.com.xbed.app.commons.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.com.xbed.app.commons.enu.HTTPMethod;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

public class HttpHelper2 {
	private static final String DEFAULT_COMMUNICATE_CHARSET = PropertiesReader.consvars.get("charset.ws");// 约定请求参数和响应都使用这种字符编码
	private static ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(HttpHelper2.class));

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
						if (value instanceof Collection) {
							Collection c = (Collection) value;
							for (Object subParam : c) {
								fullUrl.append(URLEncoder.encode(key, DEFAULT_COMMUNICATE_CHARSET)).append("=")
										.append(URLEncoder.encode(subParam.toString(), DEFAULT_COMMUNICATE_CHARSET)).append("&");
							}
						} else if (value instanceof Object[]) {
							Object[] objArra = (Object[]) value;
							for (Object subParam : objArra) {
								fullUrl.append(URLEncoder.encode(key, DEFAULT_COMMUNICATE_CHARSET)).append("=")
										.append(URLEncoder.encode(subParam.toString(), DEFAULT_COMMUNICATE_CHARSET)).append("&");
							}
						} else {
							fullUrl.append(URLEncoder.encode(key, DEFAULT_COMMUNICATE_CHARSET)).append("=")
									.append(URLEncoder.encode(value.toString(), DEFAULT_COMMUNICATE_CHARSET)).append("&");
						}
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
			// if (200 == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
			EntityUtils.consume(entity);
			return ret;
			// }
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
		// return null;
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
			// if (200 == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
			EntityUtils.consume(entity);
			return ret;
			// }
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
		// return null;
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
			// if (200 == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
			EntityUtils.consume(entity);
			return ret;
			// }
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
		// return null;
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
			// if (200 == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			String ret = EntityUtils.toString(entity, DEFAULT_COMMUNICATE_CHARSET);
			EntityUtils.consume(entity);
			return ret;
			// }
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
		// return null;
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
		params.put("name", "wyf");
		params.put("age", 22);
		List l = new Vector<>();
		l.add("basketball");
		l.add("tenis");
		params.put("hobby", new Integer[] {1,2 });

		Integer[] ii = new Integer[]{1,2};
		int[] iii = new int[]{2,3};
		System.out.println(l.getClass().isArray());
		System.out.println(ii.getClass().isArray());
		System.out.println(iii.getClass().isArray());
		
		
		
		
		System.out.println(getFullUrl(url, params));
	}
}
