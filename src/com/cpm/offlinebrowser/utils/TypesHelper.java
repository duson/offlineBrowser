package com.cpm.offlinebrowser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TypesHelper {

	public static int parseInt(String strValue, int defaultValue) {
		try {
			return Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Stringת��Ϊʱ��
	 * @param str
	 * @return
	 */
	public static Date ParseDate(String str){
		SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA); 
		Date addTime = null;
		try {
			addTime = dateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return addTime;
	}
	/**
	 * ������ת��Ϊ�ַ���
	 * @param date
	 * @return
	 */
	public static String ParseDateToString(Date date){
		return ParseDateToString(date,"yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * ������ת��Ϊ�ַ��������أ�
	 * @param date
	 * @param format:ʱ���ʽ���������yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String ParseDateToString(Date date, String format){
		if(date == null) return "";
		String result = "";
		SimpleDateFormat dateFormat =new SimpleDateFormat(format, Locale.CHINA);
		try {
			result = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * ��UMTʱ��ת��Ϊ����ʱ��
	 * @param str
	 * @return
	 * @throws ParseException 
	 */
	public static Date ParseUTCDate(String str){
		//��ʽ��2012-03-04T23:42:00+08:00
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.CHINA);
		try {
			Date date = formatter.parse(str);
			
			return date;
		} catch (ParseException e) {
			//��ʽ��Sat, 17 Mar 2012 11:37:13 +0000
			//Sat, 17 Mar 2012 22:13:41 +0800
			try{
				SimpleDateFormat formatter2=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",Locale.CHINA);
				Date date2 = formatter2.parse(str);
				
				return date2;
			}catch(ParseException ex){
				return null;
			}
		}		
	}
	
	/**
	 * ��ʱ��ת��Ϊ����
	 * @param datetime
	 * @return
	 */
	public static String DateToChineseString(Date datetime){
		Date today=new Date();
		long   seconds   =   (today.getTime()-   datetime.getTime())/1000; 

		long year=	seconds/(24*60*60*30*12);// �������
		long   month  =   seconds/(24*60*60*30);//�������
		long   date   =   seconds/(24*60*60);     //�������� 
		long   hour   =   (seconds-date*24*60*60)/(60*60);//����Сʱ�� 
		long   minute   =   (seconds-date*24*60*60-hour*60*60)/(60);//���ķ����� 
		long   second   =   (seconds-date*24*60*60-hour*60*60-minute*60);//�������� 
		
		if(year>0){
			return year + "��ǰ";
		}
		if(month>0){
			return month + "��ǰ";
		}
		if(date>0){
			return date + "��ǰ";
		}
		if(hour>0){
			return hour + "Сʱǰ";
		}
		if(minute>0){
			return minute + "����ǰ";
		}
		if(second>0){
			return second + "��ǰ";
		}
		return "δ֪ʱ��";
	}

	public static String SubstringString(String str, int len){
		if(str == null || str.length() <= len) return str;
		
		return str.substring(0, len) + "...";
	}
}
