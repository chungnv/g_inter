/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author
 */
public class ImporterConnectionPool extends Thread {

    //Thoi gian time out de release connection neu khong duoc su dung
    //thoi gian mac dinh la 2 phut
    private final int timeOut = 2 * 60 * 1000;
    private int capacityConnection = 3;
    private BlockingQueue<ImporterConnection> queueWaitConnection = null;
    private BlockingQueue<ImporterConnection> queueReadyImporter = null;

    public ImporterConnectionPool(Importer importer) {
        queueReadyImporter = new ArrayBlockingQueue<>(capacityConnection);
        queueWaitConnection = new ArrayBlockingQueue<>(this.capacityConnection);
        while (queueWaitConnection.size() < this.capacityConnection) {
            queueWaitConnection.offer(new ImporterConnection(importer));
        }
    }

    public ImporterConnectionPool(Importer importer, int capacity) {
        this.capacityConnection = capacity;
        queueReadyImporter = new ArrayBlockingQueue<>(this.capacityConnection);
        queueWaitConnection = new ArrayBlockingQueue<>(this.capacityConnection);
        while (queueWaitConnection.size() < this.capacityConnection) {
            queueWaitConnection.offer(new ImporterConnection(importer));
        }
    }

    public ImporterConnection getConnection() {
        ImporterConnection result = null;

        //lock ready lai de chi trong 1 thoi diem chi co
        //1 tien trinh request them connection moi thoi

        try {
            result = queueReadyImporter.poll();

            while (result == null) {
                //Neu nhu trong queue ready khong co thi tim kiem trong queue wait de mo connection moi
                if (result == null) {
                    result = queueWaitConnection.poll();
                    if (result != null) {
                        result.createConnection();
                    }
                }

                if (result == null) {
                    //Neu nhu trong wait cung khong co nghia la connection da duoc dung het thi doi tu ready queue
                    //Doi cho den khi co connection tra ve
                    //Lay trong queue ready trong khoang thoi gian 10s neu khong co lai quay lai trong queue Wait de kiem tra
//                    result = queueReadyImporter.poll(10000, TimeUnit.MILLISECONDS);
                    result = getFromReadyPool(10);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return result;
    }
    
    private ImporterConnection getFromReadyPool(long timeout) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return queueReadyImporter.poll(5000, TimeUnit.MILLISECONDS);
            }
        };
        Future<Object> future = executor.submit(callable);
        try {
            ImporterConnection connection = (ImporterConnection) future.get(timeout, TimeUnit.SECONDS);
            return connection;
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
//            ex.printStackTrace();
            return null;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

    /*Tra connection ve cho pool de quan ly de cho cac tien trinh khac can dung */
    public void releaseConnection(ImporterConnection connection) {
        // reset lastUse property cua connection
        // Day them connection vao trong readyQueue
        // Trong queu se duoc sap xep theo thu tu thoi gain lastUse

        if (connection != null) {
            if (connection.getConnection() == null) {
                connection.setLastUse(null);
                connection.setInsertStmt(null);
                connection.setDuplicateStmt(null);
                connection.setInsertStmt(null);
                queueWaitConnection.offer(connection);
            } else {
//                Connection conn = connection.getConnection();

                try {
                    connection.setLastUse(new Date());
                    //Them vao danh sach cac Connection trong poll
                    queueReadyImporter.offer(connection);
                } catch (Exception ex) {
                    ex.printStackTrace();

                    connection.releaseConnection();

                    //Them vao trong wait
                    queueWaitConnection.offer(connection);
                }
            }
        }
    }

    /*
    Cho he thong chay tu dong kiem tra cac connection nao qua time out
    va khong duoc su dung thi se realse
     */
    @Override
    public void run() {
        //Kiem tra lien tuc xem trong queue readyQueueCOnnection
        while (true) {
            //lock viec get connection lai
            try {
                //Bien dem cac connection kiem tra
                int count = 0;

                Date currentDate = new Date();

                int size = queueReadyImporter.size();
                while (count < size) {
                    ImporterConnection dbConn = queueReadyImporter.poll();
                    if (dbConn == null) {
                        break;
                    }
                    if (dbConn.getLastUse() != null) {
                        if (currentDate.getTime() - dbConn.getLastUse().getTime() >= timeOut) {
                            Connection conn = dbConn.getConnection();
                            try {
                                if (conn != null) {
                                    conn.close();
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            dbConn.setConnection(null);
                            dbConn.setLastUse(null);
                            queueWaitConnection.offer(dbConn);
                        } else {
                            //Neu nhu chua qua thoi gian time out thi insert lai vao queue
                            queueReadyImporter.offer(dbConn);
                        }
                    } else {
                        dbConn.setLastUse(new Date());
                        queueReadyImporter.offer(dbConn);
                    }
                    count++;
                }
                //dung 1 khoang thoi gian truoc khi check tiep lan sau
                //dung 30s
                sleep(30 * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
