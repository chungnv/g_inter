/*
 * Copyright 2012-2017 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.viettel.aerospike.main;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import com.aerospike.client.Log;
import com.aerospike.client.Log.Level;
import com.aerospike.client.async.EventLoopType;
import com.aerospike.client.policy.TlsPolicy;
import com.aerospike.client.util.Util;
import com.viettel.ginterconnect.handler.ProcessorHandler;
//import com.viettel.aerospike.handle.ProcessorHandler;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);
    private static final long serialVersionUID = 1L;
    private static final String ExampleNames = "com.viettel.aerospike.main.DatabaseCdrProcess";

    /**
     * Main entry point.
     *
     * @param args
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("../etc/log.conf");
        try {
            if (true) {
                Client.getInstance().setProcessorHandler(new ProcessorHandler());
                Client.getInstance().init("45.117.80.174", 3000, null, null, "cdr_process");
                logger.debug(Client.getInstance().getSequence(null));
                //Client.getInstance().getJobsRecordByStatus(1);
                Thread.sleep(5000);
                return;
            }

            Options options = new Options();

            CommandLineParser parser = new PosixParser();
            CommandLine cl = parser.parse(options, args, false);

            Parameters params = parseParameters(cl);
            System.out.println("" + params);

            if (cl.hasOption("netty")) {
                params.eventLoopType = EventLoopType.NETTY_NIO;
            }

            if (cl.hasOption("nettyEpoll")) {
                params.eventLoopType = EventLoopType.NETTY_EPOLL;
            }

            if (cl.hasOption("d")) {
                Log.setLevel(Level.DEBUG);
            }

            Console console = new Console();
            runExamples(console, params, ExampleNames);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parse command line parameters.
     */
    private static Parameters parseParameters(CommandLine cl) throws Exception {
        String host = cl.getOptionValue("h", "127.0.0.1");
        String portString = cl.getOptionValue("p", "3000");
        int port = Integer.parseInt(portString);
        String namespace = cl.getOptionValue("n", "cdr_process");
        String set = cl.getOptionValue("s", "master_process");

        if (set.equals("empty")) {
            set = "";
        }

        String user = cl.getOptionValue("U");
        String password = cl.getOptionValue("P");

        if (user != null && password == null) {
            java.io.Console console = System.console();

            if (console != null) {
                char[] pass = console.readPassword("Enter password:");

                if (pass != null) {
                    password = new String(pass);
                }
            }
        }

        TlsPolicy tlsPolicy = null;

        if (cl.hasOption("tls")) {
            tlsPolicy = new TlsPolicy();

            if (cl.hasOption("tp")) {
                String s = cl.getOptionValue("tp", "");
                tlsPolicy.protocols = s.split(",");
            }

            if (cl.hasOption("tlsCiphers")) {
                String s = cl.getOptionValue("tlsCiphers", "");
                tlsPolicy.ciphers = s.split(",");
            }

            if (cl.hasOption("tr")) {
                String s = cl.getOptionValue("tr", "");
                tlsPolicy.revokeCertificates = Util.toBigIntegerArray(s);
            }

            if (cl.hasOption("te")) {
                tlsPolicy.encryptOnly = true;
            }
        }
        return new Parameters(tlsPolicy, host, port, user, password, namespace, set);
    }

    /**
     * Connect and run one or more client examples.
     *
     * @param console
     */
    public static void runExamples(Console console, Parameters params, String examples) throws Exception {
        List<String> syncExamples = new ArrayList<>();
        List<String> asyncExamples = new ArrayList<>();

        syncExamples.add(examples);

        if (syncExamples.size() > 0) {
            Example.runExamples(console, params, syncExamples);
        }

        if (asyncExamples.size() > 0) {
            AsyncExample.runExamples(console, params, asyncExamples);
        }
    }
}
