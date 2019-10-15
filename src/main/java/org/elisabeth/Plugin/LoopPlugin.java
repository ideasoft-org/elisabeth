package org.elisabeth.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LoopPlugin extends Plugin {

    @Override
    public Object evaluate() {
        List<Function<Object, String>> tasks = new ArrayList<>();

        List<Map<String, Object>> settingsMap = (List<Map<String, Object>>) getYamlData().get("$loop");

        tasks = _parse_Tcp_On_Connect(settingsMap);

        while (true) {
            tasks.forEach( (obj) -> obj.apply(null));

            try {
                //Thread.sleep(10L);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
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
