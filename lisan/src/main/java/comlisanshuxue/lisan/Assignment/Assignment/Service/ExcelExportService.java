package comlisanshuxue.lisan.Assignment.Service;

import comlisanshuxue.lisan.Assignment.entity.Assignment;
import comlisanshuxue.lisan.Assignment.entity.AssignmentProblem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportService.class);


    public void generateGradeExcel(Assignment assignment,
                                   List<AssignmentProblem> problems,
                                   List<String> studentIds,
                                   Map<String, Map<Long, Boolean>> studentScores,
                                   HttpServletResponse response) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("作业成绩");

        Row headerRow = sheet.createRow(2);
        int colIndex = createHeaderRow(headerRow, problems);

        int totalPoints = calculateTotalPoints(problems);
        CellStyle centerStyle = createCenterStyle(workbook);
        CellStyle pointsStyle = createPointsStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);

        int rowIndex = 3;
        for (String studentId : studentIds) {
            Row row = sheet.createRow(rowIndex++);
            fillStudentRow(row, problems, studentId, studentScores,
                    totalPoints, centerStyle, pointsStyle, totalStyle, percentStyle);
        }

        autoSizeColumns(sheet, problems.size());

        setResponseHeaders(assignment, response);

        workbook.write(response.getOutputStream());

    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = createCenterStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = createCenterStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0"));
        return style;
    }

    private CellStyle createPointsStyle(Workbook workbook) {
        CellStyle style = createCenterStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0"));
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void setResponseHeaders(Assignment assignment, HttpServletResponse response) {

        String fileName = "assignment_grades_" + assignment.getAssignmentId() + "_" +
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");


    }

    private int calculateTotalPoints(List<AssignmentProblem> problems) {

        return problems.stream()
                .mapToInt(AssignmentProblem::getPoints)
                .sum();
    }

    private void autoSizeColumns(Sheet sheet, int size) {

        int colCount = size + 3;
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
        }
        
    }

    private void fillStudentRow(Row row, List<AssignmentProblem> problems, String studentId, Map<String, Map<Long, Boolean>> studentScores, int totalPoints, CellStyle centerStyle, CellStyle pointsStyle, CellStyle totalStyle, CellStyle percentStyle) {
        int colIndex = 0;

        // 学生ID（居中）
        Cell idCell = row.createCell(colIndex++);
        idCell.setCellValue(studentId);
        idCell.setCellStyle(centerStyle);

        double studentTotal = 0;
        Map<Long, Boolean> scores = studentScores.getOrDefault(studentId, Collections.emptyMap());

        // 题目成绩
        for (AssignmentProblem problem : problems) {
            Long problemId = problem.getProblemId();
            int points = problem.getPoints();
            Boolean isCorrect = scores.get(problemId);
            double score = (isCorrect != null && isCorrect) ? points : 0;
            studentTotal += score;

            Cell scoreCell = row.createCell(colIndex++);
            scoreCell.setCellValue(score);
            scoreCell.setCellStyle(pointsStyle);
        }

        // 总分
        Cell totalCell = row.createCell(colIndex++);
        totalCell.setCellValue(studentTotal);
        totalCell.setCellStyle(totalStyle);

        // 百分比
        Cell percentCell = row.createCell(colIndex++);
        double percentage = totalPoints > 0 ? (studentTotal / totalPoints) : 0;
        percentCell.setCellValue(percentage);
        percentCell.setCellStyle(percentStyle);
    }

    private int createHeaderRow(Row headerRow, List<AssignmentProblem> problems) {

        int colIndex = 0;

        // 学生ID列
        headerRow.createCell(colIndex++).setCellValue("学生ID");

        // 题目列
        for (AssignmentProblem problem : problems) {
            headerRow.createCell(colIndex++).setCellValue(
                    "题目" + problem.getProblemId() + "(" + problem.getPoints() + "分)"
            );
        }

        // 总分列
        headerRow.createCell(colIndex++).setCellValue("总分");

        // 百分比列
        headerRow.createCell(colIndex++).setCellValue("得分率");

        return colIndex;
    }

    private void createTitleRow(Workbook workbook, Sheet sheet, Assignment assignment, int problemCount) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(assignment.getTitle() + " - 成绩单");

        // 合并单元格
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, problemCount + 3);
        sheet.addMergedRegion(mergedRegion);

        // 设置标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
    }


}
