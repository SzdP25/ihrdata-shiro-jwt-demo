package com.ihrdata.demo.dao;

import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.mybatis.TkMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacUserMapper extends TkMapper<RbacUser> {
}