package com.ihrdata.demo.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.web.param.RbacUserAddParam;
import com.ihrdata.demo.web.param.RbacUserPagingParam;
import com.ihrdata.demo.web.vo.RbacUserAndResetPasswordVo;
import com.ihrdata.demo.web.vo.RbacUserQueryVo;
import com.ihrdata.demo.web.vo.RbacUserVo;

/**
 * 用户service
 *
 * @Author wangwz
 * @Date 2020/11/24
 */
public interface RbacUserService {
    /**
     * 获取全部用户(分页)
     */
    PageInfo<RbacUserVo> queryAll(RbacUserPagingParam param);

    /**
     * 获取全部用户
     */
    List<RbacUserQueryVo> queryAllRbacUser();

    /**
     * 添加用户
     */
    void add(RbacUserAddParam param);

    /**
     * 根据ID获取用户信息
     */
    RbacUserVo get(Long id);

    /**
     * 获取全部
     */
    RbacUserAndResetPasswordVo getAll();

    /**
     * 修改
     */
    void update(RbacUserAddParam param);

    /**
     * 删除
     */
    void delete(Long loginUserId, Long id);

    /**
     * 根据用户名查询用户信息(分页)
     */
    List<RbacUser> getByUserName(String userName);

    /**
     * 修改用户状态
     */
    void updateStatus(Long userId, Byte userStatus);

    /**
     * 重置密码
     */
    void resetPassword(Long id,String token);

    /**
     * 根据Phone获取用户信息
     *
     * @Author wangwz
     * @Date 2021/5/25
     * @Params @param phone:
     * @return com.ihrdata.demo.model.RbacUser
     */
    RbacUser getByUserId(String userId);

}
