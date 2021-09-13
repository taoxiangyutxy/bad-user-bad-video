package com.ttt.one.waiguagg;

import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.InfoService;
import com.ttt.one.waiguagg.service.UnmberService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class OneWaiguaggApplicationTests {
    @Autowired
    UnmberService service;

    @Autowired
    InfoService infoService;
    @Test
    void contextLoads() {

        UnmberEntity unmberEntity = new UnmberEntity();
        unmberEntity.setWaiguaUsername("rr221");
        service.save(unmberEntity);

    }

}
