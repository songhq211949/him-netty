package com.lmxdawn.him.api.dao.wuhan;

import com.lmxdawn.him.api.vo.res.WuHanVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WuHanDao {


    List<WuHanVO> list();

}
