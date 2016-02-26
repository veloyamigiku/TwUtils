package jp.co.myself.twutils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * リクエストトークンを取得するクラスです。
 */
public class RequestTokenGetter {
	
	/**
	 * リクエストトークン取得のエンドポイントURLです。
	 */
	private static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	
	/**
	 * リクエストメソッド(POST)です。
	 */
	private static final String REQUEST_METHOD = "POST";
	
	/**
	 * リクエストトークン取得用のパラメータ名(コールバックURL)です。
	 */
	private static final String OAUTH_CALLBACK = "oauth_callback";
	
	/**
	 * リクエストトークン取得用のパラメータ名(APIキー)です。
	 */
	private static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	
	/**
	 * リクエストトークン取得用のパラメータ名(署名の種類)です。
	 */
	private static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	
	/**
	 * リクエストトークン取得用のパラメータ名(タイムスタンプ)です。
	 */
	private static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	
	/**
	 * メッセージ認証のアルゴリズム(HMAC-SHA1)です。
	 */
	private static final String HMAC_SHA1 = "HMAC-SHA1";
	
	/**
	 * メッセージ認証のアルゴリズム(HMAC-SHA1)です。
	 */
	private static final String HMACSHA1 = "HMacSHA1";
	
	/**
	 * リクエストトークン取得用のパラメータ名(ランダム文字列)です。
	 */
	private static final String OAUTH_NONCE = "oauth_nonce";
	
	/**
	 * リクエストトークン取得用のパラメータ名(認証時のOAuthのバージョン)です。
	 */
	private static final String OAUTH_VERSION = "oauth_version";
	
	/**
	 * OAuthのバージョン(1.0)です。
	 */
	private static final String OAUTH_VERISON_1_0 = "1.0";
	
	/**
	 * OAuthの署名です。
	 */
	private static final String OAUTH_SIGNATURE = "oauth_signature";
	
	/**
	 * Authorization(リクエストヘッダ名)です。
	 */
	private static final String AUTORIZATION = "Authorization";
	
	/**
	 * OAuth(Authorizationヘッダの値の一部)です。
	 */
	private static final String OAUTH = "OAuth";
	
	/**
	 * リクエストトークンを取得します。
	 * @param apiKey APIキーです。
	 * @param apiSecret API Secretです。
	 * @param callBackURL コールバックURLです。
	 * @return　リクエストトークン。
	 */
	public static String proc(String apiKey, String apiSecret, String callBackURL) {
		
		String requestToken = null;
		
		// 1)署名作成用のキーを生成します。
		String signatureKey = null;
		String encodeApiKey = null;
		String encodeApiSecret = null;
		try {
			encodeApiKey = urlEncode(apiKey);
			encodeApiSecret = urlEncode(apiSecret);
			signatureKey = encodeApiSecret + "&" + encodeApiKey;
		} catch (UnsupportedEncodingException e) {
			return requestToken;
		}
		// debug code
		System.out.println("署名作成用のキー:" + signatureKey);
		
		// 2)署名作成用のデータを生成します。
		long timeMsec = System.currentTimeMillis();
		long timeSec = timeMsec / 1000;
		HashMap<String, String> paramHash = new HashMap<String, String>();
		paramHash.put(OAUTH_CALLBACK, callBackURL);
		paramHash.put(OAUTH_CONSUMER_KEY, encodeApiKey);
		paramHash.put(OAUTH_SIGNATURE_METHOD, HMAC_SHA1);
		paramHash.put(OAUTH_TIMESTAMP, String.valueOf(timeSec));
		paramHash.put(OAUTH_NONCE, String.valueOf(timeMsec));
		paramHash.put(OAUTH_VERSION,OAUTH_VERISON_1_0);
		String data = null;
		try {
			data = REQUEST_METHOD + "&" + 
					urlEncode(REQUEST_URL) + "&" + 
					urlEncode(joinHashMap(paramHash, "=", "&"));
		} catch (UnsupportedEncodingException e) {
			return requestToken;
		}
		
		// debug code
		System.out.println("署名作成用のデータ:" + data);
		
		// 3)署名を作成します。
		String signature = null;
		try {
			Mac mac = Mac.getInstance(HMACSHA1);
			// 署名キーを元に、メッセージ認証インスタンスを初期化します。
			SecretKeySpec sks = new SecretKeySpec(signatureKey.getBytes(), HMACSHA1);
			mac.init(sks);
			// メッセージ認証コードを取得します。
			byte[] macByteAry = mac.doFinal();
			// メッセージ認証コードをBase64形式でエンコードします。
			signature = Base64.getEncoder().encodeToString(macByteAry);
		} catch (NoSuchAlgorithmException e) {
			return requestToken;
		} catch (InvalidKeyException e) {
			return requestToken;
		}
		// debug code
		System.out.println("署名:" + signature);
		
		// 4)リクエストトークン取得のHTTPリクエストを送信します。
		//HttpClient.get("http://www.google.co.jp");
		HashMap<String, String> requestHeaders = new HashMap<String, String>();
		try {
			paramHash.put(OAUTH_CALLBACK, urlEncode(callBackURL));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paramHash.put(OAUTH_SIGNATURE, signature);
		requestHeaders.put(AUTORIZATION, OAUTH + " " + joinHashMap(paramHash, "=", ","));
		HttpClient.post(REQUEST_URL, requestHeaders);
		
		return requestToken;	
	}
	
	/**
	 * 指定の文字列を、URLエンコードします。
	 * @param str URLエンコードする文字列です。
	 * @return URLエンコード後の文字列。
	 * @throws UnsupportedEncodingException　エンコードエラー。
	 */
	private static String urlEncode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
	}
	
	/**
	 * ハッシュのキーと値の全ペアに、以下の処理を順に実施します。
	 * 1)ハッシュのキーと値を、文字列で連結します。
	 * 2)ハッシュのキーと値のペア同士を、文字列で連結します。
	 * @param hash ハッシュです。
	 * @param keyValueDelim キーと値の連結する文字列です。
	 * @param contentDelim キーと値のペア同士を連結する文字列です。
	 * @return ハッシュのキーと値の全ペアを連結した文字列。
	 */
	private static String joinHashMap(HashMap<String, String> hash, String keyValueDelim, String contentDelim) {
		StringBuilder sb = new StringBuilder();
		
		for (String key : hash.keySet()) {
			sb.append(key + keyValueDelim + hash.get(key) + contentDelim);
		}
		
		return sb.toString().substring(0, sb.toString().length() - contentDelim.length());
	}
	
}
