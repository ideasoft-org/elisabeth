package org.elisabeth.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BashPlugin extends Plugin {

    @Override
    public Object evaluate() {

        /** **/
        Object bashValue = getYamlData().get( getYamlId(BashPlugin.class) );

        if (bashValue.getClass().equals(String.class)) {
            String result = _evalBash( (String) bashValue);
            System.out.println( result );
            return result;
        } else {
            String bashResult = "";
            Map<String, Object> mapValue = (LinkedHashMap<String, Object>) bashValue;
            for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                if (entry.getKey().equals("exec")) {
                    bashResult = _evalBash(entry.getValue().toString());
                }
                if (entry.getKey().equals("store")) {
                    getGlobalVars().put(entry.getValue().toString(), bashResult);
                }
            }
            return bashResult;
        }
    }

    private static String _evalBash(String command) {

        Random random = new Random();
        String filename = "temp_sh_" + random.nextFloat() + ".sh";

        Path shPath = Paths.get(filename);
        try{
            Files.write(shPath, "#!/bin/sh\n".getBytes());
            Files.write(shPath, command.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("/bin/sh " + filename, null);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }

        try {
            Files.deleteIfExists(shPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

}
