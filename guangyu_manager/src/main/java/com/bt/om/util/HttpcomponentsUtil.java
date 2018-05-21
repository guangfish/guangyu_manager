package com.bt.om.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.om.vo.common.SmsSendVo;

/**
 * 
 * Send SMS
 * 
 * return xml :
 * 
 * <?xml version="1.0" encoding="utf-8" ?> <returnsms>
 * <returnstatus>Success</returnstatus> <message>ok</message>
 * <remainpoint>45057</remainpoint> <taskID>2049722</taskID>
 * <successCounts>1</successCounts> </returnsms>
 * 
 * @author zhongbh
 * @version $Id: HttpcomponentsUtil.java, v 0.1 2015年12月30日 下午2:30:13 zhongbh
 *          Exp $
 * 
 */
public final class HttpcomponentsUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpcomponentsUtil.class);

	private static final String EMPTY = "";
	private static final String DEFAULT_ENCODE = Consts.UTF_8.name();
	private static int DEFAULT_SOCKET_TIMEOUT = 30000;
	private static int DEFAULT_CONNECT_TIMEOUT = 30000;
	private static int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 30000;

	public class Sms implements Runnable {

		private SmsSendVo vo;
		private String requestUrl;

		public Sms(SmsSendVo vo, String requestUrl) {
			this.vo = vo;
			this.requestUrl = requestUrl;
		}

		@Override
		public void run() {
			boolean throwException = true;
			for (int i = 0; i < 5; i++) {
				try {
					List<NameValuePair> nvps = HttpcomponentsUtil.getNameValuePairs(SmsSendVo.class, vo);
					String resp = postReq(nvps, requestUrl);
					if (parseResp(resp)) {
						logger.info(MessageFormat.format("Send monitor message success, message text:{0},", vo));
						break;
					}
				} catch (Exception e) {
					if (throwException) {
						logger.error(MessageFormat.format("Send monitor message failure, message text:{0},", vo), e);
						throwException = false;
					}
				}
			}
		}
	}

	/**
	 * send post request return <tt>String</tt>
	 */
	public static String postReq(List<NameValuePair> nvps, String requestUrl) throws Exception {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(requestUrl);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
					.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
					.setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT).build();
			httpPost.setConfig(requestConfig);
			// httpPost.setHeader("Accept",
			// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			// httpPost.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone
			// OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko)
			// Version/9.0 Mobile/13B143 Safari/601.1");
			// httpPost.setHeader("Accept-Encoding","gzip, deflate");
			// httpPost.setHeader("Accept-Language","en,zh-CN;q=0.9,zh;q=0.8");
			// httpPost.setHeader("Cache-Control","max-age=0");
			// httpPost.setHeader("Connection","keep-alive");
			// httpPost.setHeader("Host","tae.xmluren.com");
			// httpPost.setHeader("Upgrade-Insecure-Requests","1");

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_ENCODE));
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					result = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
				}
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return result;
	}

	/**
	 * send post request return <tt>String</tt>
	 */
	public static String postReq(Map<String, String> params, String requestUrl) throws Exception {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String val = params.get(key);
			nvps.add(new BasicNameValuePair(key, val));
		}
		return postReq(nvps, requestUrl);
	}

	/**
	 * <p>
	 * create <tt>NameValuePair</tt> objects
	 */
	public static <T> List<NameValuePair> getNameValuePairs(Class<T> ct, T obj) throws Exception {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Field[] fields = ct.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String key = field.getName();
			String val = emptyFilter(field.get(obj));
			nvps.add(new BasicNameValuePair(key, val));
		}
		return nvps;
	}

	/**
	 * parse the return xml
	 */
	public static boolean parseResp(String xml) throws Exception {
		boolean isSuccess = false;
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xml.getBytes(DEFAULT_ENCODE)));
		Element root = document.getRootElement();
		Element statusElement = root.element("returnstatus");
		String returnstatus = statusElement.getText();
		if ("Success".equals(returnstatus))
			isSuccess = true;
		return isSuccess;
	}

	/**
	 * null 2 empty
	 */
	public static String emptyFilter(Object val) {
		if (val == null)
			return EMPTY;
		return val.toString().trim();
	}

	/**
	 * 模拟请求
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String sendHttp(List<NameValuePair> nvps,String url)
			throws ParseException, IOException {
		String body = "";

		// 创建httpclient对象
		CloseableHttpClient client = HttpClients.createDefault();
		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
				.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT).build();
		httpPost.setConfig(requestConfig);

		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_ENCODE));

//		System.out.println("请求地址：" + url);
//		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, DEFAULT_ENCODE);
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}

	/**
	 * 绕过验证
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}

	/**
	 * 模拟请求
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws ClientProtocolException
	 * 参考：https://www.cnblogs.com/Mr-Rocker/p/6229652.html
	 */
	public static String sendHttps(List<NameValuePair> nvps,String url)
			throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		String body = "";
		// 采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySSL();

		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext)).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);

		// 创建自定义的httpclient对象
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
		// CloseableHttpClient client = HttpClients.createDefault();

		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
				.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT).build();
		httpPost.setConfig(requestConfig);

		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_ENCODE));

//		System.out.println("请求地址：" + url);
//		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, DEFAULT_ENCODE);
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}

}
