package comlisanshuxue.lisan.classmaneger;


import comlisanshuxue.lisan.Mapper.ClassMapper;
import comlisanshuxue.lisan.Utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestController
public class classmanager {

    @Autowired
    private ClassMapper classmapper;
    @RequestMapping("/addclass")
    public String addclass(@RequestHeader("token") String token, String classname){
        String teacher = JWTUtils.getUsername(token);
        int re = classmapper.addclass(classname,teacher);
        if(re==1) return "success";
        else return "fail";
    }

    @RequestMapping("/getclass")
    public Result getclass(@RequestHeader("token") String token){
        String teacher = JWTUtils.getUsername(token);
        ArrayList<String> classname = new ArrayList<>();
        for (Map<String, String> map : classmapper.getclass(teacher))
            classname.add(map.values().toString());
        Result re = new Result(teacher,classname);
        return re;
    }

    @RequestMapping("/addstudent")
    public String addstudent(String classname,String studentname){
        int re = classmapper.addstudent(classname,studentname);
        if(re==1) return "success";
        else return "fail";
    }
    @PostMapping("/addstudentbyexcel")
    public String addstudentbyexcel(@RequestParam("file") MultipartFile file,String classname) throws IOException {
        if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return "仅支持.xlsx格式文件";
        }
        InputStream inputStream = file.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        int maxRow = sheet.getLastRowNum();
        for (int row = 0; row <= maxRow; row++) {
            classmapper.addstudent(classname, String.valueOf(sheet.getRow(row).getCell(0)));
        }
        return "success";
    }

    @RequestMapping("/getstudent")
    public String getstudent(String classname){
        ArrayList<String> re = new ArrayList<>();
        for (Map<String, String> map : classmapper.getstudent(classname))
            re.add(map.values().toString());
        return re.toString();
    }

}
