/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.main;

import com.viettel.ginterconnect.process.queue.GetJobQueue;

/**
 *
 * @author ubuntu
 */
public class StopWorker {

    public static void main(String[] args) {
        GetJobQueue.getInstance().stop();
    }

}
