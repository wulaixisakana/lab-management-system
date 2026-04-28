package com.lab.controller;

import com.lab.entity.*;
import com.lab.mapper.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private LabReservationMapper labReservationMapper;

    @Resource
    private OperationLogMapper operationLogMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void setResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    @GetMapping("/equipment")
    public void exportEquipment(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        List<Equipment> list = equipmentMapper.findAll(name, category, status);
        setResponse(response, "设备列表.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("设备列表");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle = createDataStyle(wb);

            String[] headers = {"设备名称", "设备编号", "分类", "品牌", "型号", "位置", "状态", "价格", "描述"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }

            String[] statusMap = {"available", "可用", "in_use", "使用中", "maintenance", "维护中", "unavailable", "不可用"};
            for (int i = 0; i < list.size(); i++) {
                Equipment e = list.get(i);
                Row row = sheet.createRow(i + 1);
                String[] values = {
                    e.getName(), e.getCode(), e.getCategory(), e.getBrand(), e.getModel(),
                    e.getLocation(), mapStatus(e.getStatus(), statusMap),
                    e.getPrice() != null ? e.getPrice().toString() : "", e.getDescription()
                };
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j] != null ? values[j] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/reservation")
    public void exportReservation(
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        List<Reservation> list = reservationMapper.findAll(equipmentName, userName, status);
        setResponse(response, "预约记录.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("预约记录");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle = createDataStyle(wb);

            String[] headers = {"设备名称", "预约人", "开始时间", "结束时间", "使用目的", "状态"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            String[] statusMap = {"pending", "待审核", "approved", "已通过", "rejected", "已拒绝", "cancelled", "已取消"};
            for (int i = 0; i < list.size(); i++) {
                Reservation r = list.get(i);
                Row row = sheet.createRow(i + 1);
                String[] values = {
                    r.getEquipmentName(), r.getUserName(),
                    r.getStartTime() != null ? r.getStartTime().format(FMT) : "",
                    r.getEndTime() != null ? r.getEndTime().format(FMT) : "",
                    r.getPurpose(), mapStatus(r.getStatus(), statusMap)
                };
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j] != null ? values[j] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/attendance")
    public void exportAttendance(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {
        List<Attendance> list = attendanceMapper.findAll(userName, startDate, endDate);
        setResponse(response, "考勤记录.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("考勤记录");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle = createDataStyle(wb);

            String[] headers = {"姓名", "签到时间", "签退时间", "在岗时长(分钟)", "状态"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            String[] statusMap = {"present", "正常", "late", "迟到", "early_leave", "早退", "absent", "缺勤"};
            for (int i = 0; i < list.size(); i++) {
                Attendance a = list.get(i);
                Row row = sheet.createRow(i + 1);
                String[] values = {
                    a.getUserName(),
                    a.getCheckInTime() != null ? a.getCheckInTime().format(FMT) : "",
                    a.getCheckOutTime() != null ? a.getCheckOutTime().format(FMT) : "-",
                    a.getDuration() != null ? a.getDuration().toString() : "-",
                    mapStatus(a.getStatus(), statusMap)
                };
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j] != null ? values[j] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/labReservation")
    public void exportLabReservation(
            @RequestParam(required = false) String laboratoryName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        List<LabReservation> list = labReservationMapper.findAll(laboratoryName, userName, status);
        setResponse(response, "实验室预约记录.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("实验室预约");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle = createDataStyle(wb);

            String[] headers = {"实验室", "预约人", "开始时间", "结束时间", "人数", "使用目的", "状态"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            String[] statusMap = {"pending", "待审核", "approved", "已通过", "rejected", "已拒绝", "cancelled", "已取消"};
            for (int i = 0; i < list.size(); i++) {
                LabReservation r = list.get(i);
                Row row = sheet.createRow(i + 1);
                String[] values = {
                    r.getLaboratoryName(), r.getUserName(),
                    r.getStartTime() != null ? r.getStartTime().format(FMT) : "",
                    r.getEndTime() != null ? r.getEndTime().format(FMT) : "",
                    r.getParticipantCount() != null ? r.getParticipantCount().toString() : "",
                    r.getPurpose(), mapStatus(r.getStatus(), statusMap)
                };
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j] != null ? values[j] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/operationLog")
    public void exportOperationLog(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String module,
            HttpServletResponse response) throws IOException {
        List<OperationLog> list = operationLogMapper.findAll(userName, module);
        setResponse(response, "操作日志.xlsx");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("操作日志");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle = createDataStyle(wb);

            String[] headers = {"操作人", "模块", "操作", "详情", "IP地址", "操作时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            for (int i = 0; i < list.size(); i++) {
                OperationLog l = list.get(i);
                Row row = sheet.createRow(i + 1);
                String[] values = {
                    l.getUserName(), l.getModule(), l.getAction(), l.getDetail(), l.getIp(),
                    l.getCreateTime() != null ? l.getCreateTime().format(FMT) : ""
                };
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j] != null ? values[j] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            wb.write(response.getOutputStream());
        }
    }

    private String mapStatus(String status, String[] map) {
        if (status == null) return "";
        for (int i = 0; i < map.length - 1; i += 2) {
            if (map[i].equals(status)) return map[i + 1];
        }
        return status;
    }
}
