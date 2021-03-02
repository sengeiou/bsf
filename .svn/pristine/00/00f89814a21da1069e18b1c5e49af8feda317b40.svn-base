package com.weking.core;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 基于HttpURLConnection简单封装的HTTP、HTTPS 请求工具类
 * 
 * @author chuanglan
 *
 */
public final class UtilHttpRequest implements AutoCloseable{
	

	static int connectTimeout = 2000;

	/**
	 * 创建一个Get请求
	 * 
	 * @param url
	 * @return
	 */
	public static UtilHttpRequest get(String url) {
		return new UtilHttpRequest(url, Method.GET,new HashMap<>());
	}

	/**
	 * 创建一个Post请求
	 * 
	 * @param url
	 * @return
	 */
	public static UtilHttpRequest post(String url) {
		return new UtilHttpRequest(url, Method.POST,new LinkedHashMap<>());
	}
	// 请求方法
	private enum Method{GET, POST}

	// 请求协议
	private enum Protocol{HTTP, HTTPS}

	private String url;// 请求地址
	//默认模拟IE11的UserAgent
	private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
	private String referer;
	private Method method;// 请求方法
	private Map<String, Object> params;// 请求参数
	private Charset reqCharset = Charset.forName("UTF-8");// 请求编码
	private Charset respCharset = Charset.forName("UTF-8");// 返回的编码
	
	private Protocol protocol;//请求协议
	private String postData;//Post 请求方式发送的数据
	
	//私有的构造方法
	private UtilHttpRequest(String url, Method method , Map map) {
		super();
		this.url = url;
		this.method = method;
		params = map;
		if (url != null && url.toUpperCase().indexOf(Protocol.HTTP.toString()) == 0) {
			protocol = Protocol.HTTP;
			if (url.toUpperCase().indexOf(Protocol.HTTPS.toString()) == 0) {
				protocol = Protocol.HTTPS;
			}
			referer = protocol.toString().toLowerCase()+"://"+url.split("/")[2];
		}
	}

	/**
	 * 设置请求编码
	 * @param charset
	 * @return
	 */
	public UtilHttpRequest reqCharset(Charset charset) {
		reqCharset = charset;
		return this;
	}

	/**
	 * 设置返回值编码
	 * @param charset
	 * @return
	 */
	public UtilHttpRequest respCharset(Charset charset) {
		respCharset = charset;
		return this;
	}
	/**
	 * 设置请求编码
	 * @param charset
	 * @return
	 */
	public UtilHttpRequest reqCharset(String charset) {
		reqCharset = Charset.forName(charset);
		return this;
	}
	
	/**
	 * 设置返回值编码
	 * @param charset
	 * @return
	 */
	public UtilHttpRequest respCharset(String charset) {
		respCharset = Charset.forName(charset);
		return this;
	}

	/**
	 * 添加一个请求参数
	 * @param key
	 * @param value
	 * @return
	 */
	public UtilHttpRequest put(String key, Object value) {
		params.put(key, value);
		return this;
	}
	/**
	 * 一次性添加多个请求参数
	 * @param params
	 * @return
	 */
	public UtilHttpRequest put(Map<String, ? extends Object> params) {
		if (params != null) {
			for (Entry<String, ? extends Object> entry : params.entrySet()) {
				this.params.put(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	/**
	 * 设置UserAgent
	 * @param userAgent
	 * @return
	 */
	public UtilHttpRequest userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}
	
	/**
	 * 设置Referer 有些接口需要验证Referer如网易云音乐，有些接口有防盗链只能为特定Referer或者为空，如微信图片
	 * 不设置则默认为请求地址的根路径，如果不想设置这请设置null
	 * @param referer
	 */
	public UtilHttpRequest referer(String referer) {
		this.referer = referer;
		return this;
	}

	/**
	 * 发送请求 并返回结果
	 * @return
	 */
	public String send() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException {
		if (protocol!=null) {
			boolean hasMap = !params.keySet().isEmpty();
			//如果是Get方式 或 Post方式但是有需要直接Post输出的参数 则将Map参数设置在字符串上
			if (method == Method.GET || postData!=null) {
				url =  hasMap? url + (url.indexOf("?") >= 0 ? "&" : "?") + mapToParamStr(params):url;
			}
			//如果是Post方式 并且没有设置需要直接Post输出的参数（非键值对）则将Map参数转化为Post参数
			if(method == Method.POST && hasMap && postData==null) {
				postData = mapToParamStr(params);
			}
			try {
				return request();
			} catch (KeyManagementException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException
					| KeyStoreException | CertificateException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return null;
	}
	/**
	 * 发送带有Post数据的请求
	 * @param postData
	 * @return
	 */
	public String send(String postData) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException {
		this.postData = postData;
		return send();
	}
	
	/**
	 * 发送带有post数据请求（将Map数据转为键值对形式）
	 * @param param
	 * @return
	 */
	public String send(Map<String,Object> param) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException {
		return send(mapToParamStr(param));
	}

	/**
	 * 执行请求的方法
	 * @return
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 */
	private String request() throws IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		URL url = new URL(this.url);
		URLConnection conn = url.openConnection();
		String result = null;
		if (Protocol.HTTPS == protocol) {
			SSLContext sslContext = SSLContext.getInstance("TLSV1.2");
			X509TrustManager[] tm = { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {return null;}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			} };
			sslContext.init(null, tm, new SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setSSLSocketFactory(ssf);
			httpsConn.setConnectTimeout(connectTimeout);
			httpsConn.setRequestMethod(method.toString());
			result = URLConnectionRequest(httpsConn);
			httpsConn.disconnect();
		} else {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod(method.toString());
			result = URLConnectionRequest(httpConn);
			httpConn.disconnect();
		}
		return result;
	}

	/**
	 * 通用的请求方法
	 * @param conn
	 * @return
	 */
	private String URLConnectionRequest(final URLConnection conn) {
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("User-Agent", userAgent);
		conn.setRequestProperty("Referer", referer);
		conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json;q=0.9,image/webp,*/*;q=0.8");
		conn.setRequestProperty("Accept-Charset", reqCharset.name());
		conn.setUseCaches(false);
		if (Method.POST == method) {
			//默认认为是表单提交
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + reqCharset.name());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if (postData!=null&&postData.length()>0) {
				postData = postData.trim();
				if(postData.indexOf("{")==0) {
					conn.setRequestProperty("Content-Type", "application/json;charset=" + reqCharset.name());
				} else if(postData.indexOf("<")==0) {
					conn.setRequestProperty("Content-Type", "application/xml;charset=" + reqCharset.name());
				}
				try(OutputStream outputStream = conn.getOutputStream()){
					outputStream.write(postData.getBytes(reqCharset));
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		try (InputStream inStream = conn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inStream, respCharset);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 创建类似于超链接的字符串，并将值urlEncode一下，暂不考虑key也为中文的情况
	 * @return 
	 */
	private String mapToParamStr(Map<String,Object> map){
		StringBuilder sb = new StringBuilder();
		String charset = reqCharset.name();
		for (Entry<String, Object> entry : map.entrySet()) {
			if(entry.getValue()!=null){
				sb.append("&").append(entry.getKey()).append("=");
				try {
					sb.append(URLEncoder.encode(entry.getValue().toString(), charset));
				} catch (UnsupportedEncodingException e) {
					sb.append(entry.getValue());
				}
			}
		}
		return sb.length() > 0 ? sb.substring(1) : sb.toString();
	}
	
	@Override
	public void close() throws Exception {		
	}
	
}