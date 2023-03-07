package com.ihrdata.demo.service.impl;

import java.util.*;

import com.google.common.collect.Lists;
import com.ihrdata.demo.dao.RbacOrganizationCustomMapper;
import com.ihrdata.demo.dao.RbacOrganizationMapper;
import com.ihrdata.demo.dao.RbacUserOrganizationMapper;
import com.ihrdata.demo.model.RbacOrganization;
import com.ihrdata.demo.model.RbacUserOrganization;
import com.ihrdata.demo.service.RbacOrganizationService;
import com.ihrdata.demo.web.param.RbacOrganizationParam;
import com.ihrdata.demo.web.vo.RbacOrganizationVo;
import com.ihrdata.wtool.common.enums.DataStatusEnum;
import com.ihrdata.wtool.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service("rbacOrganizationService")
public class RbacOrganizationServiceImpl implements RbacOrganizationService {
    @Autowired
    private RbacOrganizationMapper rbacOrganizationMapper;
    @Autowired
    private RbacOrganizationCustomMapper rbacOrganizationCustomMapper;
    @Autowired
    private RbacUserOrganizationMapper rbacUserOrganizationMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取全部部门
     */
    @Override
    public List<RbacOrganizationVo> queryAll() {
        // 查询全部部门
        Example exampleForDelete = new Example(RbacOrganization.class);
        exampleForDelete.createCriteria().andEqualTo("isDeleted", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("parentId", 0);
        List<RbacOrganization> selectAll = rbacOrganizationMapper.selectByExample(exampleForDelete);

        Collections.sort(selectAll);

        List<RbacOrganizationVo> vos = new ArrayList<RbacOrganizationVo>();

        // 递归添加子部门
        for (RbacOrganization rbacOrganization : selectAll) {
            vos.add(getChildren(selectAll, rbacOrganization));
        }

        return vos;
    }

    /**
     * 添加部门
     */
    @Transactional
    @Override
    public void add(RbacOrganizationParam rbacOrganizationParam) {
        // 判断同级下是否存在重名
        isExists(rbacOrganizationParam);

        // 添加部门业务
        RbacOrganization rbacOrganization = new RbacOrganization();
        BeanUtils.copyProperties(rbacOrganizationParam, rbacOrganization);

        rbacOrganizationMapper.insertSelective(rbacOrganization);

        // 修改后置部门序号
        rbacOrganizationCustomMapper.updateSeqById(rbacOrganization.getId(), rbacOrganizationParam.getSeq());
    }

    /**
     * 修改部门名称
     */
    @Transactional
    @Override
    public void updateName(RbacOrganizationParam rbacOrganizationParam) {
        // 判断同级下是否存在重名
        isExists(rbacOrganizationParam);

        // 修改部门名称业务
        RbacOrganization rbacOrganization = RbacOrganization.builder().id(rbacOrganizationParam.getId())
            .organizationName(rbacOrganizationParam.getOrganizationName()).build();
        rbacOrganizationMapper.updateByPrimaryKeySelective(rbacOrganization);
    }

    /**
     * 删除部门
     */
    @Transactional
    @Override
    public void delete(Long id) {
        // 判断当前部门下是否存在用户
        Example exampleForUserOrganization = new Example(RbacUserOrganization.class);
        exampleForUserOrganization.createCriteria().andEqualTo("organizationId", id);

        List<RbacUserOrganization> selectUserOrganization = rbacUserOrganizationMapper
            .selectByExample(exampleForUserOrganization);
        if (selectUserOrganization.size() > 0) {
            throw new BusinessException("当前部门下尚有用户，不可删除！");
        }

        // 删除部门业务
        Example exampleForId = new Example(RbacOrganization.class);
        exampleForId.createCriteria().andEqualTo("id", id);
        RbacOrganization rbacOrganization = RbacOrganization.builder().id(id)
            .isDeleted(DataStatusEnum.ISDELETE.getCode()).build();
        rbacOrganizationMapper.updateByExampleSelective(rbacOrganization, exampleForId);
    }

    /**
     * 获取用户所在部门
     *
     * @param userId
     * @return
     */
    @Override
    public RbacUserOrganization getOrganizationByUser(Long userId) {
        Example example = new Example(RbacUserOrganization.class);
        example.createCriteria().andEqualTo("userId", userId);

        RbacUserOrganization rbacUserOrganization = rbacUserOrganizationMapper.selectOneByExample(example);
        return rbacUserOrganization;
    }

    /**
     * 获取用户所在部门详细信息
     *
     * @param userId 用户Id
     * @return RbacOrganization 机构实体
     */
    @Override
    public RbacOrganization getOrganizationModelByUser(Long userId) {
        log.info("[rcgy] [class] RbacOrganizationService [function] getOrganizationModelByUser");
        // 查询机构大区
        RbacUserOrganization ruo = getOrganizationByUser(userId);
        if (null == ruo || null == ruo.getOrganizationId()) {
            throw new BusinessException("该用户没有所属部门！");
        }
        // 获取该机构所属大区编码
        return rbacOrganizationMapper.selectByPrimaryKey(ruo.getOrganizationId());
    }

    /**
     * 根据ID集合查询部门机构
     *
     * @Author wangwz
     * @Date 2020/12/24
     * @Params @param ids:
     * @return java.util.Map<java.lang.Long   com.ihrdata.demo.model.RbacOrganization>
     */
    @Override
    public Map<Long, RbacOrganization> selectOrganizationByIds(List<Long> ids) {
        Map<Long, RbacOrganization> result = new HashMap<>(ids.size());
        Example example = new Example(RbacOrganization.class);
        example.createCriteria().andIn("id", ids).andEqualTo("isDeleted", DataStatusEnum.NOTDELETE.getCode());
        List<RbacOrganization> roList = rbacOrganizationMapper.selectByExample(example);
        roList.forEach(ro -> result.put(ro.getId(), ro));
        return result;
    }

    /***************************** 私有方法 ****************************************/
    /**
     * 获取当前部门下全部子部门
     *
     * @param rbacOrganizations
     * @param rbacOrganization
     * @return
     */
    private RbacOrganizationVo getChildren(List<RbacOrganization> rbacOrganizations,
        RbacOrganization rbacOrganization) {
        RbacOrganizationVo organizationVo = RbacOrganizationVo.builder()
            .organizationCode(rbacOrganization.getOrganizationCode())
            .organizationArea(rbacOrganization.getOrganizationArea())
            .seq(rbacOrganization.getSeq())
            .organizationName(rbacOrganization.getOrganizationName()).organizationId(rbacOrganization.getId())
            .parentId(rbacOrganization.getParentId()).build();

        // 查询当前部门下子部门
        Example exampleForDelete = new Example(RbacOrganization.class);
        exampleForDelete.createCriteria().andEqualTo("isDeleted", DataStatusEnum.NOTDELETE.getCode())
            .andEqualTo("parentId", rbacOrganization.getId());
        List<RbacOrganization> children = rbacOrganizationMapper.selectByExample(exampleForDelete);

        Collections.sort(children);

        if (children.size() > 0) {
            ArrayList<RbacOrganizationVo> childrenVo = Lists.newArrayList();
            for (RbacOrganization organization : children) {
                childrenVo.add(getChildren(children, organization));
            }
            organizationVo.setChildren(childrenVo);
        } else {
            organizationVo.setChildren(null);
        }
        return organizationVo;
    }

    /**
     * 判断同级下是否存在重名
     *
     * @param rbacOrganizationParam
     */
    private void isExists(RbacOrganizationParam rbacOrganizationParam) {
        // 查询父级部门下是否存在重名部门
        Example exampleForName = new Example(RbacOrganization.class);
        exampleForName.createCriteria().andEqualTo("organizationName", rbacOrganizationParam.getOrganizationName())
            .andEqualTo("parentId", rbacOrganizationParam.getParentId())
            .andEqualTo("isDeleted", DataStatusEnum.NOTDELETE.getCode());

        List<RbacOrganization> rbacOrganizationList = rbacOrganizationMapper.selectByExample(exampleForName);

        if (rbacOrganizationList.size() > 0) {
            throw new BusinessException("当前部门名称已存在");
        }
    }
}
