package comlisanshuxue.lisan.Assignment.Controller;

import comlisanshuxue.lisan.Assignment.Service.AssignmentService;
import comlisanshuxue.lisan.Assignment.Service.ExcelExportService;
import comlisanshuxue.lisan.Assignment.entity.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ExcelExportService excelExportService;

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);


    @GetMapping
    public Result getAssignments(
            @RequestParam (required = false) String classname,
            Page page
    ){
        PageResult<AssignmentVO> result = assignmentService.getAssignments(classname, page.getPageNum(), page.getPageSize());

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result getAssignmentDetail(@PathVariable Long id) {
        AssignmentDetailVO detail = assignmentService.getAssignmentDetail(id);
        return Result.success(detail);
    }

    @PostMapping
    public Result createAssignment(@RequestBody AssignmentCreateDTO dto) {

        Long id = assignmentService.createAssignment(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result updateAssignment(
            @PathVariable Long id,
            @RequestBody AssignmentCreateDTO dto) {

        assignmentService.updateAssignment(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return Result.success();
    }

    @GetMapping("/export")
    public void exportAssignments(HttpServletResponse response,
                                  @RequestParam(required = false) String classname) throws IOException {

        List<AssignmentExportVO> assignmentList = assignmentService.getExportData(classname);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("作业列表");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"作业ID", "标题", "班级", "发布时间", "截止时间", "提交人数", "总人数", "提交率"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));

        CellStyle percentCellStyle = workbook.createCellStyle();
        percentCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

        int rowNum = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (AssignmentExportVO assignment : assignmentList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(assignment.getAssignmentId());
            row.createCell(1).setCellValue(assignment.getTitle());
            row.createCell(2).setCellValue(assignment.getClassname());

            Cell createDateCell = row.createCell(3);
            if (assignment.getCreatedAt() != null) {
                createDateCell.setCellValue(sdf.format(new Date(assignment.getCreatedAt())));
            } else {
                createDateCell.setCellValue("");
            }

            Cell deadlineCell = row.createCell(4);
            if (assignment.getDeadline() != null) {
                deadlineCell.setCellValue(sdf.format(new Date(assignment.getDeadline())));
            } else {
                deadlineCell.setCellValue("");
            }

            row.createCell(5).setCellValue(assignment.getSubmittedCount());
            row.createCell(6).setCellValue(assignment.getTotalStudents());

            double submitRate = assignment.getTotalStudents() == 0 ? 0 :
                    (double) assignment.getSubmittedCount() / assignment.getTotalStudents();
            Cell rateCell = row.createCell(7);
            rateCell.setCellValue(submitRate);
            rateCell.setCellStyle(percentCellStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "assignment_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        workbook.write(response.getOutputStream());
    }

    @GetMapping("/{id}/grades/export")
    public void exportAssignmentGrades(
            @PathVariable("id") Long assignmentId,
            HttpServletResponse response){
        try {
            // 获取作业基本信息
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);
            if (assignment == null) {
                throw new BusinessException("作业不存在: " + assignmentId);
            }

            // 获取班级所有学生ID
            List<String> studentIds = assignmentService.listStudentIdsByClass(assignment.getClassname());
            if (studentIds.isEmpty()) {
                throw new BusinessException("班级没有学生: " + assignment.getClassname());
            }

            // 获取作业题目列表
            List<AssignmentProblem> problems = assignmentService.getProblemsForAssignment(assignmentId);
            if (problems.isEmpty()) {
                throw new BusinessException("作业没有题目: " + assignmentId);
            }

            // 获取学生成绩数据
            Map<String, Map<Long, Boolean>> studentScores = assignmentService.getStudentScores(assignmentId, problems);

            // 5. 生成Excel
            excelExportService.generateGradeExcel(assignment, problems, studentIds, studentScores, response);

        } catch (BusinessException e) {
            handleExportError(response, HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("导出作业成绩失败: assignmentId={}", assignmentId, e);
            handleExportError(response, HttpStatus.INTERNAL_SERVER_ERROR, "导出失败: " + e.getMessage());
        }
    }


    private void handleExportError(HttpServletResponse response, HttpStatus status, String message) {

        try {
            response.setStatus(status.value());
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(message);
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error("写入错误响应失败", e);
        }

        }

    }
