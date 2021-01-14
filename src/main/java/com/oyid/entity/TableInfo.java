package com.oyid.entity;

import com.oyid.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 12:27
 * Description: TableInfo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {
    //    @Excel(name = "表名称")
    private String tableName;

    @Excel(name = "列名")
    private String columnName;

    @Excel(name = "数据类型")
    private String columnType;

    @Excel(name = "字段类型")
    private String dataType;

    @Excel(name = "长度")
    private String characterMaximumLength;

    @Excel(name = "是否为空")
    private String isNullable;

    @Excel(name = "默认值")
    private String columnDefault;

    @Excel(name = "主键")
    private String keyd;

    @Excel(name = "备注")
    private String columnComment;
}
