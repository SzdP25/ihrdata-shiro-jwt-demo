package com.ihrdata.demo.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacOrganizationCustomMapper {
    /**
     * 修改部门序号
     */
    void updateSeqById(@Param("id") Long id, @Param("seq") Integer seq);
}