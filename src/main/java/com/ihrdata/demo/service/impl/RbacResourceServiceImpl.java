package com.ihrdata.demo.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.common.utils.TreeUtil;
import com.ihrdata.demo.dao.RbacResourceCustomMapper;
import com.ihrdata.demo.dao.RbacResourceMapper;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.service.RbacResourceService;
import com.ihrdata.demo.web.param.RbacResourceParam;
import com.ihrdata.wtool.common.enums.DataStatusEnum;
import com.ihrdata.wtool.common.exception.BusinessException;
import com.ihrdata.wtool.common.exception.ParamException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service("rbacResourceService")
public class RbacResourceServiceImpl implements RbacResourceService {
    @Autowired
    private RbacResourceMapper rbacResourceMapper;

    @Autowired
    private RbacResourceCustomMapper rbacResourceCustomMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<RbacResource> list() {
        List<RbacResource> rbacResources = rbacResourceMapper.selectAll();

        return rbacResources;
    }

    @Transactional
    @Override
    public void add(RbacResourceParam param) {
        ShiroUser shiroUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();

        checkResourceName(param.getParentId(), param.getResourceName(), param.getId());

        RbacResource rbacResource =
            RbacResource.builder().parentId(param.getParentId()).resourceName(param.getResourceName())
                .resourceType(param.getResourceType()).resourceUrl(param.getResourceUrl()).seq(param.getSeq()).build();
        rbacResource.setLevel(TreeUtil.calcLevel(param.getParentId(), getLevel(param.getParentId())));
        rbacResource.setCreateUser(shiroUser.getId());

        rbacResourceMapper.insertSelective(rbacResource);
    }

    private void checkResourceName(Long parentId, String resourceName, Long id) {
        Byte isDelete = 0;
        RbacResource rbacResource =
            RbacResource.builder().parentId(parentId).resourceName(resourceName).id(id).isDelete(isDelete).build();

        // 查询有效条数
        int selectCount = rbacResourceMapper.selectCount(rbacResource);
        if (selectCount != 0) {
            throw new ParamException("同一层级下存在相同的资源名称");
        }
    }

    private String getLevel(Long id) {
        RbacResource rbacResource = rbacResourceMapper.selectByPrimaryKey(id);

        if (rbacResource == null) {
            return null;
        }

        if (rbacResource.getIsDelete() == 1) {
            throw new BusinessException("上级资源已不存在");
        }

        return rbacResource.getLevel();
    }

    @Override
    public RbacResource get(Long id) {
        return checkExist(id);
    }

    private RbacResource checkExist(Long id) {
        RbacResource rbacResource = rbacResourceMapper.selectByPrimaryKey(id);

        if (rbacResource.getIsDelete() == 1) {
            throw new ParamException(rbacResource.getResourceName() + "资源已不存在");
        }

        return rbacResource;
    }

    @Override
    public void update(RbacResourceParam param) {
        ShiroUser shiroUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();

        checkResourceName(param.getParentId(), param.getResourceName(), param.getId());

        // 获取resourceBefore
        RbacResource resourceBefore = checkExist(param.getId());

        RbacResource resourceAfter =
            RbacResource.builder().id(param.getId()).parentId(param.getParentId()).resourceName(param.getResourceName())
                .resourceType(param.getResourceType()).resourceUrl(param.getResourceUrl()).seq(param.getSeq()).build();
        resourceAfter.setLevel(TreeUtil.calcLevel(param.getParentId(), getLevel(param.getParentId())));
        resourceAfter.setUpdateUser(shiroUser.getId());
        resourceAfter.setGmtModified(new Date());

        if (!resourceAfter.getLevel().equals(resourceBefore.getLevel())) {

            Example example = new Example(RbacResource.class);
            example.createCriteria().andLike("level",
                TreeUtil.calcLevel(resourceBefore.getId(), resourceBefore.getLevel()) + "%");
            List<RbacResource> resourceList = rbacResourceMapper.selectByExample(example);

            if (CollectionUtils.isNotEmpty(resourceList)) {
                for (RbacResource resource : resourceList) {
                    resource.setLevel(
                        resourceAfter.getLevel() + resource.getLevel().substring(resourceBefore.getLevel().length()));
                }

                // 修改权限
                rbacResourceCustomMapper.batchUpdateLevel(resourceList);
            }
        }

        rbacResourceMapper.updateByPrimaryKeySelective(resourceAfter);
    }

    @Override
    public void delete(String ids) {
        ShiroUser shiroUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();

        List<String> idList = Lists.newArrayList(ids.split(","));

        for (String id : idList) {
            RbacResource rbacResource = checkExist(Long.parseLong(id));

            // 创建查询条件、并返回有效条数
            Example example = new Example(RbacResource.class);
            example.createCriteria().andEqualTo("parentId", id).andEqualTo("isDelete", 0);
            int countByExample = rbacResourceMapper.selectCountByExample(example);

            if (countByExample > 0) {
                throw new BusinessException("待删除的" + rbacResource.getResourceName() + "资源下存在子资源");
            }

            rbacResource.setUpdateUser(shiroUser.getId());
            rbacResource.setGmtModified(new Date());
            Byte isDelete = 1;
            rbacResource.setIsDelete(isDelete);

            rbacResourceMapper.updateByPrimaryKeySelective(rbacResource);
        }
    }

    @Override
    public List<RbacResource> getByRoleId(Long roleId) {
        return rbacResourceCustomMapper.selectByRoleId(roleId);
    }

    /**
     * 修改资源名称
     *
     * @Author wangwz
     * @Date 2020/12/23
     * @Params @param id:
     * @param resourceName:
     * @return void
     */
    @Override
    public void updateResourceName(Long id, String resourceName) {
        // 校验资源名称是否重复
        Example exampleByResourceName = new Example(RbacResource.class);
        exampleByResourceName.createCriteria().andEqualTo("id", id)
            .andEqualTo("resourceName", resourceName)
            .andEqualTo("isDelete",
                DataStatusEnum.NOTDELETE.getCode());
        RbacResource resource = rbacResourceMapper.selectOneByExample(exampleByResourceName);
        if (null != resource) {
            throw new BusinessException("资源名称重复");
        }

        rbacResourceMapper
            .updateByPrimaryKeySelective(RbacResource.builder().id(id).resourceName(resourceName).build());
    }

    /**
     * 修改资源序号
     *
     * @Author wangwz
     * @Date 2020/12/23
     * @Params @param rbacResourceUpdatateSeqParam:
     * @return void
     */
    @Override
    public void updatateSeq(Map<Long, Integer> paramMap) {
        rbacResourceCustomMapper.updateSeqById(paramMap);
    }
}
