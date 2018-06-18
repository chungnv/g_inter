package com.viettel.ginterconnect.util;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.*;
import com.aerospike.client.task.ExecuteTask;
import com.aerospike.client.task.RegisterTask;
import com.viettel.ginterconnect.cached.bean.BtsCode;
import com.viettel.ginterconnect.cached.bean.VtcBtsAddress;
import com.viettel.ginterconnect.cached.bean.VtcHome;
import com.viettel.ginterconnect.process.bean.*;
import java.io.FileWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hoangsinh on 25/07/2017.
 */
public class GIClient<T> {

    private AerospikeClient client;
    private String nameSpace;

    private static Map<String, GIClient> mapCLient = new HashMap<>();

    //    public Client(String host, int port) {
//        initClient(host, port);
//    }
//    static {
//        AerospikeClient client = GIClient.getInstance().getClient();
//
//        RegisterTask task = client.register(null, "../udf/GI_LUA_COMMON.lua", "GI_LUA_COMMON.lua", Language.LUA);
//        task.waitTillComplete();
//        RegisterTask task2 = client.register(null, "../udf/CAL_REVENUE_ADDITION.lua", "CAL_REVENUE_ADDITION.lua", Language.LUA);
//        task2.waitTillComplete();
//    }
    public static GIClient getInstance() {
        GIClient instance = null;
        if (!mapCLient.containsKey("default")) {
            instance = new GIClient();
            instance.setNameSpace(SystemParam.NAMESPACE);
            ClientPolicy policy = new ClientPolicy();
            policy.user = SystemParam.AEROSPIKE_USERNAME;
            policy.password = SystemParam.AEROSPIKE_PASSWORD;
            String s[] = SystemParam.AEROSPIKE_IP.split(",");
            Host[] aeroHost = new Host[s.length];
            for (int i = 0; i < s.length; i++) {
                Host host = new Host(s[i], SystemParam.AEROSPIKE_PORT);
                aeroHost[i] = host;
            }
            AerospikeClient aeroClient = new AerospikeClient(policy, aeroHost);
//            instance.setClient(new AerospikeClient(policy, SystemParam.getAerospikeIp(), SystemParam.getAerospikePort()));
            instance.setClient(aeroClient);
            mapCLient.put("default", instance);
        } else {
            instance = mapCLient.get("default");
        }
        return instance;
    }

    public static GIClient getInstance(String dataBaseName) {
        if (dataBaseName != null && !dataBaseName.isEmpty()) {
            return mapCLient.get(dataBaseName);
        } else {
            return mapCLient.get("default");
        }

    }

    public static GIClient getInstance(ImportTableBO config) {
        GIClient instance = null;
        if (!mapCLient.containsKey(config.getDatabaseName())) {
            instance = new GIClient();
            ClientPolicy policy = new ClientPolicy();
            policy.user = config.getUserName() == null ? "" : config.getUserName();
            policy.password = config.getPassword() == null ? "" : config.getPassword();
            String s[] = config.getUrl().split(",");
            Host[] aeroHost = new Host[s.length];
            for (int i = 0; i < s.length; i++) {
                String[] aeroConfig = s[i].split(":");
                Host host = new Host(aeroConfig[0], Integer.valueOf(aeroConfig[1]));
                aeroHost[i] = host;
            }
            AerospikeClient aeroClient = new AerospikeClient(policy, aeroHost);
//            String aeroConfig[] = config.getUrl().split(":");
//            instance.setClient(new AerospikeClient(policy, aeroConfig[0], Integer.parseInt(aeroConfig[1])));
            instance.setClient(aeroClient);
            mapCLient.put(config.getDatabaseName(), instance);
        } else {
            instance = mapCLient.get(config.getDatabaseName());
        }
        return instance;
    }

    public void setClient(AerospikeClient client) {
        this.client = client;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public synchronized void initClient(String host, int port) {
        if (client == null) {
            ClientPolicy policy = new ClientPolicy();
            policy.user = "";
            policy.password = "";
            client = new AerospikeClient(policy, host, port);
        }
    }

    public void insertObject(Object object) {
        String table = "";
        for (Annotation annotation : object.getClass().getDeclaredAnnotations()) {
            if (annotation instanceof Table) {
                Table annoTable = (Table) annotation;
                table = annoTable.name();
                break;
            }
        }
        List<Bin> lstBin = new ArrayList<>();
        Object primaryKey = null;
        boolean keyLong = false;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Column) {
                    Column col = (Column) annotation;
                    Object val = null;
                    try {
                        val = field.get(object);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (col.primaryKey()) {
                        if (val instanceof Long) {
                            primaryKey = (Long) val;
                            keyLong = true;
                        } else if (val instanceof String) {
                            primaryKey = val.toString();
                        }
                    }
                    Bin bin = new Bin(col.name(), val);
                    lstBin.add(bin);
                }
            }

        }
        client.writePolicyDefault.sendKey = true;
        Key key = null;
        if (keyLong == true) {
            key = new Key(this.nameSpace, table, (Long) primaryKey);
        } else {
            key = new Key(this.nameSpace, table, primaryKey.toString());
        }
        client.put(client.getWritePolicyDefault(), key, lstBin.toArray(new Bin[lstBin.size()]));
    }

    public boolean deleteObject(Object object) {
        String table = "";
        for (Annotation annotation : object.getClass().getDeclaredAnnotations()) {
            if (annotation instanceof Table) {
                Table annoTable = (Table) annotation;
                table = annoTable.name();
                break;
            }
        }
        Long primaryKey = -1L;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Column) {
                    Column col = (Column) annotation;
                    Object val = null;
                    try {
                        val = field.get(object);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (col.primaryKey()) {
                        primaryKey = (Long) val;
                        break;
                    }
                }
            }
        }
        Key key = new Key(this.nameSpace, table, primaryKey);
        client.delete(null, key);
        return true;
    }

    public void insertToSet(String shema, String table, Bin[] bins) {
        client.writePolicyDefault.sendKey = true;
        Key key = new Key(shema, table, UUID.randomUUID().toString());
        client.put(client.getWritePolicyDefault(), key, bins);
    }

    public void insertToSet(String shema, String table, String keyString, Bin[] bins) {
        client.writePolicyDefault.sendKey = true;
        Key key = new Key(shema, table, keyString);
        client.put(client.getWritePolicyDefault(), key, bins);
    }

    public List<T> getRecordByFilter(Class<T> clazz, Filter filter, PredExp... preExp) {
        List<T> lst = new ArrayList<>();

        try {
            String table = "";
            for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                if (annotation instanceof Table) {
                    Table annoTable = (Table) annotation;
                    table = annoTable.name();
                    break;
                }
            }
            Statement stmt = new Statement();
            stmt.setNamespace(this.nameSpace);
            stmt.setSetName(table);
            stmt.setFilter(filter);
            if (preExp != null && preExp.length > 0) {
                stmt.setPredExp(preExp);
            }

            RecordSet rs = client.query(client.queryPolicyDefault, stmt);

            while (rs.next()) {
                T bo = clazz.newInstance();
                Record record = rs.getRecord();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        if (annotation instanceof Column) {
                            Column col = (Column) annotation;
                            try {
                                field.set(bo, record.getValue(col.name()) != null && !record.getValue(col.name()).toString().isEmpty() ? toObject(field.getType(), record.getValue(col.name()).toString()) : null);
                            } catch (Exception ex) {
                                System.out.println("Name: " + col.name() + " record value: " + record.getValue(col.name()));
                                throw ex;
                            }
                            break;
                        }
                    }
                }
                lst.add(bo);
            }

        } catch (Exception ex) {
            System.out.println("Class: " + clazz.getName() + " .Filter : " + filter.toString());
            ex.printStackTrace();
        }
        return lst;
    }

    public List<BtsCode> getListBtsCode(Filter filter) {
        List<BtsCode> lstHome = new ArrayList<>();

        Statement stmt = new Statement();
        stmt.setNamespace(this.nameSpace);
        stmt.setSetName("CMR_BTS");
        stmt.setFilter(filter);
        RecordSet rs = client.query(client.queryPolicyDefault, stmt);
        while (rs.next()) {
            long start1 = System.currentTimeMillis();
            BtsCode home = new BtsCode();
            Record record = rs.getRecord();
            home.setCellName(record.getValue("CELL_NAME").toString());
            home.setBtsCode(record.getValue("BTS_CODE").toString());
            home.setCenterCode(record.getValue("CENTER_CODE").toString());
            home.setProvinceCode(record.getValue("PROVINCE_CODE").toString());
            lstHome.add(home);
            long end1 = System.currentTimeMillis();
            System.out.println("Query time: " + (end1 - start1));
//            break;
        }

        return lstHome;
    }

    public VtcHome getListVtcHome(Filter filter) {
//        List<VtcHome> lstHome = new ArrayList<VtcHome>();

        Statement stmt = new Statement();
        stmt.setNamespace(this.nameSpace);
        stmt.setSetName("VTC_ISDN_HOME");
        stmt.setFilter(filter);
        RecordSet rs = client.query(client.queryPolicyDefault, stmt);
        while (rs.next()) {
            VtcHome home = new VtcHome();
            Record record = rs.getRecord();
            home.setIsdn(record.getValue("ISDN").toString());
            home.setBtsCode(record.getValue("BTS_CODE").toString());
            home.setCenterCode(record.getValue("CENTER_CODE").toString());
            home.setProvinceCode(record.getValue("PROVINCE_CODE").toString());
//            home.setMarket(record.getValue("MARKET").toString());
            home.setPartition(record.getValue("PARTITION").toString());
//            lstHome.add(home);
            return home;
        }
        return null;
    }

    public List<VtcHome> getListHome(String setName, Filter filter) {
        List<VtcHome> lstHome = new ArrayList<>();

        Statement stmt = new Statement();
        stmt.setNamespace(this.nameSpace);
        stmt.setSetName(setName);
        stmt.setFilter(filter);
        RecordSet rs = client.query(client.queryPolicyDefault, stmt);
        while (rs.next()) {
            VtcHome home = new VtcHome();
            Record record = rs.getRecord();
            home.setIsdn(record.getValue("ISDN").toString());
            if (record.getValue("BTS_CODE") != null) {
                home.setBtsCode(record.getValue("BTS_CODE").toString());
            }
            if (record.getValue("CENTER_CODE") != null) {
                home.setCenterCode(record.getValue("CENTER_CODE").toString());
            }
            if (record.getValue("BUS_CENTER_CO") != null) {
                home.setCenterCode(record.getValue("BUS_CENTER_CO").toString());
            }
//            if (record.getValue("PROVINCE_CODE") != null) {
//                home.setProvinceCode(record.getValue("PROVINCE_CODE").toString());
//            }
            if (record.getValue("PROVINCE") != null) {
                home.setProvinceCode(record.getValue("PROVINCE").toString());
            }
            if (record.getValue("MARKET") != null) {
                home.setMarket(record.getValue("MARKET").toString());
            }
            if (record.getValue("PARTITION") != null) {
                home.setPartition(record.getValue("PARTITION").toString());
            }
            lstHome.add(home);
        }
        return lstHome;
    }

    public VtcBtsAddress getBtsAddress(String setName, Filter filter) {
//        List<VtcHome> lstHome = new ArrayList<VtcHome>();

        Statement stmt = new Statement();
        stmt.setNamespace(this.nameSpace);
        stmt.setSetName(setName);
        stmt.setFilter(filter);
        RecordSet rs = client.query(client.queryPolicyDefault, stmt);
        while (rs.next()) {
            VtcBtsAddress bts = new VtcBtsAddress();
            Record record = rs.getRecord();
//            home.setIsdn(record.getValue("ISDN").toString());
            if (record.getValue("BTS_CODE") != null) {
                bts.setBtsCode(record.getValue("BTS_CODE").toString());
            }
            if (record.getValue("CENTER_CODE") != null) {
                bts.setCenterCode(record.getValue("CENTER_CODE").toString());
            }
            if (record.getValue("BUS_CENTER_CO") != null) {
                bts.setCenterCode(record.getValue("BUS_CENTER_CO").toString());
            }
            if (record.getValue("PROVINCE_CODE") != null) {
                bts.setProvinceCode(record.getValue("PROVINCE_CODE").toString());
            }
            if (record.getValue("BTS_TYPE") != null) {
                bts.setBtsType(record.getValue("BTS_TYPE").toString());
            }
            if (record.getValue("ARE_TYPE") != null) {
                bts.setAreaType(record.getValue("ARE_TYPE").toString());
            }
//            home.setMarket(record.getValue("MARKET").toString());
//            home.setPartition(record.getValue("PARTITION").toString());
//            lstHome.add(home);
            return bts;
        }
        return null;
//        return lstHome;
    }

    public static Value toAeroValue(Class clazz, String value) {
        if (Boolean.class == clazz || Boolean.TYPE == clazz) {
            return new Value.BooleanValue(Boolean.parseBoolean(value));
        }
        if (Byte.class == clazz || Byte.TYPE == clazz) {
            return new Value.ByteValue(Byte.parseByte(value));
        }
        if (Short.class == clazz || Short.TYPE == clazz) {
            return new Value.IntegerValue(Short.parseShort(value));
        }
        if (Integer.class == clazz || Integer.TYPE == clazz) {
            return new Value.IntegerValue(Integer.parseInt(value));
        }
        if (Long.class == clazz || Long.TYPE == clazz) {
            return new Value.LongValue(Long.parseLong(value));
        }
        if (Float.class == clazz || Float.TYPE == clazz) {
            return new Value.FloatValue(Float.parseFloat(value));
        }
        if (Double.class == clazz || Double.TYPE == clazz) {
            return new Value.DoubleValue(Double.parseDouble(value));
        }
        return new Value.StringValue(value);
    }

    public static Object toObject(Class clazz, String value) {
        if (value != null && value.toUpperCase().equals("NULL")) {
            return null;
        }
        if (Boolean.class == clazz || Boolean.TYPE == clazz) {
            return Boolean.parseBoolean(value);
        }
        if (Byte.class == clazz || Byte.TYPE == clazz) {
            return Byte.parseByte(value);
        }
        if (Short.class == clazz || Short.TYPE == clazz) {
            return Short.parseShort(value);
        }
        if (Integer.class == clazz || Integer.TYPE == clazz) {
            return Integer.parseInt(value);
        }
        if (Long.class == clazz || Long.TYPE == clazz) {
            return Long.parseLong(value);
        }
        if (Float.class == clazz || Float.TYPE == clazz) {
            return Float.parseFloat(value);
        }
        if (Double.class == clazz || Double.TYPE == clazz) {
            return Double.parseDouble(value);
        }
        return value;
    }

    public void deleteSet(Class clazz, final Key key) {
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = false;
        String table = "";
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation instanceof Table) {
                Table annoTable = (Table) annotation;
                table = annoTable.name();
                break;
            }
        }

        client.scanAll(scanPolicy, this.nameSpace, table, new ScanCallback() {

            public void scanCallback(Key key, Record record) throws AerospikeException {
                client.delete(null, key);
//                if (key.equals(key)) {
//                    client.delete(null, key);
//                }
            }
        }, new String[]{});
    }

    public void deleteSet(String table, String schema) {
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = false;
        client.getWritePolicyDefault().durableDelete = true;
        final int[] count = {0};

        client.scanAll(scanPolicy, schema, table, new ScanCallback() {

            public void scanCallback(Key key, Record record) throws AerospikeException {

                if (client.delete(null, key)) {
                    count[0]++;
                }
                if (count[0] % 25000 == 0) {
                    System.out.println("Deleted " + count[0]);
                }
            }
        }, new String[]{});
        System.out.println("Total deleted " + count[0]);
    }

    public Switchboard getSwitchboard(String id) {
        try {
            List<Switchboard> lst = GIClient.getInstance().getRecordByFilter(Switchboard.class, Filter.equal("ID", id));
            if (lst == null || lst.isEmpty()) {
                return null;
            }
            for (Switchboard sw : lst) {
                if (sw.getId().equals(id)) {
                    return sw;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public MapConvert getMapConvert(String type, String country) {
        List<MapConvert> lstMapConvert = GIClient.getInstance().getRecordByFilter(MapConvert.class, Filter.equal("SWITCH_TYPE", type));
        for (MapConvert mapConvert : lstMapConvert) {
            if (mapConvert.getSwitchType() != null && mapConvert.getCountry() != null) {
                if (mapConvert.getSwitchType().toUpperCase().equals(type.toUpperCase())
                        && mapConvert.getCountry().toUpperCase().equals(country.toUpperCase())) {
                    return mapConvert;
                }
            }
        }
        return null;
//        if (lstMapConvert == null || lstMapConvert.isEmpty()) {
//            return null;
//        }
//        return (MapConvert) lstMapConvert.get(0);
    }

    public void mergeRevenue(Map<String, Map<String, Object>> mapNewRevenue, String market) {

        System.setProperty("lua.dir", "../udf");

        if (mapNewRevenue != null && !mapNewRevenue.isEmpty()) {
            for (Map<String, Object> record : mapNewRevenue.values()) {
                //gen key
                Key key = new Key(nameSpace, market + "_REVENUE_DAILY", "" + record.get("ISDN") + record.get("REVENUE_DATE"));

                client.execute(null, key, "CAL_REVENUE_ADDITION", "merge_revenue", Value.get(record));
            }
        }
    }

    public Map<String, Map<String, Object>> sumRevenueByDay(String sumDate, List<String> groupByValue, String market) {
        System.setProperty("lua.dir", "../udf");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String performDate = sdf2.format(new Date());
        Statement stmt = new Statement();
        stmt.setNamespace(nameSpace);
        stmt.setSetName(market + "_REVENUE_DAILY");
        stmt.setFilter(Filter.equal("REVENUE_DATE", sumDate));
        stmt.setAggregateFunction("CAL_REVENUE_ADDITION", "cal_revenue_addition", Value.get(groupByValue), Value.get(performDate), Value.get(market));
        ResultSet resultSet = client.queryAggregate(null, stmt);
        while (resultSet.next()) {
            Map<String, Map<String, Object>> mapReturn = (Map<String, Map<String, Object>>) resultSet.getObject();
            FileWriter out = null;
            try {
                out = new FileWriter("../data/sumrevenue" + performDate);
                for (Map<String, Object> rec : mapReturn.values()) {
                    out.write(rec.toString() + "\n");
                }
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return mapReturn;
        }
        return null;
    }

    public long delete(String nameSpace, String setName, Filter filter) {
        System.setProperty("lua.dir", "../udf");
        Statement stmt = new Statement();
        stmt.setNamespace(nameSpace);
        stmt.setSetName(setName);
        stmt.setFilter(filter);
        stmt.setAggregateFunction("GI_LUA_COMMON", "count_record");
        ResultSet resultSet = client.queryAggregate(null, stmt);
        resultSet.next();
        long count = Long.valueOf(resultSet.getObject().toString());
        if (count > 0) {
            stmt = new Statement();
            stmt.setNamespace(nameSpace);
            stmt.setSetName(setName);
            stmt.setFilter(filter);
            client.getWritePolicyDefault().durableDelete = true;
            ExecuteTask task = client.execute(null, stmt, "GI_LUA_COMMON", "deleteRecord");
            task.waitTillComplete();
        }
        System.out.println("delete " + count + " record");
        return count;
    }

    public void mergeRevenueDailyAndMonthly(Map<String, Map<String, Map<String, Object>>> mapNewRevenue, String market) {

        System.setProperty("lua.dir", "../udf");
        List<Key> lstKeys = new ArrayList<>();
        List<Value[]> lstValues = new ArrayList<>();

        if (mapNewRevenue != null && !mapNewRevenue.isEmpty()) {
            for (String isdn : mapNewRevenue.keySet()) {
                for (String date : mapNewRevenue.get(isdn).keySet()) {
                    String month = date.substring(0, 6);
                    Map<String, Object> cdr = mapNewRevenue.get(isdn).get(date);
                    //gen key daily
                    Key keyDaily = new Key(nameSpace, market + "_REVENUE_DAILY_MONTHLY", "" + isdn + date);
                    lstKeys.add(keyDaily);
                    Value[] values = {Value.get(isdn), Value.get(date), Value.get(month), Value.get(cdr)};
                    lstValues.add(values);
                }
            }
        }

        ExecuteAsync execute = new ExecuteAsync("CAL_REVENUE_ADDITION", "merge_revenue_daily", lstKeys, lstValues);
        execute.run();
    }

    public Map<String, Map<String, Object>> sum15cent3day(String month, List<String> groupByValue, long numcent, long numday) {
        System.setProperty("lua.dir", "../udf");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String performDate = sdf2.format(new Date());
        Statement stmt = new Statement();
        stmt.setNamespace(nameSpace);
        stmt.setSetName("REVENUE_DAILY_ADN_MONTHLY");
        stmt.setFilter(Filter.equal("REVENUE_MONTH", month));
        stmt.setAggregateFunction("CAL_REVENUE_ADDITION", "sum_15cent_3day", Value.get(groupByValue), Value.get(numcent), Value.get(numday));
        ResultSet resultSet = client.queryAggregate(null, stmt);
        while (resultSet.next()) {
            Map<String, Map<String, Object>> mapReturn = (Map<String, Map<String, Object>>) resultSet.getObject();
            FileWriter out = null;
            try {
                out = new FileWriter("../data/sum15cent3day" + performDate);
                for (Map<String, Object> rec : mapReturn.values()) {
                    out.write(rec.toString() + "\n");
                }
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return mapReturn;
        }
        return null;
    }

    public Map<String, Map<String, Object>> sumRevenueMonthly(String month) {
        System.setProperty("lua.dir", "../udf");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String performDate = sdf2.format(new Date());
        Statement stmt = new Statement();
        stmt.setNamespace(nameSpace);
        stmt.setSetName("REVENUE_DAILY_ADN_MONTHLY");
        stmt.setFilter(Filter.equal("REVENUE_MONTH", month));
        stmt.setAggregateFunction("CAL_REVENUE_ADDITION", "sumRevenueMonthly");
        ResultSet resultSet = client.queryAggregate(null, stmt);
        while (resultSet.next()) {
            Map<String, Map<String, Object>> mapReturn = (Map<String, Map<String, Object>>) resultSet.getObject();
            FileWriter out = null;
            try {
                out = new FileWriter("../data/sum15cent3day" + performDate);
                for (Map<String, Object> rec : mapReturn.values()) {
                    out.write(rec.toString() + "\n");
                }
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return mapReturn;
        }
        return null;
    }

    public AerospikeClient getClient() {
        return client;
    }

    public static void main(String[] args) throws Exception {

//        Switchboard switchboard = GIClient.getInstance().getSwitchboard("2009" + "");
//        List<MasterJob> lstJob = GIClient.getInstance().getRecordByFilter(MasterJob.class, Filter.equal("CREATE_PROCESS",
//                "A"), null);
//        System.out.println("job size " + lstJob.size());
//        
//        GIClient.getInstance().deleteSet("TABLE_IMPORT", "bigdata");
//        String namespace = args[0];
//        for (int i = 1; i < args.length; i++) {
//          GIClient.getInstance().deleteSet(args[i], namespace);  
//        }
//        Map<String, Map<String, Object>> map = GIClient.getInstance().sumRevenueByDay("20170825", Arrays.asList("BTS_CODE", "CENTER_CODE"), "VTC");
//        GIClient.getInstance().sumRevenueByDay(new Date(), null, null)
//        AerospikeClient client = GIClient.getInstance().getClient();
//
//        RegisterTask task = client.register(null, "../udf/GI_FILTER_RECORD.lua", "GI_FILTER_RECORD.lua", Language.LUA);
//        task.waitTillComplete();
//
//        Map<String, Object> argument = new HashMap<>();
//
//        List<Value.MapValue> argumentSorters = new ArrayList<>();
//
//        List<Value.MapValue> argumentFilter = new ArrayList<>();
//
//        System.setProperty("lua.dir", "../udf");
//        Map<String, Object> s = new HashMap<>();
//        s.put("sort_key", "FUNCTION_ID");
//        s.put("order", "ASC");
//        argumentSorters.add(new Value.MapValue(s));
//
//        Map<String, Object> s1 = new HashMap<>();
//        s1.put("sort_key", "STAND_ID");
//        s1.put("order", "DESC");
//
//        argumentSorters.add(new Value.MapValue(s1));
//        Filter f
//        long start = System.currentTimeMillis();
//        List<VtcHome> lstBtsCode = (List<VtcHome>) GIClient.getInstance()
//                .getListVtcHome(Filter.equal("ISDN", "60200006"));
//        System.out.println("size: " + lstBtsCode.size());
//        long end1 = System.currentTimeMillis();
//        System.out.println("Time 1 = " + (end1 - start));
//
//        Thread.sleep(500);
//
//        long start2 = System.currentTimeMillis();
//        List<BtsCode> lstBtsCode1 = (List<BtsCode>) GIClient.getInstance()
//                .getListBtsCode(Filter.equal("BTS_CODE", "NW0040"));
//        System.out.println("size: " + lstBtsCode1.size());
//        long end2 = System.currentTimeMillis();
//        System.out.println("Time 2 = " + (end2 - start2));
//
//        Thread.sleep(500);
//        long start3 = System.currentTimeMillis();
//        List<BtsCode> lstBtsCode2 = (List<BtsCode>) GIClient.getInstance()
//                .getListBtsCode(Filter.equal("BTS_CODE", "NW0040"));
//        System.out.println("size: " + lstBtsCode2.size());
//        long end3 = System.currentTimeMillis();
//        System.out.println("Time 3 = " + (end3 - start3));
//
//        Thread.sleep(500);
//        long start4 = System.currentTimeMillis();
//        List<BtsCode> lstBtsCode3 = (List<BtsCode>) GIClient.getInstance()
//                .getListBtsCode(Filter.equal("BTS_CODE", "NW0040"));
//        System.out.println("size: " + lstBtsCode3.size());
//        long end4 = System.currentTimeMillis();
//        System.out.println("Time 4 = " + (end4 - start4));
//        GIClient.getInstance().deleteSet("master_process", "bigdata");
//        GIClient.getInstance().deleteSet("jobs", "bigdata");
//        Map<String, Object> f = new HashMap<>();
//        f.put("field", "FUNCTION_ID");
//        f.put("value", "8002");
//        f.put("operator", "<");
//
//        argumentFilter.add(new Value.MapValue(f));
//
//        argument.put("sorters", new Value.ListValue(argumentSorters));
//        argument.put("filters", new Value.ListValue(argumentFilter));
//
//        Statement stmt = new Statement();
//        stmt.setNamespace("bigdata");
//        stmt.setSetName("md_standardize");
//        stmt.setAggregateFunction("GI_FILTER_RECORD", "GI_FILTER_RECORD", Value.get(argument));
//        ResultSet resultSet = client.queryAggregate(null, stmt);
//        while (resultSet.next()) {
//            System.out.println(resultSet.getObject());
////        }
//        if ("INT".equals(args[4].toUpperCase())) {
//            long i = GIClient.getInstance().delete(args[0], args[1], Filter.equal(args[2], Integer.parseInt(args[3])));
//            System.out.println("delete rows = " + i);
//        } else {
//            long i = GIClient.getInstance().delete(args[0], args[1], Filter.equal(args[2], args[3]));
//            System.out.println("delete rows = " + i);
//        }
//        long i = GIClient.getInstance().delete("bigdata", "jobs", Filter.equal("JOB_STATUS", 2L));
//        System.out.println("delete rows = " + i);
        GIClient.getInstance().deleteSet("master_process", "test");
//        GIClient.getInstance().deleteSet("md_rule", "bigdata");
//        GIClient.getInstance().deleteSet("md_expression", "bigdata");
//        GIClient.getInstance().deleteSet("md_sub_expression", "bigdata");
//        GIClient.getInstance().deleteSet("md_data_structure", "bigdata");
//        GIClient.getInstance().deleteSet("md_function", "bigdata");
//        GIClient.getInstance().deleteSet("md_result", "bigdata");
//        GIClient.getInstance().deleteSet("md_standardize", "bigdata");
//        GIClient.getInstance().deleteSet("switchboard", "bigdata");
//        GIClient.getInstance().deleteSet("switchboard_function_config", "bigdata");
//        GIClient.getInstance().deleteSet("VTC_REVENUE_DAILY", "bigdata");
//        GIClient.getInstance().deleteSet(args[1], args[0]);
//        GIClient.getInstance().deleteSet("VTC_ISDN_HOME", "bigdata");
//        GIClient.getInstance().deleteSet("VTZ_ISDN_HOME", "bigdata");
//        GIClient.getInstance().deleteSet("md_switchboard", "bigdata");
//        GIClient.getInstance().deleteSet("master_process", "cdr_process");
//        GIClient.getInstance().deleteSet("md_rule", "cdr_process");
//        GIClient.getInstance().deleteSet("md_expression", "cdr_process");
//        GIClient.getInstance().deleteSet("md_sub_expression", "cdr_process");
//        GIClient.getInstance().deleteSet("md_data_structure", "cdr_process");
//        GIClient.getInstance().deleteSet("md_function", "cdr_process");
//        GIClient.getInstance().deleteSet("md_result", "cdr_process");
//        GIClient.getInstance().deleteSet("md_standardize", "bigdata");
//        GIClient.getInstance().deleteSet("md_switchboard", "cdr_process");
//        Switchboard x = GIClient.getInstance().getSwitchboard("1");
//        System.out.println("x");
//        GIClient.getInstance().deleteSet("master_process", "bigdata");
//        GIClient.getInstance().deleteSet("jobs", "bigdata");
        //GIClient.getInstance().deleteSet("jobs", "bigdata");
        //GIClient.getInstance().deleteSet("jobs", "bigdata");
//        GIClient.getInstance().deleteSet("jobs", "bigdata");
//        List<DataStructureField> lst = Client.getInstance().getRecordByFilter(DataStructureField.class, null,
//                PredExp.stringBin("SWITCH_TYPE"),
//                PredExp.stringValue("HUAWEI"),
//                PredExp.stringEqual(),
//                PredExp.stringBin("COUNTRY"),
//                PredExp.stringValue("LAOS"),
//                PredExp.stringEqual(),
//                PredExp.and(2));
//        List<ProcessParamBO> listParam = GIClient.getInstance().getRecordByFilter(ProcessParamBO.class, null,
//                PredExp.integerBin("MASTER_ID"),
//                PredExp.integerValue(1),
//                PredExp.integerEqual()
//                //,
////                PredExp.stringBin("STATUS"),
////                PredExp.integerValue(1),
////                PredExp.stringEqual(),
////                PredExp.and(2)
//        );
//        System.out.println("1");
//        DataStructureField field1 = new DataStructureField();
//        field1.setStructureId(10L);
//        field1.setStructureName("DATA_INPUT_HUAWEI");
//        field1.setFieldName("ERROR");
//        field1.setFieldType("STRING");
//        field1.setFieldPos(10);
//        GIClient.getInstance().insertObject(field1);
//
//        DataStructureField field2 = new DataStructureField();
//        field2.setStructureId(2L);
//        field2.setStructureName("DATA_INPUT_HUAWEI");
//        field2.setFieldName("TO_PHONE");
//        field2.setFieldType("STRING");
//        field2.setFieldPos(2);
//        field2.setSwitchType("HUAWEI");
//        field2.setCountry("LAOS");
//        GIClient.getInstance().insertObject(field2);
//
//        DataStructureField field3 = new DataStructureField();
//        field3.setStructureId(3L);
//        field3.setStructureName("DATA_INPUT_HUAWEI");
//        field3.setFieldName("TRUNK_IN");
//        field3.setFieldType("STRING");
//        field3.setFieldPos(3);
//        field3.setSwitchType("HUAWEI");
//        field3.setCountry("LAOS");
//        GIClient.getInstance().insertObject(field3);
//
//        DataStructureField field4 = new DataStructureField();
//        field4.setStructureId(4L);
//        field4.setStructureName("DATA_INPUT_HUAWEI");
//        field4.setFieldName("TRUNK_OUT");
//        field4.setFieldType("STRING");
//        field4.setFieldPos(4);
//        field4.setSwitchType("HUAWEI");
//        field4.setCountry("LAOS");
//        GIClient.getInstance().insertObject(field4);
//        Jobs jobs = new Jobs();
//        jobs.setId(100L);
//        jobs.setCreateProcess("sinhhv");
//        jobs.setJobStatus("0");
//        jobs.setJobType("3");
//        jobs.setFlow("FILTER");
//        jobs.setSwitchType("HUAWEI");
//        jobs.setCountry("LAOS");
//        jobs.setJobParam("1");
//        jobs.setJobField("FILEID");
//        jobs.setCreateDate("01-01-2017");
//        jobs.setProcessDate("01-01-2017");
//        jobs.setWorkerId("123456");
//        jobs.setIp("127.0.0.1");
//        client.insertObject(jobs);
//        Rule rule = new Rule();
//        rule.setRuleId(1L);
//        rule.setSwitchType("HUAWEI");
//        rule.setCountry("LAOS");
//        rule.setRuleName("filter");
//        rule.setAllowableExit(0L);
//        GIClient.getInstance().insertObject(rule);
//
//        SubExpression subex = new SubExpression();
//        subex.setExpressionId(1L);
//        subex.setSubExpressionId(1L);
//        subex.setLeftField("FROM_PHONE");
//        subex.setOperator("=");
//        subex.setConstant("01649542680");
//        subex.setIsConstant(1L);
//        subex.setSwitchType("HUAWEI");
//        subex.setCountry("LAOS");
//        GIClient.getInstance().insertObject(subex);
//
//        GIClient.getInstance().deleteSet("tb_standadize_error","cdr_process");
//
//        Result rs = new Result();
//        rs.setResultId(1L);
//        rs.setLocalFolder("/home/hoangsinh/data_test/output");
//        rs.setFileNameTemplate("out.txt");
//        rs.setResultName("Result");
//        rs.setRuleId(1L);
//        rs.setFileExtension("txt");
//        rs.setResultType("1");
//        rs.setImportTableId(1L);
//        rs.setDataStructureName("DATA_INPUT_HUAWEI");
//        rs.setSeqName("seq");
//        rs.setSwitchType("HUAWEI");
//        rs.setCountry("LAOS");
//        GIClient.getInstance().insertObject(rs);
//        GIClient.getInstance().deleteSet("table_import", "cdr_process");
//        ImportTableBO importTb = new ImportTableBO();
//        importTb.setImportTableId(1l);
//        importTb.setDatabaseName("IMPORT_DATABASE");
//        importTb.setIsDelete(0l);
//        importTb.setUrl("45.117.80.174");
//        importTb.setTableName("table_import");
//        importTb.setTableErrorName("tb_standadize_error");
//        GIClient.getInstance().insertObject(importTb);
//
//        GIClient.getInstance().insertObject(rs);
//
//        Expression ex = new Expression();
//        ex.setExpressionId(1L);
//        ex.setRuleId(1L);
//        ex.setCountry("LAOS");
//        ex.setSwitchType("HUAWEI");
//        GIClient.getInstance().insertObject(ex);
//        FunctionBO function = new FunctionBO();
//        function.setFunctionId(1l);
//        function.setFunctionName("StandardISDN");
//        function.setIsScript(0l);
//        GIClient.getInstance().insertObject(function);
//
//        StandardizeField standardizeField = new StandardizeField();
//        standardizeField.setStandardizeId(2l);
//        standardizeField.setFunctionId(1l);
//        standardizeField.setSwitchType("OCS_VTTEK_REC");
//        standardizeField.setCountry("VTZ");
//        standardizeField.setPriority(1l);
//        standardizeField.setResultId(2000l);
//        standardizeField.setIsDelete(0l);
//        GIClient.getInstance().insertObject(standardizeField);
//        FullDataRouteBO route1 = new FullDataRouteBO();
//        route1.setRouteId(1l);
//        route1.setSwitchId(101l);
//        route1.setRouteName("trunk_in");
//        GIClient.getInstance().insertObject(route1);
//
//        FullDataRouteBO route2 = new FullDataRouteBO();
//        route2.setRouteId(2l);
//        route2.setSwitchId(101l);
//        route2.setRouteName("trunk_out");
//        GIClient.getInstance().insertObject(route2);
//        GIClient.getInstance().deleteSet("md_data_structure","cdr_process");
//        System.out.println(UUID.randomUUID().toString());
    }
}
