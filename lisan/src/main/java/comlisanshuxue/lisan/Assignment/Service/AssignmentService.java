package comlisanshuxue.lisan.Assignment.Service;

import comlisanshuxue.lisan.Assignment.Mapper.*;
import comlisanshuxue.lisan.Assignment.entity.*;
import comlisanshuxue.lisan.entity.SharedData;
import comlisanshuxue.lisan.entity.SubmitRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private AssignmentProblemMapper assignmentProblemMapper;

    @Autowired
    private SubmitRecordMapper1 submitRecordMapper;

    @Autowired
    private ClassStudentMapper classStudentMapper;

    @Autowired
    private SharedDataMapper1 sharedDataMapper;

    public PageResult<AssignmentVO> getAssignments(String classname, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        // 查询作业基本信息
        List<Assignment> assignments = assignmentMapper.selectAssignments(classname, offset, pageSize);
        long total = assignmentMapper.countAssignments(classname);

        List<AssignmentVO> voList = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentVO vo = new AssignmentVO();
            vo.setAssignmentId(assignment.getAssignmentId());
            vo.setTitle(assignment.getTitle());
            vo.setClassname(assignment.getClassname());
            vo.setAssignTime(assignment.getCreatedAt());
            vo.setDeadline(assignment.getDeadline());

            try {
            List<Long> problemIds = assignmentMapper.selectProblemIds(assignment.getAssignmentId())
                    .stream().map(AssignmentProblem::getProblemId)
                    .collect(Collectors.toList());

            // 统计已提交人数
            if (!problemIds.isEmpty()) {
                    Integer submittedCount = submitRecordMapper.countSubmittedStudents(
                            assignment.getClassname(), problemIds);
                    // 防止数据库返回null
                    vo.setSubmittedCount(submittedCount != null ? submittedCount : 0);
            } else {
                vo.setSubmittedCount(0);
                }
            } catch (Exception e) {
                logger.error("获取作业提交人数失败，作业ID: {}", assignment.getAssignmentId(), e);
                vo.setSubmittedCount(0); // 出错时默认显示为0，保证页面健壮性
            }

            // 统计班级总人数
            vo.setTotalStudents(classStudentMapper.countStudentsByClass(assignment.getClassname()));
            voList.add(vo);
        }

        return new PageResult<>(total, voList);
    }

    public AssignmentDetailVO getAssignmentDetail(Long id) {
        Assignment assignment = assignmentMapper.selectById(id);
        if(assignment == null){
            throw new BusinessException("作业不存在");
        }

        AssignmentDetailVO vo = new AssignmentDetailVO();
        vo.setAssignmentId(assignment.getAssignmentId());
        vo.setTitle(assignment.getTitle());
        vo.setDescription(assignment.getDescription());
        vo.setCreatedAt(assignment.getCreatedAt());
        vo.setDeadline(assignment.getDeadline());
        vo.setClassname(assignment.getClassname());

        // 查询题目关联
        List<AssignmentProblem> problems = assignmentProblemMapper.findByAssignmentId(id);
        vo.setProblems(new ArrayList<>());

        if (problems == null || problems.isEmpty()) {
            vo.setTotalPoints(0);
            return vo; // 如果没有关联问题，提前返回
        }

        // 计算总分
        int totalPoints = problems.stream().mapToInt(AssignmentProblem::getPoints).sum();
        vo.setTotalPoints(totalPoints);

        // 查询题目信息
        List<Long> problemIds = problems.stream()
                .map(AssignmentProblem::getProblemId)
                .collect(Collectors.toList());

        Map<Long, String> problemTitles = new HashMap<>();
        try {
            // 确保 problemIds 非空，避免不必要的查询和潜在的SQL错误
            if (!problemIds.isEmpty()) {
        List<SharedData> sharedDatas = sharedDataMapper.findByIds(problemIds);
                if (sharedDatas != null) {
                    problemTitles = sharedDatas.stream()
                            .filter(sd -> sd.getId() != null && sd.getContent() != null) // 过滤无效数据
                            .collect(Collectors.toMap(SharedData::getId, SharedData::getContent, (oldValue, newValue) -> oldValue)); // 防止重复键
                }
            }
        } catch (Exception e) {
            logger.error("为作业 {} 查询题目详情时出错: {}", id, e.getMessage(), e);
            // 查询失败，不中断流程，后续步骤会使用默认值
        }

        // 构建题目VO
        for (AssignmentProblem problem : problems) {
            ProblemVO pvo = new ProblemVO();
            pvo.setProblemId(problem.getProblemId());
            pvo.setPoints(problem.getPoints());
            pvo.setProblemTitle(problemTitles.getOrDefault(
                    problem.getProblemId(), "加载题目信息失败")); // 提供更明确的错误提示
            vo.getProblems().add(pvo);
        }

        return vo;
    }

    public Long createAssignment(AssignmentCreateDTO dto) {

        // 创建作业记录
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setClassname(dto.getClassname());
        assignment.setCreatedAt(System.currentTimeMillis());
        assignment.setDeadline(dto.getDeadline());

        assignmentMapper.insert(assignment);
        Long assignmentId = assignment.getAssignmentId();

        // 创建题目关联
        assignmentProblemMapper.batchInsert(assignmentId, dto.getProblems());

        return assignmentId;
    }

    public void updateAssignment(Long id, AssignmentCreateDTO dto) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(id);
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setClassname(dto.getClassname());
        assignment.setDeadline(dto.getDeadline());


        //  更新作业
        int rows = assignmentMapper.update(assignment);
        if (rows == 0) {
            throw new BusinessException("作业不存在");
        }

        //  删除原作业问题
        assignmentProblemMapper.deleteByAssignmentId(id);

        //  插入新作业问题
        assignmentProblemMapper.batchInsert(id, dto.getProblems());
    }

    public void deleteAssignment(Long id) {
        assignmentProblemMapper.deleteByAssignmentId(id);

        int rows = assignmentMapper.delete(id);
        if (rows == 0) {
            throw new BusinessException("作业不存在");
        }
    }

    public List<String> getAllClasses() {
        return classStudentMapper.findAllClassNames();
    }

    public List<AssignmentExportVO> getExportData(String classname) {

        // 1. 获取作业列表（注意命名转换）
        List<Assignment> assignments = assignmentMapper.selectByCondition(classname);

        // 记录所有assignmentId（驼峰命名）用于调试
        List<Long> assignmentIds = assignments.stream()
                .map(Assignment::getAssignmentId)
                .collect(Collectors.toList());
        logger.info("作业ID列表: {}", assignmentIds);

        if (assignments.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 提取涉及的班级
        Set<String> classNames = assignments.stream()
                .map(Assignment::getClassname)
                .collect(Collectors.toSet());

        // 3. 批量获取班级总人数
        Map<String, Integer> classTotalMap = classNames.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        classStudentMapper::countStudentsByClass
                ));

        // 4. 提取assignmentId并确保非空
        Set<Long> validAssignmentIds = assignments.stream()
                .map(Assignment::getAssignmentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 5. 获取作业题目关联
        Map<Long, List<Long>> assignmentProblemMap = new HashMap<>();
        if (!validAssignmentIds.isEmpty()) {
            List<AssignmentProblem> problems = assignmentProblemMapper.findByAssignmentIds(validAssignmentIds);
            assignmentProblemMap = problems.stream()
                    .collect(Collectors.groupingBy(
                            AssignmentProblem::getAssignmentId,
                            Collectors.mapping(AssignmentProblem::getProblemId, Collectors.toList())
                    ));
        }

        // 6. 构建导出数据VO（保持驼峰命名）
        List<AssignmentExportVO> result = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentExportVO vo = new AssignmentExportVO();
            // 保持驼峰命名赋值
            vo.setAssignmentId(assignment.getAssignmentId());
            vo.setTitle(assignment.getTitle());
            vo.setClassname(assignment.getClassname());
            vo.setCreatedAt(assignment.getCreatedAt());
            vo.setDeadline(assignment.getDeadline());

            // 设置班级总人数
            vo.setTotalStudents(classTotalMap.getOrDefault(assignment.getClassname(), 0));

            // 设置提交人数（使用assignmentId）
            List<Long> problemIds = assignmentProblemMap.get(assignment.getAssignmentId());
            if (problemIds != null && !problemIds.isEmpty()) {
                vo.setSubmittedCount(
                        submitRecordMapper.countDistinctStudentsByProblems(
                                assignment.getClassname(),
                                problemIds
                        )
                );
            } else {
                vo.setSubmittedCount(0);
            }

            result.add(vo);
        }

        return result;

        }

    public Assignment getAssignmentById(Long assignmentId) {

        return assignmentMapper.selectById(assignmentId);
    }

    public List<String> listStudentIdsByClass(String classname) {

        return classStudentMapper.listStudentIdsByClass(classname);

    }

    public List<AssignmentProblem> getProblemsForAssignment(Long assignmentId) {

        return assignmentProblemMapper.findByAssignmentId(assignmentId);

    }


    public Map<String, Map<Long, Boolean>> getStudentScores(
            Long assignmentId,
            List<AssignmentProblem> problems) {

        List<Long> problemIds = problems.stream()
                .map(AssignmentProblem::getProblemId)
                .collect(Collectors.toList());

        if (problemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SubmitRecord> lastSubmits = submitRecordMapper.findLastSubmitsForAssignment(problemIds);

        Map<String, Map<Long, Boolean>> result = new HashMap<>();

        for (SubmitRecord record : lastSubmits) {
            String studentId = record.getStudentId();
            Long problemId = record.getQuestionId();

            result.computeIfAbsent(studentId, k -> new HashMap<>())
                    .put(problemId, record.getIsCorrect());
        }

        return result;

    }
}
