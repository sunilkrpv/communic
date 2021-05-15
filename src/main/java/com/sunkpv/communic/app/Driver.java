package com.sunkpv.communic.app;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.sunkpv.communic.CommunicClient;
import com.sunkpv.communic.CommunicServer;


/**
 * Driver: Main driver class to run the module.
 * This can be run as server OR client mode. 
 * 
 * To run as server mode pass the following arguements:
 * 	java -jar communic.jar -server -port 'port-number' -address 'ip/identifiable name'
 * 
 * To run as client mode pass the following arguements:
 * 	java -jar communic.jar -client -port 'port-number' -address 'ip/identifiable name'
 * 
 * 
 * @author skumar
 * @since 26/Dec/2017
 *
 */
public class Driver {
	
	private static class BootUp {
		
		/** 127 is client, -128 is server */
		byte runAs = 0; // default run as client
		/** the default binding port */
		int port = 5001;
		/** ip address. For Client it would be the server's IP address. */
		String address = "localhost";
	}
	
	private static BootUp bootUp;

	public static void main(String[] args) {
		
		bootUp = new BootUp();
		
		if(!parseArgs(args)) {
			System.exit(-1);
		}
		
		if(bootUp.runAs == -128) {
			runServer();
		}
		else if(bootUp.runAs == 127) {
			runClient();
		}
		else {
			System.out.println("Unsupported operation");
		}
		
		System.exit(0);
	}

	private static void runClient() {
		
		CommunicClient connection = new CommunicClient(bootUp.address, bootUp.port);
		connection.connect();
		
		Scanner sc = new Scanner(System.in);
		String line = "";
		do {
			try {
				
				line = sc.nextLine();
				connection.send(line.getBytes());
			} catch (Exception e) {
				break;
			}
			
		} while(!"^C".equals(line));
		
		connection.disconnect();
		
		System.out.println("type any key to exit");
		try {
			sc.close();
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void runServer() {
		
		CommunicServer server = new CommunicServer(bootUp.port);
		server.syncStart();
		server.stop();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean parseArgs(String[] args) {
		
		
		
		Option serverOption = Option.builder()
								.longOpt("server")
								.argName("server mode")
								.required(false)
								.desc("runs as server").build();
		Option clientOption = Option.builder()
								.longOpt("client")
								.argName("client mode")
								.optionalArg(false)
								.required(false)
								.desc("runs as client").build();
		Option portOption = Option.builder()
								.longOpt("port")
								.argName("port")
								.hasArg()
								.required(true)
								.desc("server port value to bind with ip").build();
		Option ipAddress = Option.builder()
									.longOpt("address")
									.argName("ip address")
									.hasArg()
									.required(true)
									.desc("server ip address").build();
		
		Options options = new Options();
		options.addOption(serverOption);
		options.addOption(clientOption);
		options.addOption(portOption);
		options.addOption(ipAddress);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		
		CommandLine cmd;
		try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("comm-core", options);
            return false;
        }
		
		bootUp.runAs = (byte) (cmd.hasOption("server") ? -128 : 127);
		bootUp.address = cmd.getOptionValue("address");
		bootUp.port = Integer.parseInt(cmd.getOptionValue("port"));
		
		return true;
	}

}
