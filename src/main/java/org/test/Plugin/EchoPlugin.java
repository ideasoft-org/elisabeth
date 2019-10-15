package org.test.Plugin;


public class EchoPlugin extends Plugin {

    @Override
    public Object evaluate() {

        /** **/
        String bashValue = (String) getYamlData().get( getYamlId(EchoPlugin.class) );

        if (bashValue.startsWith("$")) {
            System.out.println(getGlobalVars().get(bashValue.substring(1)));
            return getGlobalVars().get(bashValue.substring(1));
        } else {
            System.out.println(bashValue.substring(1));
            return bashValue;
        }
    }
}
