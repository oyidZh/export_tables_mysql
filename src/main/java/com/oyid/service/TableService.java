package com.oyid.service;

import com.oyid.entity.Table;
import com.oyid.entity.TableInfo;
import com.oyid.mapper.TableMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 15:29
 * Description: TableService
 */
@Service
public class TableService {

    @Resource
    private TableMapper tableMapper;

    public List<Table> getAllTable(String database) {
        return tableMapper.getAllTable(database);
    }

    public List<TableInfo> getAllTableInfo(String database) {
        return tableMapper.getAllTableInfo(database);
    }
}
