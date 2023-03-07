package com.ihrdata.demo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ihrdata.demo.common.jwt.JWTUtils;
import com.ihrdata.demo.common.shiro.crypto.ShiroCrypto;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.dao.*;
import com.ihrdata.demo.model.*;
import com.ihrdata.demo.service.RbacUserService;
import com.ihrdata.demo.web.param.RbacUserAddParam;
import com.ihrdata.demo.web.param.RbacUserPagingParam;
import com.ihrdata.demo.web.vo.RbacResourcesVo;
import com.ihrdata.demo.web.vo.RbacUserAndResetPasswordVo;
import com.ihrdata.demo.web.vo.RbacUserQueryVo;
import com.ihrdata.demo.web.vo.RbacUserVo;
import com.ihrdata.wtool.common.enums.DataStatusEnum;
import com.ihrdata.wtool.common.exception.BusinessException;
import com.ihrdata.wtool.common.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service("rbacUserService")
public class RbacUserServiceImpl implements RbacUserService {
    @Value("${server.default.password}")
    private String resetPassword;

    @Autowired
    private RbacOrganizationMapper rbacOrganizationMapper;

    @Autowired
    private RbacUserMapper rbacUserMapper;

    @Autowired
    private RbacRoleUserMapper rbacRoleUserMapper;

    @Autowired
    private RbacRoleMapper rbacRoleMapper;

    @Autowired
    private RbacRoleResourceMapper rbacRoleResourceMapper;

    @Autowired
    private RbacResourceMapper rbacResourceMapper;

    @Autowired
    private RbacUserOrganizationMapper rbacUserOrganizationMapper;

    @Autowired
    private RbacUserCustomMapper rbacUserCustomMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageInfo<RbacUserVo> queryAll(RbacUserPagingParam param) {
        // 分页处理
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<RbacUser> rbacUser = rbacUserCustomMapper.selectAllRole(param);

        PageInfo<RbacUser> pInfo = new PageInfo<RbacUser>(rbacUser);

        ArrayList<RbacUserVo> rbacUserVoList = new ArrayList<>();
        for (RbacUser user : rbacUser) {
            // 根据UserId从中间表中获得RoleId
            Example exampleByUserId = new Example(RbacRoleUser.class);
            exampleByUserId.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
                .andEqualTo("userId", user.getId());

            RbacRoleUser rbacRoleUser = rbacRoleUserMapper.selectOneByExample(exampleByUserId);

            RbacUserVo rbacUserVo = new RbacUserVo();
            rbacUserVo.setUserId(user.getId());
            rbacUserVo.setNickname(user.getNickname());
            rbacUserVo.setUsername(user.getUsername());
            rbacUserVo.setUserStatus(user.getUserStatus());
            rbacUserVo.setGmtCreate(user.getGmtCreate());

            // 该用户可能没有绑定角色
            if (rbacRoleUser != null) {
                RbacRole rbacRole = rbacRoleMapper.selectByPrimaryKey(rbacRoleUser.getRoleId());
                rbacUserVo.setRoleName(rbacRole.getRoleName());
                rbacUserVo.setRoleId(rbacRole.getId());
            }

            // 添加部门Id和部门名称
            Example exampleByUserIdForOrganization = new Example(RbacUserOrganization.class);
            exampleByUserIdForOrganization.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
                .andEqualTo("userId", user.getId());
            RbacUserOrganization rbacUserOrganization = rbacUserOrganizationMapper
                .selectOneByExample(exampleByUserIdForOrganization);

            RbacOrganization organization =
                rbacOrganizationMapper.selectByPrimaryKey(rbacUserOrganization.getOrganizationId());
            rbacUserVo.setOrganizationId(organization.getId());
            rbacUserVo.setOrganizationName(organization.getOrganizationName());
            rbacUserVoList.add(rbacUserVo);
        }
        PageInfo<RbacUserVo> pageInfo = new PageInfo<RbacUserVo>(rbacUserVoList);
        pageInfo.setTotal(pInfo.getTotal());
        pageInfo.setPages(pInfo.getPages());
        return pageInfo;
    }

    /**
     * 查询全部用户
     * @return
     */
    @Override
    public List<RbacUserQueryVo> queryAllRbacUser() {
        Example example = new Example(RbacUser.class);
        example.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        List<RbacUser> rbacUsers = rbacUserMapper.selectByExample(example);
        List<RbacUserQueryVo> rbacUserQueryVos = new ArrayList<>();
        RbacUserQueryVo rbacUserQueryVo;
        for (RbacUser rbacUser : rbacUsers) {
            rbacUserQueryVo = new RbacUserQueryVo();
            BeanUtils.copyProperties(rbacUser, rbacUserQueryVo);
            rbacUserQueryVos.add(rbacUserQueryVo);
        }
        return rbacUserQueryVos;
    }

    @Transactional
    @Override
    public void add(RbacUserAddParam param) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(param.getToken(), "userid"));

        // 校验用户名及手机号是否被注册
        checkUsername(param.getUsername(), null);
        checkPhone(param.getPhone());

        RbacUser rbacUser = RbacUser.builder().nickname(param.getNickname()).username(param.getUsername())
            .password(ShiroCrypto.encryptPassword(resetPassword)).phone(param.getPhone())
            .createUser(userId).build();

        // 添加账号
        rbacUserMapper.insertSelective(rbacUser);

        // 添加账号和角色关联
        if (!ValidationUtil.isEmpty(param.getRoleId())) {
            Example exampleByUsername = new Example(RbacUser.class);
            exampleByUsername.createCriteria().andEqualTo("username", param.getUsername());
            RbacUser rbacUserByUsername = rbacUserMapper.selectOneByExample(exampleByUsername);

            RbacRoleUser rbacRoleUser =
                RbacRoleUser.builder().userId(rbacUserByUsername.getId()).roleId(param.getRoleId()).createUser(userId)
                    .build();

            rbacRoleUserMapper.insertSelective(rbacRoleUser);
        }

        // 添加部门
        if (!ValidationUtil.isEmpty(param.getOrganizationId())) {
            RbacUserOrganization rbacUserOrganization =
                RbacUserOrganization.builder().organizationId(param.getOrganizationId()).userId(rbacUser.getId())
                    .createUser(userId).build();
            rbacUserOrganizationMapper.insertSelective(rbacUserOrganization);
        }
    }

    private void checkUsername(String userName, Long id) {
        // 创建查询条件roleName、并返回有效条数
        Example example = new Example(RbacUser.class);
        example.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("username", userName);
        int countByExample = rbacUserMapper.selectCountByExample(example);

        if (countByExample != 0) {
            throw new BusinessException("当前账户已被占用");
        }
    }

    @Override
    public RbacUserVo get(Long id) {
        RbacUser rbacUser = checkExist(id);

        // 创建查询条件
        Example exampleByUserId = new Example(RbacRoleUser.class);
        exampleByUserId.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("userId", id);
        RbacRoleUser rbacRoleUser = rbacRoleUserMapper.selectOneByExample(exampleByUserId);
        RbacRole role = rbacRoleMapper.selectByPrimaryKey(rbacRoleUser.getRoleId());

        RbacUserVo rbacUserVo = new RbacUserVo();
        rbacUserVo.setUsername(rbacUser.getUsername());
        rbacUserVo.setNickname(rbacUser.getNickname());
        rbacUserVo.setPassword(rbacUser.getPassword());
        rbacUserVo.setRoleName(role.getRoleName());
        rbacUserVo.setRoleId(role.getId());
        rbacUserVo.setUserId(rbacUser.getId());

        // 创建资源名及URL集合
        ArrayList<RbacResourcesVo> resourcesList = Lists.newArrayList();

        // 查询拥有的资源
        Example exampleByRoleId = new Example(RbacRoleResource.class);
        exampleByRoleId.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("roleId", role.getId());
        List<RbacRoleResource> rbacRoleResourceList = rbacRoleResourceMapper.selectByExample(exampleByRoleId);
        for (RbacRoleResource rbacRoleResource : rbacRoleResourceList) {
            RbacResource rbacResource = rbacResourceMapper.selectByPrimaryKey(rbacRoleResource.getResourceId());

            RbacResourcesVo resourcesVo = new RbacResourcesVo();
            resourcesVo.setResourceId(rbacResource.getId());
            resourcesVo.setResourceParentId(rbacResource.getParentId());
            resourcesVo.setResourceName(rbacResource.getResourceName());
            resourcesVo.setResourceUrl(rbacResource.getResourceUrl());
            resourcesVo.setChildren(Lists.newArrayList());
            resourcesVo.setResourceType(rbacResource.getResourceType());
            resourcesVo.setPerms(rbacResource.getPerms());
            resourcesVo.setSeq(rbacResource.getSeq());
            resourcesList.add(resourcesVo);
        }

        rbacUserVo.setRbacResourceList(resourcesList);

        return rbacUserVo;
    }

    @Override
    public RbacUserAndResetPasswordVo getAll() {
        RbacUserAndResetPasswordVo vo = new RbacUserAndResetPasswordVo();
        vo.setByAll(rbacRoleMapper.selectAll());
        vo.setResetPassword(resetPassword);

        return vo;
    }

    private RbacUser checkExist(Long id) {
        Example example = new Example(RbacUser.class);
        example.createCriteria().andEqualTo("id", id);
        RbacUser rbacUser = rbacUserMapper.selectOneByExample(example);

        if (rbacUser.getIsDelete() == 1) {
            throw new BusinessException("用户已不存在");
        }

        return rbacUser;
    }

    @Transactional
    @Override
    public void update(RbacUserAddParam param) {
//        ShiroUser shiroUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(param.getToken(), "userid"));
        RbacUser rbacUser = checkExist(param.getUserId());
        if (!param.getUsername().equals(rbacUser.getUsername())) {
            throw new BusinessException("用户名不允许修改");
        }

        // 校验手机号
        Example exampleByPhone = new Example(RbacUser.class);
        exampleByPhone.createCriteria()
            .andEqualTo("phone", param.getPhone())
            .andNotEqualTo("id", param.getUserId())
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        List<RbacUser> rbacUsers = rbacUserMapper.selectByExample(exampleByPhone);

        if (rbacUsers.size() > 0) {
            throw new BusinessException("当前手机号已被使用");
        }

        RbacUser user = RbacUser.builder().id(param.getUserId()).nickname(param.getNickname())
            .password(ShiroCrypto.encryptPassword(resetPassword)).phone(param.getPhone())
            .updateUser(userId).gmtModified(new Date()).build();

        rbacUserMapper.updateByPrimaryKeySelective(user);

        // 修改RbacRoleUser表
        Example exampleByUserId = new Example(RbacRoleUser.class);
        exampleByUserId.createCriteria().andEqualTo("userId", param.getUserId())
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        RbacRoleUser rbacRoleUser = rbacRoleUserMapper.selectOneByExample(exampleByUserId);

        if (rbacRoleUser != null) {
            //删除原有用户权限
            rbacRoleUser.setUpdateUser(userId);
            rbacRoleUser.setGmtModified(new Date());
            rbacRoleUser.setIsDelete(DataStatusEnum.ISDELETE.getCode());
            rbacRoleUserMapper.updateByPrimaryKeySelective(rbacRoleUser);
        }

        //添加用户权限
        if (!ValidationUtil.isEmpty(param.getRoleId())) {
            RbacRoleUser build =
                RbacRoleUser.builder().roleId(param.getRoleId()).userId(param.getUserId()).createUser(userId).build();
            rbacRoleUserMapper.insertSelective(build);
        }

        // 修改用户和部门关联表
        Example exampleByOrganization = new Example(RbacUserOrganization.class);
        exampleByOrganization.createCriteria().andEqualTo("userId", param.getUserId())
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        RbacUserOrganization rbacUserOrganization =
            rbacUserOrganizationMapper.selectOneByExample(exampleByOrganization);

        if (rbacUserOrganization != null) {
            //取消用户原有部门关联
            rbacUserOrganization.setUpdateUser(userId);
            rbacUserOrganization.setGmtModified(new Date());
            rbacUserOrganization.setIsDelete(DataStatusEnum.ISDELETE.getCode());
            rbacUserOrganizationMapper.updateByPrimaryKeySelective(rbacUserOrganization);
        }
        //添加用户和部门关联
        if (!ValidationUtil.isEmpty(param.getOrganizationId())) {
            RbacUserOrganization build =
                RbacUserOrganization.builder().organizationId(param.getOrganizationId()).userId(param.getUserId())
                    .createUser(userId).build();
            rbacUserOrganizationMapper.insertSelective(build);
        }
    }

    @Transactional
    @Override
    public void delete(Long loginUserId, Long id) {
        RbacUser byPrimaryKey = rbacUserMapper.selectByPrimaryKey(id);

        if ("管理员".equals(byPrimaryKey.getNickname())) {
            throw new BusinessException("管理员不能被删除");
        }

        // 创建删除 RbacRoleUser表删除条件RoleId
        Example exampleByRbacRoleUser = new Example(RbacRoleUser.class);
        exampleByRbacRoleUser.createCriteria().andEqualTo("userId", id);

        List<RbacRoleUser> roleUserList = rbacRoleUserMapper.selectByExample(exampleByRbacRoleUser);

        Byte isDelete = 1;

        // 创建删除RbacUserOrganization表条件UserId
        Example exampleByOrganizationUserId = new Example(RbacUserOrganization.class);
        exampleByOrganizationUserId.createCriteria().andEqualTo("userId", id)
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        List<RbacUserOrganization> rbacUserOrganizations = rbacUserOrganizationMapper
            .selectByExample(exampleByOrganizationUserId);
        for (RbacUserOrganization rbacUserOrganization : rbacUserOrganizations) {
            rbacUserOrganization.setIsDelete(isDelete);
            rbacUserOrganization.setUpdateUser(loginUserId);
            rbacUserOrganization.setGmtModified(new Date());
            rbacUserOrganizationMapper.updateByPrimaryKey(rbacUserOrganization);
        }

        try {
            RbacUser rbacUser = checkExist(id);
            // 修改者((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getId()
            rbacUser.setUpdateUser(loginUserId);
            rbacUser.setGmtModified(new Date());
            rbacUser.setIsDelete(isDelete);

            rbacUserMapper.updateByPrimaryKeySelective(rbacUser);

            // 修改RbacRoleUser表
            Example exampleByUserId = new Example(RbacRoleUser.class);
            exampleByUserId.createCriteria().andEqualTo("userId", id)
                .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
            List<RbacRoleUser> rbacRoleUsers = rbacRoleUserMapper.selectByExample(exampleByUserId);
            for (RbacRoleUser rbacRoleUser : rbacRoleUsers) {
                rbacRoleUser.setIsDelete(isDelete);
                rbacRoleUser.setUpdateUser(loginUserId);
                rbacRoleUser.setGmtModified(new Date());
                rbacRoleUserMapper.updateByPrimaryKey(rbacRoleUser);
            }

            // shiroSessionDAO.delete(rbacUser.getUsername());
        } catch (BusinessException ex) {
            log.error("Exception Occur On RbacUserService.delete() id:{}", id, ex);
        }
    }

    @Override
    public List<RbacUser> getByUserName(String userName) {
        // 创建条件查询、分组
        Example example = new Example(RbacUser.class);
        example.createCriteria().andEqualTo("username", userName).andEqualTo("isDelete", 0);
        example.orderBy("username");

        return rbacUserMapper.selectByExample(example);
    }

    /**
     * 修改用户状态
     *
     * @Author wangwz
     * @Date 2020/12/18
     * @Params @param userId:
     * @param userStatus:
     * @return void
     */
    @Override
    public void updateStatus(Long userId, Byte userStatus) {
        RbacUser rbacUser = RbacUser.builder().id(userId).userStatus(userStatus).build();
        rbacUserMapper.updateByPrimaryKeySelective(rbacUser);
    }

    @Transactional
    @Override
    public void resetPassword(Long id,String token) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(token, "userid"));
        RbacUser rbacUser = rbacUserMapper.selectByPrimaryKey(id);
        rbacUser.setPassword(ShiroCrypto.encryptPassword(resetPassword));
        // 修改者((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getId()
        rbacUser.setUpdateUser(userId);
        rbacUser.setGmtModified(new Date());

        rbacUserMapper.updateByPrimaryKeySelective(rbacUser);
    }

    /**
     * 根据Phone获取用户信息
     *
     * @Author wangwz
     * @Date 2021/5/25
     * @Params @param phone:
     * @return com.ihrdata.demo.model.RbacUser
     * @param userId
     */
    @Override
    public RbacUser getByUserId(String userId) {
        Example exampleByPhone = new Example(RbacUser.class);
        exampleByPhone.createCriteria().andEqualTo("id", Long.valueOf(userId))
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        return rbacUserMapper.selectOneByExample(exampleByPhone);
    }

    /**
     * 校验手机号
     *
     * @Author wangwz
     * @Date 2021/5/25
     * @Params @param phone:手机号
     * @return void
     */
    private void checkPhone(String phone) {
        // 校验手机号
        Example exampleByPhone = new Example(RbacUser.class);
        exampleByPhone.createCriteria()
            .andEqualTo("phone", phone)
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        List<RbacUser> rbacUsers = rbacUserMapper.selectByExample(exampleByPhone);

        if (rbacUsers.size() > 0) {
            throw new BusinessException("当前手机号已被使用");
        }
    }
}
