package com.opview.summary.dao;


import com.opview.summary.entity.ExecutionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogDao extends CrudRepository<ExecutionLog, Long> {

 // CrudRepository 提供了 save() 方法，可用於新增執行日誌
 // 這裡無需自定義額外的方法
}