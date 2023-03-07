package com.ihrdata.demo.service;

import java.util.List;
import java.util.Map;

import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.web.param.RbacResourceParam;

/**
 * 资源service
 *
 * @Author wangwz
 * @Date 2020/11/24
 */
public interface RbacResourceService {
    /**
     * 获取全部资源
     */
    List<RbacResource> list();

    /**
     * 添加资源
     */
    void add(RbacResourceParam param);

    /**
     * 根据Id获取资源
     */
    RbacResource get(Long id);

    /**
     * 修改资源
     */
    void update(RbacResourceParam param);

    /**
     * 删除资源
     */
    void delete(String ids);

    /**
     * 获取某一角色下全部资源
     */
    List<RbacResource> getByRoleId(Long roleId);

    /**
     * 修改资源名称
     */
    void updateResourceName(Long id, String resourceName);

    /**
     * 修改序号
     */
    void updatateSeq(Map<Long, Integer> paramMap);
}
