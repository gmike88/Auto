package com.netease.datadriver;

import java.util.Random;

public class GetRandomItem {
	
	/**
	 * This method is for getting random string which contains a-z and 0-9
	 * @author kris
	 * @param length
	 * 
	 */
	public String getRandomString(int length) {
		try{
			String base = "abcdefghijklmnopqrstuvwxyz0123456789";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			for(int i =0; i < length; i++){
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is for getting random string which contains 0-9
	 * @author kris
	 * @param length
	 * 
	 */
	public String getRandomStringNumber(int length) {
		try{
			String base = "0123456789";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			for(int i =0; i < length; i++){
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method is for getting random string which contains 0-9
	 * @author kris
	 * @param length
	 *
	 */
	public String getRandomStringLetter(int length) {
		try{
			String base = "abcdefghijklmnopqrstuvwxyz";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			for(int i =0; i < length; i++){
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
