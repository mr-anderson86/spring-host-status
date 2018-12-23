package com.spring.app.hoststatus.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.lang.management.ManagementFactory;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

@RestController
@RequestMapping("/")
public class HostResources {
    private String runCommand(String cmd) throws IOException {
        String command[] =  {"/bin/sh", "-c", cmd};
        Process uptimeProc = Runtime.getRuntime().exec(command);
        BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        return in.readLine();
    }

    private double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }

    @GetMapping
    public String hello() {
        String hostName="", osName="", upTime="", loadAvg="", totalMem="", usedMem="", freeMem="", memUsage="", cpuUsage="";
        String newLine = "</br>";

        try {
            hostName = "Hostname: " + InetAddress.getLocalHost().getHostName() + newLine;
            osName = "Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + newLine ;
            upTime = "Uptime: " + runCommand("uptime | awk '{print $3,$4,$5}' | cut -d',' -f1,2") + newLine;
            loadAvg = "Load AVG: " + runCommand("uptime | awk '{print $(NF-2),$(NF-1), $NF}'") + newLine;
            totalMem = "Total memory: " + runCommand("free -h | grep Mem | awk '{print $2}'") + ", ";
            usedMem = "Used memory: " + runCommand("free -h | grep Mem | awk '{print $3}'") + ", ";
            freeMem = "Free memory: " + runCommand("free -h | grep Mem | awk '{print $4}'");
            memUsage = totalMem + usedMem + freeMem + newLine;
            double cpu = getProcessCpuLoad();
            cpuUsage = "CPU Usage: " + cpu +"%" + newLine;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mainPage = "Welcome to the web host status application :-)" + newLine + newLine;

        mainPage = mainPage + hostName + osName + upTime + loadAvg + memUsage + cpuUsage;
        return mainPage;
    }

}
