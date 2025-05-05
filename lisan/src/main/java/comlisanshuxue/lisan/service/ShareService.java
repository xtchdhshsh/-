package comlisanshuxue.lisan.service;

import comlisanshuxue.lisan.entity.SharedData;
import comlisanshuxue.lisan.Mapper.SharedDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShareService {
    @Autowired
    private SharedDataMapper sharedDataMapper;

    public Long saveSharedData(String content) {
        SharedData sharedData = new SharedData();
        sharedData.setContent(content);
        sharedData.setCreateTime(new Date().getTime());
        sharedDataMapper.insert(sharedData);
        return sharedData.getId();
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

