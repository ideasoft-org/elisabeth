package org.test;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.test.Plugin.*;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.io.*;
import java.net.*;
@SpringBootApplication
public class Main {

    private static Map<String, String> heap = new HashMap<>();

    public static void main(String[] args) throws Exception {



        Yaml yaml = new Yaml();
        String yamlSrc = new String(
                Files.readAllBytes(
                        Paths.get(
                                "/home/maksim/test/milana-sql/src/main/resources/util.yaml"
                        )
                )
        );

        Map<String, Object> yamlResult = yaml.load(yamlSrc);


        yamlResult.entrySet().stream().forEach( entry -> {

            if (entry.getKey().startsWith("thread_")) {
                thread(entry);
            }
        });


        Integer a = 1;
    }

    private static void thread(Map.Entry<String, Object> threadContent) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){

                PluginContaier pluginContaier = new PluginContaier();
                pluginContaier.register(BashPlugin.class);
                pluginContaier.register(TcpPlugin.class);
                pluginContaier.register(EchoPlugin.class);
                pluginContaier.register(LoopPlugin.class);

                ((List)threadContent.getValue()).forEach( listItem -> {

                    Map<String, Object> yamlData = (Map<String, Object>) listItem;

                    String yamlId = yamlData.entrySet().stream().findFirst().get().getKey();

                    try {
                        pluginContaier.instantiate(yamlId, yamlData, heap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                pluginContaier.evalAll();
            }
        });
        thread.start();
    }


    private static void loop(HashMap<String, Object> bash) {

        Object tcpValue = bash.get("$loop");
        if (tcpValue == null) return;

        while (true) {
            for (Map<String, Object> entry : (List<Map<String, Object>>) tcpValue) {
                bash(entry);
                echo(entry);
            }
            try {
                Thread.sleep(3000l);
            } catch (Exception e) {}
        }
    }


    private static void bash(Map<String, Object> bash) {

        Object bashValue = bash.get("$bash");
        if (bashValue == null) return;

        if (bashValue.getClass().equals(String.class)) {
            System.out.println( _evalBash( (String) bashValue) );
            return;
        } else {

            String bashResult = "";
            Map<String, Object> mapValue = (LinkedHashMap<String, Object>) bashValue;
            for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                if (entry.getKey().equals("exec")) {
                    bashResult = _evalBash(entry.getValue().toString());
                }
                if (entry.getKey().equals("store")) {
                    heap.put(entry.getValue().toString(), bashResult);
                }
            }
        }
    }



    private static void echo(Map<String, Object> bash) {

        Object echoValue = bash.get("$echo");
        if (echoValue == null) return;

        if (echoValue.getClass().equals(String.class)) {

            String content = (String) echoValue;

            if (content.startsWith("$")) {
                System.out.println(heap.get(content.substring(1)));
            } else {
                System.out.println( (String) echoValue);
            }
        }

    }


    private static String _evalBash(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command, null);
        } catch (Exception e){
            return null;
        }

        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
                System.out.println(line);
            }

            // Null was received, so loop was aborted.

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());

        }
//
//        try (Scanner scanner = new Scanner(process.getInputStream(), "UTF-8")) {
//            return scanner.next();
//        }\
        return result;
    }
}