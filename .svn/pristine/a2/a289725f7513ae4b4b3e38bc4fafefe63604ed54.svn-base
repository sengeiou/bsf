package com.weking.mapper.account;

import com.weking.model.account.AccountInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountInfoMapper {


    int updateRatioByUserid(@Param("ratio") BigDecimal ratio, @Param("userId") int userId);

    int clearGetuiInfo(@Param("cid") String cid);

    int updateGetuiInfo(@Param("id") int userId, @Param("cid") String cid, @Param("devicetoken") String devicetoken);

    int insertSelective(AccountInfo record);

    List<AccountInfo> batchSelectUsers(@Param("set")Set accounts); //批量查询用户

    AccountInfo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(AccountInfo record);

    AccountInfo selectByAccountId(String account); //通过account获取信息

    Integer findUserIdByNickname(String nickname);

    Integer loginByPhone(@Param("phone") String phone,@Param("password")String password); //通过手机登录

    Integer findByPassword (@Param("userId") int userId,@Param("password")String password); //验证用户密码

    int updatePwdByPhone(@Param("phone") String phone,@Param("password")String password,@Param("areaCode")String areaCode); //修改手机密码

    Integer loginByWxNum(@Param("wxNum") String wxNum);  //通过微信unionid登录

    Integer selectByInviteCode(String inviteCode); //通过邀请码获得用户userId

    Integer loginByEmail(@Param("email") String email, @Param("password") String password);  //通过邮箱登录

    Integer loginByFacebook(@Param("fbNum") String fbNum); //通过facebook登录

    Integer loginByKakao(@Param("kakaoNum") String kakaoNum); //通过kaokao登录

    Integer loginByGoogle(@Param("googleNum") String googleNum);  //通过谷歌登录

    Integer loginByAppleNum(@Param("apple_num") String apple_num);  //通过苹果登录

    Integer loginByLineNum(@Param("line_num") String line_num);  //通过line登录


    Integer loginByTwitter(@Param("twitterNum") String twitterNum); //通过推特登录

    int verifyAccountId(String account);//验证该咔嚓号是否已经存在

    int verifyEmail(String email); //验证邮箱是否存在

    List<AccountInfo> getFans(@Param("user_id") int user_id, @Param("offset") int offset, @Param("limit") int limit); //通过user_id列表获取信息

    List<AccountInfo> getStarts(@Param("user_id") int user_id, @Param("offset") int offset, @Param("limit") int limit); //通过user_id 列表获取信息

    List<AccountInfo> searchAcount(AccountInfo record);

    int updateUserNumById(AccountInfo record); //绑定第三方账号

    int cancelBingByUserId(AccountInfo record); //取消绑定

    int selectByThirdNum(AccountInfo record);

    int updateExperience(@Param("userId")int userId,@Param("experience")int experience,@Param("level")int level);

    int updateAnchorExperience(@Param("userId")int userId,@Param("anchor_experience")int anchor_experience,@Param("anchor_level")int anchor_level);

    int updateVipExperience(@Param("userId")int userId,@Param("vip_experience")int vip_experience,@Param("vip_level")int vip_level);

    int delUser(int userId);

    List<AccountInfo> selectAnchorList(@Param("userId")int userId,@Param("offset") int index,@Param("limit") int count);

    List<AccountInfo> selectRecommendAnchorList(@Param("userId")int userId,@Param("offset") int index,@Param("limit") int count);

    Integer findUserIdByAccount(String account);

    Integer updateParentIdByUserId(@Param("parentId") int parentId,@Param("userId") int userId);

    int selectInviteCount(int userId);  //查询邀请数量

    List<AccountInfo> selectInviteList(@Param("userId") int userId,@Param("index")int index,@Param("count")int count);  //查询邀请列表

    AccountInfo findUserMapByAccount(String account);

    Map<String,String> findUserInfoByUserId(Integer id);

    List<AccountInfo> selectNormalUserList(@Param("offset")int index,@Param("limit")int count);

    List<Map<String,Object>> selectUserListByLanguage(@Param("lang") String lang, @Param("offset") int index,@Param("limit") int count);


}