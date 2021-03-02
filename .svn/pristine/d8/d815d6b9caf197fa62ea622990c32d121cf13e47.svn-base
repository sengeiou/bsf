package com.weking.core;

import com.weking.model.weixin.Unifiedorder;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * post提交xml格式的参数
 * @author iYjrg_xiebin
 * @date 2015年11月25日下午3:33:38
 */
public class HttpXmlUtils {

	/**
	 * 开始post提交参数到接口
	 * 并接受返回
	 * @param url
	 * @param xml
	 * @param method
	 * @param contentType
	 * @return
	 */
	public static String xmlHttpProxy(String url,String xml,String method,String contentType){
		InputStream is = null;
		OutputStreamWriter os = null;

		try {
			URL _url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
			conn.setDoInput(true);   
			conn.setDoOutput(true);   
			conn.setRequestProperty("Content-type", "text/xml");
			conn.setRequestProperty("Pragma:", "no-cache");  
			conn.setRequestProperty("Cache-Control", "no-cache");  
			conn.setRequestMethod("POST");
			os = new OutputStreamWriter(conn.getOutputStream());
			os.write(new String(xml.getBytes(contentType)));
			os.flush();

			//返回值
			is = conn.getInputStream();
			return getContent(is, "utf-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(os!=null){os.close();}
				if(is!=null){is.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 解析返回的值
	 * @param is
	 * @param charset
	 * @return
	 */
	public static String getContent(InputStream is, String charset) {
		String pageString = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer sb = null;
		try {
			isr = new InputStreamReader(is, charset);
			br = new BufferedReader(isr);
			sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			pageString = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null){
					is.close();
				}
				if(isr!=null){
					isr.close();
				}
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sb = null;
		}
		return pageString;
	}

	/**
	 * 构造xml参数
	 * @param xml
	 * @return
	 */
	public static String xmlInfo(Unifiedorder unifiedorder){
		//构造xml参数的时候，至少又是个必传参数
		/*
		 * <xml>
			   <appid>wx2421b1c4370ec43b</appid>
			   <attach>支付测试</attach>
			   <body>JSAPI支付测试</body>
			   <mch_id>10000100</mch_id>
			   <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
			   <notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url>
			   <openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid>
			   <out_trade_no>1415659990</out_trade_no>
			   <spbill_create_ip>14.23.150.211</spbill_create_ip>
			   <total_fee>1</total_fee>
			   <trade_type>JSAPI</trade_type>
			   <sign>0CB01533B8C1EF103065174F50BCA001</sign>
			</xml>
		 */

		if(unifiedorder!=null){
			StringBuffer bf = new StringBuffer();
			bf.append("<xml>");

			bf.append("<appid><![CDATA[");
			bf.append(unifiedorder.getAppid());
			bf.append("]]></appid>");

			bf.append("<mch_id><![CDATA[");
			bf.append(unifiedorder.getMch_id());
			bf.append("]]></mch_id>");

			bf.append("<nonce_str><![CDATA[");
			bf.append(unifiedorder.getNonce_str());
			bf.append("]]></nonce_str>");

			bf.append("<sign><![CDATA[");
			bf.append(unifiedorder.getSign());
			bf.append("]]></sign>");

			bf.append("<body><![CDATA[");
			bf.append(unifiedorder.getBody());
			bf.append("]]></body>");

			bf.append("<detail><![CDATA[");
			bf.append(unifiedorder.getDetail());
			bf.append("]]></detail>");

			bf.append("<attach><![CDATA[");
			bf.append(unifiedorder.getAttach());
			bf.append("]]></attach>");

			bf.append("<out_trade_no><![CDATA[");
			bf.append(unifiedorder.getOut_trade_no());
			bf.append("]]></out_trade_no>");

			bf.append("<total_fee><![CDATA[");
			bf.append(unifiedorder.getTotal_fee());
			bf.append("]]></total_fee>");

			bf.append("<spbill_create_ip><![CDATA[");
			bf.append(unifiedorder.getSpbill_create_ip());
			bf.append("]]></spbill_create_ip>");

			bf.append("<time_start><![CDATA[");
			bf.append(unifiedorder.getTime_start());
			bf.append("]]></time_start>");

			bf.append("<time_expire><![CDATA[");
			bf.append(unifiedorder.getTime_expire());
			bf.append("]]></time_expire>");

			bf.append("<notify_url><![CDATA[");
			bf.append(unifiedorder.getNotify_url());
			bf.append("]]></notify_url>");

			bf.append("<trade_type><![CDATA[");
			bf.append(unifiedorder.getTrade_type());
			bf.append("]]></trade_type>");


			bf.append("</xml>");
			return bf.toString();
		}

		return "";
	}


	public static String PayxmlInfo(String mch_appid,String mchid,String nonce_str,String partner_trade_no
			,String openid,String check_name,String re_user_name,String amount,String desc,String spbill_create_ip,String sign){
	
			StringBuffer bf = new StringBuffer();
			bf.append("<xml>");

			bf.append("<mch_appid><![CDATA[");
			bf.append(mch_appid);
			bf.append("]]></mch_appid>");

			bf.append("<mchid><![CDATA[");
			bf.append(mchid);
			bf.append("]]></mchid>");

			bf.append("<nonce_str><![CDATA[");
			bf.append(nonce_str);
			bf.append("]]></nonce_str>");

			bf.append("<partner_trade_no><![CDATA[");
			bf.append(partner_trade_no);
			bf.append("]]></partner_trade_no>");

			bf.append("<openid><![CDATA[");
			bf.append(openid);
			bf.append("]]></openid>");

			bf.append("<check_name><![CDATA[");
			bf.append(check_name);
			bf.append("]]></check_name>");

			bf.append("<re_user_name><![CDATA[");
			bf.append(re_user_name);
			bf.append("]]></re_user_name>");

			bf.append("<amount><![CDATA[");
			bf.append(amount);
			bf.append("]]></amount>");

			bf.append("<desc><![CDATA[");
			bf.append(desc);
			bf.append("]]></desc>");

			bf.append("<spbill_create_ip><![CDATA[");
			bf.append(spbill_create_ip);
			bf.append("]]></spbill_create_ip>");

			bf.append("<sign><![CDATA[");
			bf.append(sign);
			bf.append("]]></sign>");


			bf.append("</xml>");
			return bf.toString();
		

	}
	
	/**
	 * post请求并得到返回结果
	 * @param requestUrl
	 * @param requestMethod
	 * @param output
	 * @return
	 */
	public static String httpsRequest(String requestUrl, String requestMethod, String output) {
		try{
			URL url = new URL(requestUrl);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod(requestMethod);
			if (null != output) {
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(output.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			connection.disconnect();
			return buffer.toString();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return "";
	}


	/**
	 * 发送post请求
	 * @param httpClient
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param encoding 编码
	 * @return
	 */
	public static String sendPost(HttpClient httpClient, String url, Map<String, String> params, Charset encoding) {
		String resp = "";
		HttpPost httpPost = new HttpPost(url);
		if (params != null && params.size() > 0) {
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, encoding);
			httpPost.setEntity(postEntity);
		}
		CloseableHttpResponse response = null;
		try {
			response = (CloseableHttpResponse) httpClient.execute(httpPost);
			resp = EntityUtils.toString(response.getEntity(), encoding);
		} catch (Exception e) {
			// log
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// log
					e.printStackTrace();
				}
			}
		}
		return resp;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	
}
