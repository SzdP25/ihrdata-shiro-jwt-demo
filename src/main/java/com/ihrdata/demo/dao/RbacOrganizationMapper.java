package com.ihrdata.demo.dao;

import com.ihrdata.demo.model.RbacOrganization;
import com.ihrdata.demo.mybatis.TkMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacOrganizationMapper extends TkMapper<RbacOrganization> {
}