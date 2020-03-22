package com.lmxdawn.him.api.controller.wuhan;

import com.lmxdawn.him.api.dto.UserLoginDTO;
import com.lmxdawn.him.api.service.wuhan.WuHanService;
import com.lmxdawn.him.api.utils.PageUtils;
import com.lmxdawn.him.api.utils.UserLoginUtils;
import com.lmxdawn.him.api.vo.res.WuHanVO;
import com.lmxdawn.him.common.entity.user.UserFriendMsg;
import com.lmxdawn.him.common.enums.ResultEnum;
import com.lmxdawn.him.common.utils.ResultVOUtils;
import com.lmxdawn.him.common.vo.res.BaseResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/wuhan")
@RestController
public class WuHanController {

    @Autowired
    private WuHanService wuHanService;

    @GetMapping("/list")
    public BaseResVO lists(HttpServletRequest request,@RequestParam(value = "type",required = false) String type) {
        List<List<String>>  list = wuHanService.list(type);
        return ResultVOUtils.success(list);
       // return ResultVOUtils.noLogin();
    }


}
