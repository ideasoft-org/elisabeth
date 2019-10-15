package org.elisabeth.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;

public class TcpPlugin extends Plugin {

    @Override
    public Object evaluate() {
        String port = null;
        Boolean loop = false;
        List<Function<Object, String>> tasks = new ArrayList<>();

        Map<String, Object> settingsMap = (Map<String, Object>) getYamlData().get("$tcp");

        for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
            if (entry.getKey().equals("port")) {
                port = entry.getValue().toString();
            }

            if (entry.getKey().equals("loop")) {
                loop = Boolean.parseBoolean(entry.getValue().toString());
            }

            if (entry.getKey().equals("on_connect")) {
                tasks = _parse_Tcp_On_Connect((List<Map<String, Object>>) entry.getValue());
            }
        }
        EchoServer server = new EchoServer(Integer.parseInt(port), loop);
        server.serve(tasks);
        return null;
    }

    private List<Function<Object, String>> _parse_Tcp_On_Connect(List<Map<String, Object>> commands) {


        List<Function<Object, String>> tasks = new ArrayList<>();

        for (Map<String, Object> entry : commands) {

            String pluginId = entry.keySet().stream().findAny().get();

            Plugin plugin;

            try {
                plugin = getPluginContaier().instantiate(pluginId, entry, getGlobalVars());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            tasks.add(new Function<Object, String>() {
                @Override
                public String apply(Object o) {
                    Object result = plugin.evaluate();
                    if (result != null) {
                        return result.toString();
                    } else {
                        return "";
                    }
                }
            });
        }
        return tasks;
    }




    public static class EchoServer
    {
        public EchoServer(int portnum, boolean loop)
        {
            this.loop = loop;
            try
            {
                server = new ServerSocket(portnum);
            }
            catch (Exception err)
            {
                System.out.println(err);
            }
        }

        public void serve(List<Function<Object, String>> tasks)
        {
            try
            {
                do
                {
                    Socket client = server.accept();
                    BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter w = new PrintWriter(client.getOutputStream(), true);
                    tasks.forEach( (obj) -> w.println(obj.apply(null)));
                    client.close();
                } while (loop);
            }
            catch (Exception err)
            {
                System.err.println(err);
            }
        }

        private ServerSocket server;
        private boolean loop = false;
    }
}
