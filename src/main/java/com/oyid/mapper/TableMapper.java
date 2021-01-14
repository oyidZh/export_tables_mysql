package com.oyid.mapper;

import com.oyid.entity.Table;
import com.oyid.entity.TableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 15:30
 * Description: TableMapper
 */
public interface TableMapper {

    List<Table> getAllTable(@Param("database") String database);

    List<TableInfo> getAllTableInfo(@Param("database") String database);
}
