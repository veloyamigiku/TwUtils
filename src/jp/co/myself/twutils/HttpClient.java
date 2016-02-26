package jp.co.myself.twutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP通信のクライアントクラスです。
 */
public class HttpClient {
	
	/**
	 * HTTPリクエストのメソッド(GET)です。
	 */
	private static final String HTTP_METHOD_GET = "GET";
	
	/**
	 * HTTPリクエストのメソッド(POST)です。
	 */
	private static final String HTTP_METHOD_POST = "POST";
	
	/**
	 * リクエスト(GET)を実行します。
	 * @param requestUrl リクエストURLです。
	 */
	public static void get(String requestUrl) {
		HttpURLConnection con = null;
		URL url = null;
		try {
			url = new URL(requestUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(HTTP_METHOD_GET);
			con.setRequestProperty("Accept-Language", "jp");
			con.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// ヘッダーの取得と表示
		printResponseHeader(con);
		
		// 本文の取得と表示
		printResponseBody(con);
	}
	
	public static void post(String requestUrl, HashMap<String, String> requestHeaders) {
		HttpURLConnection con = null;
		URL url = null;
		try {
			url = new URL(requestUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod(HTTP_METHOD_POST);
			for (String headerKey : requestHeaders.keySet()) {
				con.setRequestProperty(headerKey, requestHeaders.get(headerKey));
			}
			con.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		/*
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8))) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		// ヘッダーの取得と表示
		printResponseHeader(con);
		
		// 本文の取得と表示
		printResponseBody(con);
	}
	
	/**
	 * レスポンスヘッダを取得して、標準出力に出力します。
	 * @param con コネクションです。
	 */
	private static void printResponseHeader(HttpURLConnection con) {
		System.out.println("レスポンスヘッダ:");
		Map<String, List<String>> headers = con.getHeaderFields();
		StringBuilder sb = new StringBuilder();
		for (String headerKey : headers.keySet()) {
			sb.append(headerKey + ":" + headers.get(headerKey) + "\n");
		}
		System.out.println(sb.toString());
	}
	
	/**
	 * レスポンスボディを取得して、標準出力に出力します。
	 * @param con コネクションです。
	 */
	private static void printResponseBody(HttpURLConnection con) {
		System.out.println("レスポンスボディ：");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
			for (String line; (line = br.readLine()) != null;) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
