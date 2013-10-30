package com.cpm.offlinebrowser.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class NetHelper {
	/**
	 * ��ȡDefaultHttpClientʵ��
	 * 
	 * @param charset
	 *            �������뼯, �ɿ�
	 * @return DefaultHttpClient ����
	 */
	private static DefaultHttpClient getDefaultHttpClient(final String charset) {
		HttpParams httpParams = new BasicHttpParams();

		// �������ӳ�ʱ�� Socket ��ʱ���Լ� Socket �����С
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		// �����ض���ȱʡΪ true
		HttpClientParams.setRedirecting(httpParams, true);

		// ���� user agent
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		return new DefaultHttpClient(httpParams);
	}
	public static String getData(String url, String charset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String responseStr = "";
		HttpClient httpClient = null;
		HttpGet hg = null;
		try {
			httpClient = getDefaultHttpClient(charset);
			hg = new HttpGet(url);
			// �������󣬵õ���Ӧ
			HttpResponse response = httpClient.execute(hg);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return "";
			}
			responseStr = EntityUtils.toString(response.getEntity(), charset);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (httpClient != null) {
				try {
					httpClient.getConnectionManager().shutdown();
				} catch (Exception e) {
				}
			}
		}
		return responseStr;
	}
	/**
	 * ��ȡ�����Ƿ����״̬
	 * 
	 * @return
	 */
	public static boolean networkIsAvailable(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		if (info.isConnected()) {
			return true;
		}
		return false;
	}
	/**
	 * ��ȡ��������
	 * 
	 * @param _url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String GetContentFromUrl(String url) {
		String result = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(url);
			HttpResponse resp = client.execute(req);
			HttpEntity ent = resp.getEntity();
			int status = resp.getStatusLine().getStatusCode();
			// If the status is equal to 200 ��that is OK
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils.toString(ent);
				// Encode utf-8 to iso-8859-1
				// result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
			}
			client.getConnectionManager().shutdown();
			return result;
		} catch (Exception e) {
			Log.e("NetHelper", "��ȡ����ʧ�� GetContentFromUrl" + e.toString() + url);
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * �õ�xml����
	 * 
	 * @param url
	 * @param contentType
	 * @return
	 */
	public static String GetXmlContentFromUrl(String url, String contentType) {
		return GetContentFromUrl(url, contentType).replaceAll("\n|\t|\r", "");
	}
	/**
	 * ��ȡ��������
	 * 
	 * @param _url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String GetContentFromUrl(String url, String contentType) {
		String result = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(url);
			HttpResponse resp = client.execute(req);
			req.getParams().setParameter("Content-Type", "UTF-8");
			HttpEntity ent = resp.getEntity();
			int status = resp.getStatusLine().getStatusCode();
			// If the status is equal to 200 ��that is OK
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils.toString(ent);
				// Encode utf-8 to iso-8859-1
				result = new String(result.getBytes("ISO-8859-1"), contentType);
			}
			client.getConnectionManager().shutdown();
			return result;
		} catch (Exception e) {
			Log.e("NetHelper", "______________��ȡ����ʧ�� GetContentFromUrl" + e.toString()
					+ url);
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * ������Post���ݻ�÷���
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String GetContentFromUrlByPostParams(String url,
			List<NameValuePair> params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost httpPost = new HttpPost(url);
			HttpEntity postEntity = new UrlEncodedFormEntity(params);
			httpPost.setEntity(postEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				String result = httpResponse.getEntity().toString();
				return result;
			}
			if (responseCode == 403) {
				return "1";// �Ѿ���ע�˴���
			}
		} catch (Exception e) {
			Log.e("NetHelper", "______________��ȡ����ʧ�� GetContentFromUrlByPostParams" + e.toString()
					+ url);
			e.printStackTrace();
		}
		httpClient.getConnectionManager().shutdown();
		return "";
	}
	/**
	 * ��ȡ������
	 */
	public static byte[] readInputStream(InputStream inStream) throws IOException {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	/**
	 * ����ͼƬ������
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImageFromUrlWithStore(String folder, String url, String extension) {
		
		try {
			//ע��url���ܰ���?���������Ҫ��?ǰ�ض�
			if(url.indexOf("?")>0){
				url=url.substring(0,url.indexOf("?"));
			}
			String fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
			String ext;
			if(extension != null && extension != "") 
				ext = extension;
			else
				ext = url.substring(url.lastIndexOf(".") + 1);
			
			String encodeFileName = URLEncoder.encode(fileName);
			URL imageUrl = new URL(url.replace(fileName, encodeFileName));
			byte[] data = readInputStream((InputStream) imageUrl.openStream());
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				FilesHelper.MakeDir(folder);
				String outFilename = folder + fileName + "." + ext;
				bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(
						outFilename));
				Bitmap bitmapCompress = BitmapFactory.decodeFile(outFilename);
				Drawable drawable = new BitmapDrawable(bitmapCompress);
				return drawable;
			}
		} catch (IOException e) {
			Log.e("download_img_err", e.toString());
		}
		return null;
	}
	
    /**
     * ͨ��URL�������ͼƬ����:http://www.xxxxxx.com/xx.jpg
     * */
    public Bitmap getBitmap(String url, int displaypixels, Boolean isBig) throws MalformedURLException, IOException {
        Bitmap bmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        
        InputStream stream = new URL(url).openStream();
        byte[] bytes = readInputStream(stream);
        //��3���Ǵ���ͼƬ�����begin( �������Ҫ�������ֱ�� opts.inSampleSize=1;)
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
        //end
        opts.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        return bmp;
    }
	/****
	 * ����ͼƬbitmap size exceeds VM budget ��Out Of Memory �ڴ������
	 */
	private int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	    
	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImageFromUrl(String url) {
		InputStream is = null;
		try {
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			String encodeFileName = URLEncoder.encode(fileName);
			URL imageUrl = new URL(url.replace(fileName, encodeFileName));
			is = (InputStream) imageUrl.getContent();
		} catch (Exception e) {
			Log.e("There", e.toString());
		}
		Drawable d = Drawable.createFromStream(is, "src");
		return d;
	}
}
