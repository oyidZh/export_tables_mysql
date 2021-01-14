package com.oyid.util;

import com.alibaba.fastjson.JSONObject;
import com.oyid.entity.Table;
import com.oyid.entity.TableInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 16:13
 * Description: TableUtils
 * <p>
 * 传统jdbc连接方式获取表结构
 */
@Component
public class TableUtils {
    private static final String database = "fuck-space";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    @Value("${spring.datasource.url}")
    private String DB_URL;
    @Value("${spring.datasource.username}")
    private String USER;
    @Value("${spring.datasource.password}")
    private String PASSWORD;
    Statement stmt = null;

    private Connection conn;

    public Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        if (conn == null) {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        return conn;
    }

    public List<Table> getTables() throws SQLException, ClassNotFoundException {
        conn = createConnection();
        stmt = conn.createStatement();
        String sql = "SELECT * FROM information_schema.TABLES WHERE table_schema = '" + database + "'";
        ResultSet rs = stmt.executeQuery(sql);
        // 展开结果集数据库
        List<Table> list = new ArrayList<>();
        while (rs.next()) {
            // 通过字段检索
            String table_name = rs.getString("table_name");
            String table_comment = rs.getString("table_comment");
            Table table = Table.builder()
                    .tableName(table_name)
                    .tableComment(table_comment)
                    .build();
            list.add(table);
        }
        return list;
    }

    public List<TableInfo> getTableInfo() throws SQLException, ClassNotFoundException {
        conn = createConnection();
        stmt = conn.createStatement();
        String sql = "SELECT table_name, column_name, column_type, data_type, character_maximum_length, is_nullable, column_default, IF ( column_key = 'PRI', 'Y', 'N' ) keyd, column_comment FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = '" + database + "'";
        ResultSet rs = stmt.executeQuery(sql);
        // 展开结果集数据库
        List<TableInfo> list = new ArrayList<>();
        while (rs.next()) {
            // 通过字段检索
            String table_name = rs.getString("table_name");
            String column_name = rs.getString("column_name");
            String column_type = rs.getString("column_type");
            String data_type = rs.getString("data_type");
            String character_maximum_length = rs.getString("character_maximum_length");
            String is_nullable = rs.getString("is_nullable");
            String column_default = rs.getString("column_default");
            String keyd = rs.getString("keyd");
            String column_comment = rs.getString("column_comment");
            TableInfo info = TableInfo.builder()
                    .tableName(table_name)
                    .columnName(column_name)
                    .columnType(column_type)
                    .dataType(data_type)
                    .characterMaximumLength(character_maximum_length)
                    .isNullable(is_nullable)
                    .columnDefault(column_default)
                    .keyd(keyd)
                    .columnComment(column_comment)
                    .build();
            list.add(info);
        }
        return list;
    }
}
