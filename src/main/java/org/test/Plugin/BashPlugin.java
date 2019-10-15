package org.test.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

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
                //System.out.println(line);
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
