package comlisanshuxue.lisan.Addbyexcel;

import comlisanshuxue.lisan.Mapper.ClassMapper;
import comlisanshuxue.lisan.Mapper.UserMapper;
import comlisanshuxue.lisan.Utils.JWTUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class addbyexcel {

    @Autowired
    private ClassMapper classmapper;

    @Autowired
    private UserMapper usermapper;

    @PostMapping("/addbyexcel")
    public String addByExcel(@RequestParam("file") MultipartFile file) throws IOException {
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

        String teacher = sheet.getRow(2).getCell(19).getStringCellValue();

        classmapper.newaddclass(classname,teacher,cl);

        for (int row = 5; row < maxRow; row++) {
                Row currentRow = sheet.getRow(row);
                String studentnum = currentRow.getCell(1).getStringCellValue();
                String studentname = currentRow.getCell(2).getStringCellValue();
                String studentcl = currentRow.getCell(3).getStringCellValue();
                classmapper.newaddstudent(classname,studentnum,teacher,cl);
                usermapper.newregister(studentname,studentnum,studentcl);
        }
        return "success";
    }
}
