package com.ihrdata.demo.service;

import java.util.List;
import java.util.Map;

import com.ihrdata.demo.model.RbacOrganization;
import com.ihrdata.demo.model.RbacUserOrganization;
import com.ihrdata.demo.web.param.RbacOrganizationParam;
import com.ihrdata.demo.web.vo.RbacOrganizationVo;

public interface RbacOrganizationService {
    /**
     * 获取全部部门
     *
     * @return
     */
    List<RbacOrganizationVo> queryAll();

    /**
     * 添加部门
     *
     * @param rbacOrganizationParam
     */
    void add(RbacOrganizationParam rbacOrganizationParam);

    /**
     * 修改部门名称
     *
     * @param rbacOrganizationParam
     */
    void updateName(RbacOrganizationParam rbacOrganizationParam);

    /**
     * 删除部门
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 获取用户所在部门
     *
     * @param userId
     * @return
     */
    RbacUserOrganization getOrganizationByUser(Long userId);

    /**
     * 获取用户所在部门详细信息
     *
     * @param userId 用户Id
     * @return RbacOrganization 机构实体
     */
    RbacOrganization getOrganizationModelByUser(Long userId);

    /**
     * 根据ID集合查询部门机构
     *
     * @param ids id集合
     * @return Map<Long                                                               ,                                                                                                                               RbacOrganization> key:ID value:部门实体
     */
    Map<Long, RbacOrganization> selectOrganizationByIds(List<Long> ids);
}
