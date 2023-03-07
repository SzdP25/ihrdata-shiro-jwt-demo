package com.ihrdata.demo.dao;

import java.util.List;
import java.util.Map;

import com.ihrdata.demo.model.RbacResource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacResourceCustomMapper {

    void batchUpdateLevel(@Param("resourceList") List<RbacResource> resourceList);

    List<RbacResource> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 修改层级
     */
    void updateSeqById(@Param("map") Map<Long, Integer> paramMap);
}
