package com.lmxdawn.him.api.controller.user;


import com.lmxdawn.him.api.service.other.QqWebAuthService;
import com.lmxdawn.him.api.service.user.UserQqService;
import com.lmxdawn.him.api.utils.UserLoginUtils;
import com.lmxdawn.him.api.vo.req.UserLoginPwdReqVO;
import com.lmxdawn.him.api.vo.res.QqOpenIdResVO;
import com.lmxdawn.him.api.vo.res.QqUserInfoResVO;
import com.lmxdawn.him.api.vo.res.UserLoginResVO;
import com.lmxdawn.him.api.service.user.UserService;
import com.lmxdawn.him.api.utils.PasswordUtils;
import com.lmxdawn.him.common.entity.user.User;
import com.lmxdawn.him.common.entity.user.UserQq;
import com.lmxdawn.him.common.enums.ResultEnum;
import com.lmxdawn.him.common.vo.res.BaseResVO;
import com.lmxdawn.him.common.utils.ResultVOUtils;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 登录控制器
 */
@RequestMapping("/user/login")
@RestController
public class UserLoginController {
    
    @Resource
    private UserService userService;
    
    @Resource
    private QqWebAuthService qqWebAuthService;
    
    @Resource
    private UserQqService userQqService;
    
    /**
     * 用户密码登录
     *
     * @param userLoginPwdReqVO
     * @param bindingResult
     * @return
     */
    @PostMapping("/byPwd")
    public BaseResVO byPwd(UserLoginPwdReqVO userLoginPwdReqVO,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }
        
        String userName = userLoginPwdReqVO.getUserName();
        User user = userService.findPwdByUid(userName);
        //String md5Pwd = PasswordUtils.md52md5(userLoginPwdReqVO.getPassword());
        //暂时不对密码做MD5加密处理
        String md5Pwd = userLoginPwdReqVO.getPassword();
        if (user == null || !md5Pwd.equals(user.getPwd())) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "密码或用户名错误~");
        }
        String token = UserLoginUtils.createSid(user.getUid());
        
        UserLoginResVO userLoginResVO = new UserLoginResVO();
        userLoginResVO.setUid(user.getUid());
        userLoginResVO.setSid(token);
        return ResultVOUtils.success(userLoginResVO);
    }
    
    /**
     * 游客登录
     *
     * @return
     */
    @PostMapping("/byTourist")
    public BaseResVO byTourist(@RequestParam(value = "sex") Integer type) {
    
        if (type != 1 && type != 2) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "请选择游客性别~");
        }
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String name = "有缘人" + RandomStringUtils.random(7, chr);
        String avatar = String.format("http://47.100.212.206/avatar/%d.jpg", type);
        String remark = "念念不忘";
        // 创建用户
        User user = new User();
        user.setName(name);
        user.setAvatar(avatar);
        user.setRemark(remark);
        boolean b = userService.insertUser(user);
        if (!b) {
            return ResultVOUtils.error();
        }
        
        Long uid = user.getUid();
        String token = UserLoginUtils.createSid(uid);
        
        UserLoginResVO userLoginResVO = new UserLoginResVO();
        userLoginResVO.setUid(uid);
        userLoginResVO.setSid(token);
        return ResultVOUtils.success(userLoginResVO);
    }
    
    /**
     * 第三方QQ登录
     *
     * @return
     */
    @PostMapping("/byQq")
    public BaseResVO byQq(@RequestParam(value = "code") String code,
                          @RequestParam(value = "redirect_uri") String redirect_uri) {
    
        //code为qq第三方登入后获取的Authorization code ,redirect_uri为跳转时的uri
        //accessToken即为我们后端取到的token
        String accessToken = qqWebAuthService.getAccessToken(code, redirect_uri);
        if (accessToken == null || "".equals(accessToken)) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "accessToken 获取失败~");
        }
        //获取openid,这个openIdResVO里面包含openid,这个openid与用户QQ号一一对应，不会改变
        QqOpenIdResVO openIdResVO = qqWebAuthService.getOpenID(accessToken);
        if (openIdResVO == null || openIdResVO.getOpenid() == null || "".equals(openIdResVO.getOpenid())) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "openid 获取失败~");
        }
        //获取用户信息 根据openId和accessToken 获取用户信息
        QqUserInfoResVO userInfo = qqWebAuthService.getUserInfo(accessToken, openIdResVO.getOpenid());
        
        if (userInfo == null) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "userInfo 获取失败~");
        }
    
        UserQq userQq = userQqService.findByOpenId(openIdResVO.getOpenid());
        
        // 判断是否有过值
        if (userQq == null || userQq.getUid() == null) {
            // 创建用户
            User user = new User();
            String nickname = userInfo.getNickname();
            user.setName(nickname);
            user.setAvatar(userInfo.getFigureurl_qq_2());
            //判断是否是她
            String remark =null;
            if("☆".equals(nickname)){
                remark = "但凡未得到,但凡是过去,总是最登对!";
            }else {
                remark = "历尽千帆归来仍是少年";
            }
            user.setRemark(remark);
            boolean b = userService.insertUser(user);
            if (!b) {
                return ResultVOUtils.error();
            }
            // 创建QQ用户
            userQq = new UserQq();
            userQq.setUid(user.getUid());
            userQq.setOpenid(openIdResVO.getOpenid());
            boolean b1 = userQqService.insertUserQq(userQq);
            if (!b1) {
                return ResultVOUtils.error();
            }
        }
    
        Long uid = userQq.getUid();

        //使用jwt加密用户信息
        String sid = UserLoginUtils.createSid(uid);
        
        UserLoginResVO userLoginResVO = new UserLoginResVO();
        userLoginResVO.setUid(uid);
        userLoginResVO.setSid(sid);
        return ResultVOUtils.success(userLoginResVO);
    }
    
    
}
