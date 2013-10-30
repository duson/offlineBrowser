/**
 * 
 */
package com.cpm.offlinebrowser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CNIT
 *
 */
public class HtmlHelper {
	private final static String regxpForHtml = "<([^>]*)>"; // ����������<��ͷ��>��β�ı�ǩ

	// private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>";
	// �ҳ�IMG��ǩ

	// �ҳ�IMG��ǩ��SRC����
	private final static String regxpForImaTagSrcAttrib = "<\\s*img\\s+src=\"([^\"]+)\"\\s*>";

	/**
	 * 
	 * �������ܣ��滻�����������ʾ
	 * <p>
	 * 
	 * @param input
	 * @return String
	 */
	public static String replaceTag(String input) {
		if (!hasSpecialChars(input)) {
			return input;
		}
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		for (int i = 0; i <= input.length() - 1; i++) {
			c = input.charAt(i);
			switch (c) {
				case '<' :
					filtered.append("&lt;");
					break;
				case '>' :
					filtered.append("&gt;");
					break;
				case '"' :
					filtered.append("&quot;");
					break;
				case '&' :
					filtered.append("&amp;");
					break;
				default :
					filtered.append(c);
			}

		}
		return (filtered.toString());
	}

	/**
	 * 
	 * �������ܣ��жϱ���Ƿ����
	 * <p>
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean hasSpecialChars(String input) {
		boolean flag = false;
		if ((input != null) && (input.length() > 0)) {
			char c;
			for (int i = 0; i <= input.length() - 1; i++) {
				c = input.charAt(i);
				switch (c) {
					case '>' :
						flag = true;
						break;
					case '<' :
						flag = true;
						break;
					case '"' :
						flag = true;
						break;
					case '&' :
						flag = true;
						break;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * �������ܣ�����������"<"��ͷ��">"��β�ı�ǩ
	 * <p>
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterHtml(String str) {
		Pattern pattern = Pattern.compile(regxpForHtml);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * �������ܣ�����ָ����ǩ
	 * <p>
	 * 
	 * @param str
	 * @param tag
	 *            ָ����ǩ
	 * @return String
	 */
	public static String fiterHtmlTag(String str, String tag) {
		String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
		Pattern pattern = Pattern.compile(regxp);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * �������ܣ��滻ָ���ı�ǩ
	 * <p>
	 * 
	 * @param str
	 * @param beforeTag
	 *            Ҫ�滻�ı�ǩ
	 * @param tagAttrib
	 *            Ҫ�滻�ı�ǩ����ֵ
	 * @param startTag
	 *            �±�ǩ��ʼ���
	 * @param endTag
	 *            �±�ǩ�������
	 * @return String
	 * @�磺�滻img��ǩ��src����ֵΪ[img]����ֵ[/img]
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag) {
		String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
		String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
		Matcher matcherForTag = patternForTag.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer();
			Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
					.group(1));
			if (matcherForAttrib.find()) {
				matcherForAttrib.appendReplacement(sbreplace, startTag
						+ matcherForAttrib.group(1) + endTag);
			}
			matcherForTag.appendReplacement(sb, sbreplace.toString());
			result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);
		return sb.toString();
	}
	
	public static List<String> getLinks(String src, String regex){
		List<String> result = new ArrayList<String>();
		
		Pattern patternForImg = Pattern.compile(regex);
		Matcher matchForImg = patternForImg.matcher(src);
		while(matchForImg.find()){
			result.add(matchForImg.group(1));
		}
		
		return result;
	}
	
	public static List<String> getImages(String src){
		List<String> result = new ArrayList<String>();
		
		String regxpForImaTagSrcAttrib = "<\\s*img\\s+[^>]*src=(\"|')([^\"']+)(\"|')[^>]*>";
		Pattern patternForImg = Pattern.compile(regxpForImaTagSrcAttrib);
		Matcher matchForImg = patternForImg.matcher(src);
		while(matchForImg.find()){
			result.add(matchForImg.group(2));
		}
		
		return result;
	}
	
	public static String replaceImagePath(String str, String oldPath, String newPath) {
		String regxpForImaTagSrcAttrib = "src=\"" + oldPath + "\"";
		Pattern patternForImage = Pattern.compile(regxpForImaTagSrcAttrib);
		Matcher matcherForImage = patternForImage.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcherForImage.find()) {
			matcherForImage.appendReplacement(sb, "src=\"" + newPath + "\"");
		}
		
		matcherForImage.appendTail(sb);
		return sb.toString();
	}
	
}
