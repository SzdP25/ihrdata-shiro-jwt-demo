package com.ihrdata.demo.dao;

import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.mybatis.TkMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacResourceMapper extends TkMapper<RbacResource> {
}