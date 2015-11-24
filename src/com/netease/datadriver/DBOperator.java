package com.netease.datadriver;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.netease.dagger.BrowserEmulator;
import com.netease.datadriver.ExcelDataProvider;

public class DBOperator {
	
	private static Logger logger = Logger.getLogger(DBOperator.class
			.getName());
	
	public static void testDataPreparation(String Preparedata){
		Connection conn = null;
		java.sql.PreparedStatement pstm1 = null;
		java.sql.PreparedStatement pstm2 = null;
		java.sql.PreparedStatement pstm3 = null;
		String url = "jdbc:mysql://10.20.30.13:3306/testdb?" + "user=testA&password=123456&userUnicode=true&characterEncoding=UTF8";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Success to load MySQL driver");
			conn = DriverManager.getConnection(url);
			switch(Preparedata){
			
			case("Sobet_18_inferior_transfer"): {
				ExcelDataProvider edp = new ExcelDataProvider(Preparedata, null);
				if(edp.hasNext()) {
					Object[] obj =edp.next();
					for (int i = 0; i < obj.length; i++) {
						Map<String, String> data = (Map<String, String>) obj[i];
						String value1 = data.get("username1");
						String value2 = data.get("select_inferior1");
						pstm1 = conn.prepareStatement("select UIN from cn bind where cn = ?");
						pstm1.setString(1, value2);
						String selectResults = getSelectResult(pstm1, "UIN");
						logger.info("selectResults");
						pstm2 = conn.prepareStatement("UPDATE cn_bind SET FUIN = ? where cn = ?");
						pstm2.setString(1, selectResults);
						pstm2.setString(2, value1);
						pstm3 = conn.prepareStatement("UPDATE cb_point SET POINT = 1000 where UIN = (select UIN from cn_bind where cn =?)");
						pstm3.setString(1, value1);
						int result1 = pstm2.executeUpdate();
						int result2 = pstm3.executeUpdate();
						if (result1 + result2 > 0) {
							logger.info("SQL执行成功，测试数据已准备好");
						} else{
							logger.info("SQL执行成功，测试数据未准备好");
						}
					}
				}
				break;
			}
			}
		}catch (SQLException e){
			logger.error("MYSQL操作错误");
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List converList(ResultSet rs) throws SQLException{
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount(); //Map rowdata
		logger.info("converList: " + columnCount);
		while(rs.next()) {
			Map rowData =new HashMap();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}
	
	private static String getSelectResult(PreparedStatement pm, String fieldName) throws SQLException{
		ResultSet set = pm.executeQuery();
		List list = converList(set);
		Iterator it = list.listIterator();
		String exeResult = null;
		while(it.hasNext()) {
			HashMap<String, Object> map = (HashMap<String, Object>) it.next();
			exeResult = map.get(fieldName).toString();
					break;
			}
		return exeResult;
	}
}
