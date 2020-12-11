package com.kim.community.Dao.impl;

import com.kim.community.Dao.AlphaDao;
import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoimpl implements AlphaDao {
    @Override
    public String select() {
        return "执行AlphaDao里的select方法";
    }
}
