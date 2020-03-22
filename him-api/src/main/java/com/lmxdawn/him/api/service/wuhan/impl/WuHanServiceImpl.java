package com.lmxdawn.him.api.service.wuhan.impl;

import com.lmxdawn.him.api.dao.wuhan.WuHanDao;
import com.lmxdawn.him.api.service.wuhan.WuHanService;
import com.lmxdawn.him.api.vo.res.WuHanVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class WuHanServiceImpl implements WuHanService {

    @Resource
    private WuHanDao wuHanDao;

    @Override
    public List<List<String>> list(String type) {
        List<WuHanVO> list = wuHanDao.list();
        ArrayList<List<String>> result = new ArrayList<>();
        ArrayList<String> first = new ArrayList<>();
        first.add("日期");
        first.add("确诊新增");
        first.add("确诊总数");
        first.add("死亡新增");
        first.add("死亡总数");
        first.add("出院新增");
        first.add("出院总数");
        result.add(first);
        if(!CollectionUtils.isEmpty(list)){
            for (WuHanVO wuHanVO : list) {
                ArrayList<String> oneList = new ArrayList<>();
                oneList.add(wuHanVO.getDate());
                oneList.add(wuHanVO.getSureAdd());
                oneList.add(wuHanVO.getSureSum());
                oneList.add(wuHanVO.getDieAdd());
                oneList.add(wuHanVO.getDieSum());
                oneList.add(wuHanVO.getRecoveryAdd());
                oneList.add(wuHanVO.getRecoverySum());
                result.add(oneList);
            }
        }
        return result;
    }
}
