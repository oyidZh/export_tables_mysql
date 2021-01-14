package com.oyid.controller;

import com.oyid.entity.Table;
import com.oyid.entity.TableInfo;
import com.oyid.service.TableService;
import com.oyid.util.ExcelUtils;
import com.oyid.util.TableUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 11:19
 * Description: Test
 */
@RestController
public class TestController {

    @Resource
    private ExcelUtils excelUtils;

    @Resource
    private TableService tableService;

    @Resource
    private TableUtils tableUtils;


    /**
     * 传统jdbc连接方式获取表结构
     *
     * @throws SQLException           s
     * @throws ClassNotFoundException c
     */
    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public void sql11() throws SQLException, ClassNotFoundException {
        List<Table> tables = tableUtils.getTables();
        List<TableInfo> tableInfo = tableUtils.getTableInfo();
        String s = excelUtils.exportExcel(tables, tableInfo);
        System.out.println("文件名：" + s);
        System.out.println("获取结束");
    }

    /**
     * 使用mybatis获取
     *
     * @param database 所要导出的数据库
     */
    @RequestMapping("/getTables")
    public void getTables(@RequestParam("database") String database) {
        List<Table> tables = tableService.getAllTable(database);
        List<TableInfo> tableInfo = tableService.getAllTableInfo(database);
        String s = excelUtils.exportExcel(tables, tableInfo);
        System.out.println("文件名：" + s);
        System.out.println("获取结束");
    }


}
