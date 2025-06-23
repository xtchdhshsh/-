package comlisanshuxue.lisan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import comlisanshuxue.lisan.entity.SharedData;
import comlisanshuxue.lisan.Mapper.SharedDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class ShareService {
    @Autowired
    private SharedDataMapper sharedDataMapper;

    // 修改保存方法
    public Long saveSharedData(SharedData sharedData) {
        sharedDataMapper.insert(sharedData);
        return sharedData.getId();
    }

    // 新增方法：根据id和type获取答案
    public String getAnswerByIdAndType(Long id, String type) {
        SharedData data = sharedDataMapper.selectByIdAndType(id, type);
        if (data == null) return null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(data.getContent());
            return root.get("answer").asText();
        } catch (IOException e) {
            return null; // 解析失败返回null
        }
    }

    // 新增方法：根据类型查询
    public List<SharedData> findByType(String type) {
        return sharedDataMapper.selectByType(type);
    }


    public SharedData getSharedData(Long id) {
        return sharedDataMapper.selectById(id);
    }

    public List<SharedData> findAll(){
        return sharedDataMapper.selectAll();
    }

    public boolean deleteSharedData(Long id) {
        return sharedDataMapper.deleteById(id) > 0;
    }
}