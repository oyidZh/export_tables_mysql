package com.oyid.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 12:26
 * Description: Table
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Table {
    private String tableName;

    private String tableComment;

    private List<TableInfo> data;
}
