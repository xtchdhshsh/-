package comlisanshuxue.lisan.classmaneger;


import comlisanshuxue.lisan.Mapper.ClassMapper;
import comlisanshuxue.lisan.Mapper.UserMapper;
import comlisanshuxue.lisan.Utils.JWTUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class classmanager {

    @Autowired
    private ClassMapper classmapper;

    @Autowired
    private UserMapper usermapper;
    @RequestMapping("/addclass")
    public String addclass(@RequestHeader("token") String token, String classname,String cl){
        String teacher = JWTUtils.getUsername(token);
        int re = classmapper.addclass(classname,teacher,cl);
        if(re==1) return "success";
        else return "fail";
    }

    @RequestMapping("/getclass")
    public Result getclass(@RequestHeader("token") String token){
        String teacher = JWTUtils.getUsername(token);
        ArrayList<String> classname = new ArrayList<>();
        ArrayList <String> cl = new ArrayList<>();
        for (Map<String, String> map : classmapper.getclass(teacher)){
            classname.add(map.get("classname"));
            cl.add(map.get("class"));
        }
        return new Result(teacher,classname,cl);
    }

    @RequestMapping("/delclass")
    public String delclass(@RequestHeader("token") String token, String classname,String cl){
        String teacher = JWTUtils.getUsername(token);
        classmapper.delclass(classname,teacher,cl);
        classmapper.delstudent2(classname,teacher,cl);
        return "success";
    }

    @RequestMapping("/addstudent")
    public String addstudent(String classname,String studentname,@RequestHeader("token") String token, String cl){
        String teacher = JWTUtils.getUsername(token);
        int re = classmapper.addstudent(classname,studentname,teacher,cl);
        if(re==1) return "success";
        else return "fail";
    }
    @RequestMapping("/getstudent")
    public String getstudent(String classname,@RequestHeader("token") String token, String cl){
        String teacher = JWTUtils.getUsername(token);
        ArrayList<String> re = new ArrayList<>();
        for (Map<String, String> map : classmapper.getstudent(classname,teacher,cl))
            re.add(map.values().toString());
        return re.toString();
    }

    @RequestMapping("/delstudent")
    public String delstudent(String classname,@RequestHeader("token") String token, String cl,String studentname){
        String teacher = JWTUtils.getUsername(token);
        classmapper.delstudent(classname,studentname,teacher,cl);
        return "success";
    }

    @PostMapping("/addstudentbyexcel")
    public String addByExcel(@RequestParam("file") MultipartFile file, @RequestHeader("token") String token) throws IOException {
         String teacher = JWTUtils.getUsername(token);
        // 验证文件类型
        String contentType = file.getContentType();
        if (!(contentType != null
                && (contentType.equals("application/vnd.ms-excel")
                ||contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))))
        {
            return "仅支持.xls和.xlsx格式文件";
        }

        InputStream inputStream = file.getInputStream();

        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(0);
        int maxRow = sheet.getLastRowNum();

        String cl = sheet.getRow(2).getCell(0).getStringCellValue().substring(3);

        String classname = sheet.getRow(3).getCell(4).getStringCellValue();

        classmapper.addclass(classname,teacher,cl);

        for (int row = 5; row < maxRow; row++) {
            Row currentRow = sheet.getRow(row);
            String studentnum = currentRow.getCell(1).getStringCellValue();
            String studentname = currentRow.getCell(2).getStringCellValue();
            String studentcl = currentRow.getCell(3).getStringCellValue();
            classmapper.addstudent(classname,studentnum,teacher,cl);
            usermapper.newregister(studentname,studentnum,studentcl);
        }
        return "success";
    }

     @PostMapping("/updateclassname")
     public String updateClassName(String newclassname,String oldclassname,String cl ,@RequestHeader("token") String token){
        String teacher = JWTUtils.getUsername(token);
        classmapper.updateclassname(newclassname,oldclassname,teacher,cl);
        classmapper.updateclassname2(newclassname,oldclassname,teacher,cl);
        return "success";
     }
}
