package com.xjtushilei.imdemo.repository;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TableStoreRepository {
    @Value("${aliyun.tablestore.endpoint}")
    private String endpoint;
    @Value("${aliyun.tablestore.AccessKeyID}")
    private String accessId;
    @Value("${aliyun.tablestore.AccessKeySecret}")
    private String accessKey;
    @Value("${aliyun.tablestore.instanceName}")
    private String instanceName;


    public static SyncClient client;

    /**
     * 进行初始化，完成tablestore的clinet的连接
     */
    @PostConstruct
    private void init() {
        client = new SyncClient(this.endpoint, this.accessId, this.accessKey, this.instanceName);
    }

    public List<String> ListTable() {
        return client.listTable().getTableNames();
    }

    public void createTable(String tableName, List<PrimaryKeySchema> primaryKey) {
        TableMeta tableMeta = new TableMeta(tableName);
        tableMeta.addPrimaryKeyColumns(primaryKey);
        CreateTableRequest request = new CreateTableRequest(tableMeta, new TableOptions(-1, 1));
        client.createTable(request);
    }

    public DeleteTableResponse deleteTable(String tableName) {
        DeleteTableRequest request = new DeleteTableRequest(tableName);
        return client.deleteTable(request);
    }


    public void putRow(String tableName, Map<String, PrimaryKeyValue> pk, Map<String, ColumnValue> columns) {
        List<PrimaryKeyColumn> primaryKeyColumns = new ArrayList<>();
        pk.forEach((k, v) -> primaryKeyColumns.add(new PrimaryKeyColumn(k, v)));
        PrimaryKey primaryKey = new PrimaryKey(primaryKeyColumns);
        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        columns.forEach(rowPutChange::addColumn);
        PutRowRequest request = new PutRowRequest(rowPutChange);
        client.putRow(request);
    }


    public GetRowResponse getRow(String tableName, Map<String, PrimaryKeyValue> pk) {
        List<PrimaryKeyColumn> primaryKeyColumns = new ArrayList<>();
        pk.forEach((k, v) -> primaryKeyColumns.add(new PrimaryKeyColumn(k, v)));
        PrimaryKey primaryKey = new PrimaryKey(primaryKeyColumns);
        SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(tableName, primaryKey);
        singleRowQueryCriteria.setMaxVersions(1);
        GetRowRequest getRowRequest = new GetRowRequest(singleRowQueryCriteria);
        return client.getRow(getRowRequest);
    }

    public void deleteRow(String tableName, Map<String, PrimaryKeyValue> pk) {
        List<PrimaryKeyColumn> primaryKeyColumns = new ArrayList<>();
        pk.forEach((k, v) -> primaryKeyColumns.add(new PrimaryKeyColumn(k, v)));
        PrimaryKey primaryKey = new PrimaryKey(primaryKeyColumns);
        RowDeleteChange rowDeleteChange = new RowDeleteChange(tableName, primaryKey);
        client.deleteRow(new DeleteRowRequest(rowDeleteChange));
    }


    public List<Row> getRange(String tableName, List<PkV> startPkValues, List<PkV> endPkValues) {
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(tableName);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        for (PkV startPkValue : startPkValues) {
            primaryKeyBuilder.addPrimaryKeyColumn(startPkValue.pk, startPkValue.primaryKeyValue);
        }
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
        // 设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        for (PkV pkV : endPkValues) {
            primaryKeyBuilder.addPrimaryKeyColumn(pkV.pk, pkV.primaryKeyValue);
        }
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        rangeRowQueryCriteria.setMaxVersions(1);
        List<Row> result = new ArrayList<>();
        while (true) {
            GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            result.addAll(getRangeResponse.getRows());
            // 若nextStartPrimaryKey不为null, 则继续读取.
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
        return result;
    }

    public String toString() {
        return "TableStoreService{" +
                "endpoint='" + endpoint + '\'' +
                ", accessId='" + accessId + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", instanceName='" + instanceName + '\'' +
                '}';
    }
}

