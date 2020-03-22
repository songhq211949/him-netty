package com.lmxdawn.him.api.service.wuhan;

import com.lmxdawn.him.api.vo.res.WuHanVO;

import java.util.List;

public interface WuHanService {

    List<List<String>> list(String type);
}
