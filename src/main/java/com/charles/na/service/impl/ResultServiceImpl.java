package com.charles.na.service.impl;

import com.charles.na.service.IResultService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huqj
 */
@Service("resultService")
public class ResultServiceImpl implements IResultService {


    private Logger LOGGER = Logger.getLogger(ResultServiceImpl.class);

    private String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    public void convert2result() {

    }
}
