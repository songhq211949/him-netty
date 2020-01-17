package com.lmxdawn.him.api.service.user;

import com.lmxdawn.him.api.vo.res.UserInfoListResVO;
import com.lmxdawn.him.common.entity.user.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User findByUid(Long uid);

    User findPwdByUid(String userName);
    
    Map<Long, UserInfoListResVO> listByUidIn(List<Long> uids);

    //这个会返回对象中的uid
    boolean insertUser(User user);

    boolean updateUser(User user);

    boolean deleteByUid(Long uid);

}
