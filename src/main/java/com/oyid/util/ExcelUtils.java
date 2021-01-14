package com.oyid.util;

import com.oyid.annotation.Excel;
import com.oyid.entity.Table;
import com.oyid.entity.TableInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: zhw
 * Date: 2021/1/14 12:19
 * Description: ExcelUtils
 */
@Component
public class ExcelUtils {

    public String exportExcel(List<Table> tableList, List<TableInfo> tableInfo) {
        tableList.forEach(table -> {
            List<TableInfo> list = tableInfo.stream().filter(info -> info.getTableName().equals(table.getTableName())).collect(Collectors.toList());
            table.setData(list);
        });

        Field[] allFields = TableInfo.class.getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();
        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Excel.class)) {
                fields.add(field);
            }
        }
        String sheetName = "test1表结构";
        OutputStream out = null;
        HSSFWorkbook workbook = null;

        int rowNum = 0;
        try {
            // 产生工作薄对象
            workbook = new HSSFWorkbook();
            // excel2003中每个sheet中最多有65536行
            int sheetSize = 65536;
            // 取出一共有多少个sheet.
            double sheetNo = Math.ceil(8 / 65536);
            for (int index = 0; index <= sheetNo; index++) {
                // 产生工作表对象
                HSSFSheet sheet = workbook.createSheet();
                if (sheetNo == 0) {
                    workbook.setSheetName(index, sheetName);
                } else {
                    // 设置工作表的名称.
                    workbook.setSheetName(index, sheetName + index);
                }
                for (int k = 0; k < tableList.size(); k++) {
                    if (k != 0) {
                        rowNum++;
                        rowNum++;
                    }
                    Table datum = tableList.get(k);
                    HSSFRow tableRow = sheet.createRow(rowNum);// 设置表名 产生一行
                    rowNum++;
                    HSSFCell tableCell = tableRow.createCell(0);// 产生单元格
                    tableCell.setCellValue(datum.getTableName() + "(" + datum.getTableComment() + ")");

                    HSSFRow row;
                    HSSFCell cell; // 产生单元格
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    List<TableInfo> list = datum.getData();
                    for (int i = 0; i < fields.size(); i++) {
                        Field field = fields.get(i);
                        Excel attr = field.getAnnotation(Excel.class);
                        // 创建列
                        cell = row.createCell(i);
                        // 设置列中写入内容为String类型
                        cell.setCellType(CellType.STRING);
                        HSSFCellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        if (attr.name().contains("注：")) {
                            HSSFFont font = workbook.createFont();
                            font.setColor(HSSFFont.COLOR_RED);
                            cellStyle.setFont(font);
                            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
                            sheet.setColumnWidth(i, 6000);
                        } else {
                            HSSFFont font = workbook.createFont();
                            // 粗体显示
                            font.setBold(true);
                            // 选择需要用到的字体格式
                            cellStyle.setFont(font);
                            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
                            // 设置列宽
                            sheet.setColumnWidth(i, 3766);
                        }
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        cellStyle.setWrapText(true);
                        cell.setCellStyle(cellStyle);

                        // 写入列名
                        cell.setCellValue(attr.name());

                        // 如果设置了提示信息则鼠标放上去提示.
                        if (!StringUtils.isEmpty(attr.prompt())) {
                            // 这里默认设了2-101列提示.
                            setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, i, i);
                        }
                        // 如果设置了combo属性则本列只能选择不能输入
                        if (attr.combo().length > 0) {
                            // 这里默认设了2-101列只能选择不能输入.
                            setHSSFValidation(sheet, attr.combo(), 1, 100, i, i);
                        }
                    }

                    int startNo = index * sheetSize;
                    int endNo = Math.min(startNo + sheetSize, list.size());
                    // 写入各条记录,每条记录对应excel表中的一行
                    HSSFCellStyle cs = workbook.createCellStyle();
                    cs.setAlignment(HorizontalAlignment.CENTER);
                    cs.setVerticalAlignment(VerticalAlignment.CENTER);
                    for (int i = startNo; i < endNo; i++) {
                        row = sheet.createRow(rowNum);
                        rowNum++;
                        // 得到导出对象.
                        TableInfo vo = list.get(i);
                        for (int j = 0; j < fields.size(); j++) {
                            // 获得field.
                            Field field = fields.get(j);
                            // 设置实体类私有属性可访问
                            field.setAccessible(true);
                            Excel attr = field.getAnnotation(Excel.class);
                            try {
                                // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                                if (attr.isExport()) {
                                    // 创建cell
                                    cell = row.createCell(j);
                                    cell.setCellStyle(cs);
                                    try {
                                        if (String.valueOf(field.get(vo)).length() > 10) {
                                            throw new Exception("长度超过10位就不用转数字了");
                                        }
                                        // 如果可以转成数字则导出为数字类型
                                        BigDecimal bc = new BigDecimal(String.valueOf(field.get(vo)));
                                        cell.setCellType(CellType.NUMERIC);
                                        cell.setCellValue(bc.doubleValue());
                                    } catch (Exception e) {
                                        cell.setCellType(CellType.STRING);
                                        if (vo == null) {
                                            // 如果数据存在就填入,不存在填入空格.
                                            cell.setCellValue("");
                                        } else {
                                            // 如果数据存在就填入,不存在填入空格.
                                            cell.setCellValue(field.get(vo) == null ? "" : String.valueOf(field.get(vo)));
                                        }

                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            String filename = encodingFilename(sheetName);
            out = new FileOutputStream(getAbsoluteFile(filename));
            workbook.write(out);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle, String promptContent, int firstRow,
                                          int endRow, int firstCol, int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation dataValidationView = new HSSFDataValidation(regions, constraint);
        dataValidationView.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(dataValidationView);
        return sheet;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet, String[] textlist, int firstRow, int endRow,
                                              int firstCol, int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(dataValidationList);
        return sheet;
    }

    /**
     * 编码文件名
     */
    public String encodingFilename(String filename) {
//        filename = UUID.randomUUID().toString() + "_" + filename + ".xls";
        filename = filename + ".xls";
        return filename;
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     */
    public String getAbsoluteFile(String filename) {
        String downloadPath = "/Users/liulh/Desktop/excel/" + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }
}
