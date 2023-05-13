package com.star.aries.auth.server.service;

import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.exception.CommonRuntimeException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

public class BaseService {
    @Autowired
    TransactionTemplate transactionTemplate;

    public <T> T transactionExecute(DbAction<T> dbAction, String exceptionMsg) {
        return transactionTemplate.execute((TransactionStatus transactionStatus) -> {
            try {
                return dbAction.doInvoke();
            } catch (Throwable e) {
                transactionStatus.setRollbackOnly();
                throw new CommonRuntimeException(StatusType.DB_ERROR, exceptionMsg);
            }
        });
    }

    @FunctionalInterface
    public interface DbAction<T> {
        T doInvoke();
    }

    public String formatDate(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("yyyy-MM-dd HH:mm:ss");
    }

}
