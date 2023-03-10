package com.ihrdata.demo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ihrdata.demo.common.jwt.JWTUtils;
import com.ihrdata.demo.dao.*;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.model.RbacRoleResource;
import com.ihrdata.demo.model.RbacRoleUser;
import com.ihrdata.demo.service.RbacRoleService;
import com.ihrdata.demo.web.param.RbacRoleAddParam;
import com.ihrdata.demo.web.param.RbacRolePagingParam;
import com.ihrdata.demo.web.vo.RbacRoleResourceVo;
import com.ihrdata.demo.web.vo.RbacRoleVo;
import com.ihrdata.wtool.common.enums.DataStatusEnum;
import com.ihrdata.wtool.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service("rbacRoleService")
public class RbacRoleServiceImpl implements RbacRoleService {

    @Autowired
    private RbacRoleMapper rbacRoleMapper;

    @Autowired
    private RbacRoleCustomMapper rbacRoleCustomMapper;

    @Autowired
    private RbacUserMapper rbacUserMapper;

    @Autowired
    private RbacUserCustomMapper rbacUserCustomMapper;

    @Autowired
    private RbacResourceMapper rbacResourceMapper;

    @Autowired
    private RbacResourceCustomMapper rbacResourceCustomMapper;

    @Autowired
    private RbacRoleUserMapper rbacRoleUserMapper;

    @Autowired
    private RbacRoleResourceMapper rbacRoleResourceMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageInfo<RbacRole> query(RbacRolePagingParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());

        // ??????????????????????????????????????????
        Example example = new Example(RbacRole.class);
        Criteria criteria = example.createCriteria().andEqualTo("isDelete", 0);
        if (StringUtils.isNotBlank(param.getRoleName())) {
            criteria.andLike("roleName", "%" + param.getRoleName() + "%");
        }
        example.orderBy("roleName").asc();
        List<RbacRole> selectByExample = rbacRoleMapper.selectByExample(example);

        return new PageInfo<RbacRole>(selectByExample);
    }

    @Transactional
    @Override
    public void add(RbacRoleAddParam param) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(param.getToken(), "userid"));

        // ??????????????????????????????
        checkRoleName(param.getRoleName(), null);

        RbacRole rbacRole = RbacRole.builder().roleName(param.getRoleName())
            .createUser(userId).build();
        // ?????????((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getId()

        rbacRoleMapper.insertSelective(rbacRole);

        // List<String> prems = param.getPrems();
        // // ?????????????????????HashSet??????????????????list????????????????????????
        // boolean isRepeat = prems.size() != new HashSet<String>(prems).size();
        // if (isRepeat) {
        //     throw new BusinessException("??????????????????");
        // }
        List<String> resourceIds = param.getResourceIds();
        for (String resourceId : resourceIds) {
            RbacRoleResource rbacRoleResource = RbacRoleResource.builder().roleId(rbacRole.getId())
                .resourceId(Long.parseLong(resourceId)).createUser(userId).build();
            rbacRoleResourceMapper.insertSelective(rbacRoleResource);
        }

        // shiroCache.removeAll();
    }

    private void checkRoleName(String roleName, Long id) {
        // ??????????????????roleName????????????????????????
        Example exampleByRoleName = new Example(RbacRole.class);
        exampleByRoleName.createCriteria().andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("roleName", roleName);
        int countByExample = rbacRoleMapper.selectCountByExample(exampleByRoleName);

        if (countByExample > 0) {
            throw new BusinessException("?????????????????????");
        }
    }

    private void roleUserResource(Long roleId, String userId, Long resourceId) {
        RbacRoleUser rbacRoleUser = RbacRoleUser.builder().roleId(roleId).userId(Long.parseLong(userId)).build();

        rbacRoleUserMapper.insertSelective(rbacRoleUser);

        RbacRoleResource rbacRoleResource = RbacRoleResource.builder().roleId(roleId).resourceId(resourceId).build();

        rbacRoleResourceMapper.insertSelective(rbacRoleResource);

    }

    @Override
    public RbacRoleVo get(Long id) {
        RbacRole rbacRole = checkExist(id);

        RbacRoleVo rbacRoleVo = RbacRoleVo.adapt(rbacRole);
        rbacRoleVo.setRbacUserList(rbacUserCustomMapper.selectByRoleId(id));
        rbacRoleVo.setRbacResourceList(rbacResourceCustomMapper.selectByRoleId(id));

        return rbacRoleVo;
    }

    private RbacRole checkExist(Long id) {
        RbacRole rbacRole = rbacRoleMapper.selectByPrimaryKey(id);

        if (rbacRole.getIsDelete() == 1) {
            throw new BusinessException("??????????????????");
        }

        return rbacRole;
    }

    private void checkRoleName(Long id, String roleName) {
        // ???????????? RbacRole???????????????roleName
        Example exampleByRbacRoleUser = new Example(RbacRole.class);
        exampleByRbacRoleUser.createCriteria().andEqualTo("roleName", roleName)
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());

        RbacRole rbacRole = rbacRoleMapper.selectOneByExample(exampleByRbacRoleUser);
        // ?????????????????????????????????
        if (rbacRole != null) {
            // ???????????????ID????????????
            if (!rbacRole.getId().equals(id)) {
                // Id?????????
                throw new BusinessException("??????????????????");
            }
        }
    }

    @Transactional
    @Override
    public void update(RbacRoleAddParam param) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(param.getToken(), "userid"));

        // ??????Role
        checkExist(Long.parseLong(param.getId()));
        checkRoleName(Long.parseLong(param.getId()), param.getRoleName());

        // ???????????? RbacRoleResource???????????????RoleId
        Example exampleByRbacRoleResource = new Example(RbacRoleResource.class);
        exampleByRbacRoleResource.createCriteria().andEqualTo("roleId", param.getId());
        List<RbacRoleResource> rbacRoleResources = rbacRoleResourceMapper.selectByExample(exampleByRbacRoleResource);
        for (RbacRoleResource rbacRoleResource : rbacRoleResources) {
            rbacRoleResource.setIsDelete(DataStatusEnum.ISDELETE.getCode());
            rbacRoleResource.setUpdateUser(userId);
            rbacRoleResource.setGmtModified(new Date());
            rbacRoleResourceMapper.updateByPrimaryKey(rbacRoleResource);
        }

        RbacRole rbacRole = RbacRole.builder().id(Long.parseLong(param.getId())).roleName(param.getRoleName())
            .updateUser(userId)
            .gmtModified(new Date()).build();
        // ?????????((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getId()

        rbacRoleMapper.updateByPrimaryKeySelective(rbacRole);

        List<String> resourceIds = param.getResourceIds();
        for (String resourceId : resourceIds) {
            RbacRoleResource rbacRoleResource = RbacRoleResource.builder().roleId(Long.parseLong(param.getId()))
                .resourceId(Long.parseLong(resourceId)).createUser(userId).build();
            rbacRoleResourceMapper.insertSelective(rbacRoleResource);
        }
    }

    @Transactional
    @Override
    public void delete(Long id, String token) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(token, "userid"));
        // ???????????? RbacRoleUser???????????????roleId
        Example exampleByRbacRoleUser = new Example(RbacRoleUser.class);
        exampleByRbacRoleUser.createCriteria().andEqualTo("roleId", id);

        // ???????????? RbacRoleResource???????????????RoleId
        Example exampleByRbacRoleResource = new Example(RbacRoleResource.class);
        exampleByRbacRoleResource.createCriteria().andEqualTo("roleId", id);

        List<RbacRoleUser> roleUserList = rbacRoleUserMapper.selectByExample(exampleByRbacRoleUser);

        if (roleUserList.size() > 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        List<RbacRoleResource> rbacRoleResources = rbacRoleResourceMapper.selectByExample(exampleByRbacRoleResource);
        for (RbacRoleResource rbacRoleResource : rbacRoleResources) {
            rbacRoleResource.setIsDelete(DataStatusEnum.ISDELETE.getCode());
            rbacRoleResource.setUpdateUser(userId);
            rbacRoleResource.setGmtModified(new Date());
            rbacRoleResourceMapper.updateByPrimaryKey(rbacRoleResource);
        }
        RbacRole rbacRole = rbacRoleMapper.selectByPrimaryKey(id);
        rbacRole.setIsDelete(DataStatusEnum.ISDELETE.getCode());
        rbacRole.setUpdateUser(userId);
        rbacRole.setGmtModified(new Date());
        rbacRoleMapper.updateByPrimaryKey(rbacRole);
    }

    @Override
    public List<RbacRole> getByUserId(Long userId) {
        return rbacRoleCustomMapper.selectByUserId(userId);
    }

    // ??????????????????
    @Override
    public PageInfo<RbacRoleResourceVo> queryAll(RbacRolePagingParam param) {
        // ??????????????????(??????ID)
        Example example = new Example(RbacRole.class);
        example.createCriteria().andEqualTo("isDelete", 0);

        // ????????????
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<RbacRole> rbacRoles = rbacRoleCustomMapper.selectAllRole(param);

        // List<RbacRole> rbacRoleAll = rbacRoleMapper.selectByExample(example);
        PageInfo<RbacRole> pInfo = new PageInfo<RbacRole>(rbacRoles);

        // ??????RbacRoleResourceVo??????
        ArrayList<RbacRoleResourceVo> roleResourceVoList = new ArrayList<>();

        for (RbacRole rbacRole : rbacRoles) {
            // ??????????????????(roleID)
            Example exampleRoleResource = new Example(RbacRoleResource.class);
            exampleRoleResource.createCriteria().andEqualTo("roleId", rbacRole.getId())
                .andEqualTo("isDelete",DataStatusEnum.NOTDELETE);
            // ???????????????????????????????????????ID
            List<RbacRoleResource> rbacRoleResourceByExample =
                rbacRoleResourceMapper.selectByExample(exampleRoleResource);

            // ??????RbacRoleResource??????
            ArrayList<RbacResource> resourceList = new ArrayList<>();

            // ???????????????????????????
            for (RbacRoleResource roleResource : rbacRoleResourceByExample) {
                // ?????????????????????ResourceId,?????????RbacResource
                RbacResource selectByPrimaryKey = rbacResourceMapper.selectByPrimaryKey(roleResource.getResourceId());
                resourceList.add(selectByPrimaryKey);
            }

            // ??????RbacRoleResourceVo???????????????roleName
            RbacRoleResourceVo roleResourceVo = new RbacRoleResourceVo();
            roleResourceVo.setId(rbacRole.getId());
            roleResourceVo.setSeq(rbacRole.getSeq());
            roleResourceVo.setRoleName(rbacRole.getRoleName());
            roleResourceVo.setResources(resourceList);

            roleResourceVoList.add(roleResourceVo);
        }

        PageInfo<RbacRoleResourceVo> pageInfo = new PageInfo<RbacRoleResourceVo>(roleResourceVoList);
        pageInfo.setTotal(pInfo.getTotal());
        pageInfo.setPages(pInfo.getPages());

        return pageInfo;
    }

    @Override
    public RbacRole getByRoleId(Long roleId) {
        return rbacRoleMapper.selectByPrimaryKey(roleId);
    }

}
