package com.batix.rundeck.core;

import java.util.Map;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;

public class PropertyResolver {
    /**
     * Resolve a job/node/project/framework property by first checking the job attributes named X,
     * then node attributes named X, project properties named "project.X", then framework properties
     * named "framework.X". If none of those exist, return the default value
     */
    public static String resolveProperty(
            final String attribute,
            final String defaultValue,
            final String frameworkProject,
            final Framework framework,
            final INodeEntry node,
            final Map<String, Object> jobConf
    )
    {
        // First check if the job Configuration contains the attribute
        // Then check if the project has that attribute
        // Finally check the framework
        if ( jobConf != null && jobConf.containsKey(attribute) ) {
               return (String) jobConf.get(attribute);
        } else if ( node != null && null != node.getAttributes().get(attribute)) {
            return node.getAttributes().get(attribute);
        } else if ( framework.hasProjectProperty(AnsibleDescribable.PROJ_PROP_PREFIX + attribute, frameworkProject)
             && !"".equals(framework.getProjectProperty(frameworkProject, AnsibleDescribable.PROJ_PROP_PREFIX + attribute))
           ) {
               return framework.getProjectProperty(frameworkProject, AnsibleDescribable.PROJ_PROP_PREFIX + attribute);
        } else if (framework.hasProperty(AnsibleDescribable.FWK_PROP_PREFIX + attribute)) {
            return framework.getProperty(AnsibleDescribable.FWK_PROP_PREFIX + attribute);
        } else {
            return defaultValue;
        }
    }

    public static String resolveJobProperty(
            final String attribute,
            final String defaultValue,
            final Map<String, Object> jobConf
    )
    {
        // First check if the job Configuration contains the attribute
        // Then check if the project has that attribute
        // Finally check the framework
        if ( jobConf.containsKey(attribute) ) {
               return (String) jobConf.get(attribute);
        } else {
            return defaultValue;
        }
    }

    public static Integer resolveIntProperty(
            final String attribute,
            final Integer defaultValue,
            final String frameworkProject,
            final Framework framework,
            final INodeEntry node,
            final Map<String, Object> jobConf
    ) throws ConfigurationException
    {

        Integer value = defaultValue;
        final String string = resolveProperty(attribute, null, frameworkProject, framework, node, jobConf);
        if (null != string) {
            try {
                value = Integer.parseInt(string);
            } catch (NumberFormatException e) {
            	throw new ConfigurationException("Can't parse attribute :" + attribute + ", value: " +
            			string + " Expected Integer. : " + e.getMessage());
            }
        }
        return value;
    }

    public static Long resolveLongProperty(
            final String attribute,
            final Long defaultValue,
            final String frameworkProject,
            final Framework framework,
            final INodeEntry node,
            final Map<String, Object> jobConf
            ) throws ConfigurationException
    {

        Long value = defaultValue;
        final String string = resolveProperty(attribute, null, frameworkProject, framework, node, jobConf);
        if (null != string) {
            try {
                value = Long.parseLong(string);
            } catch (NumberFormatException e) {
            	throw new ConfigurationException("Can't parse attribute :" + attribute + ", value: " +
            			string + " Expected Long. : " + e.getMessage());
            }
        }
        return value;
    }

    public static Boolean resolveBooleanProperty(
            final String attribute,
            final Boolean defaultValue,
            final String frameworkProject,
            final Framework framework,
            final INodeEntry node,
            final Map<String, Object> jobConf    ) throws ConfigurationException
    {

        Boolean value = defaultValue;
        final String string = resolveProperty(attribute, null, frameworkProject, framework, node, jobConf);
        if (null != string) {
        	try {
                value = Boolean.parseBoolean(string);
        	} catch (NumberFormatException e) {
            	throw new ConfigurationException("Can't parse attribute :" + attribute + ", value: " +
            			string + " Expected Boolean. : " + e.getMessage());
            }
        }
        return value;
    }

    public static String evaluateSecureOption(final String optionName, final ExecutionContext context ) {
        if (null == optionName) {
            return null;
        }
        if (null == context.getPrivateDataContext()) {
            return null;
        }
        final String[] opts = optionName.split("\\.", 2);
        String dataset = null;
        String optname = null;
        if (null != opts && 2 == opts.length) {
            dataset = opts[0];
            optname = opts[1];
        } else if (null != opts && 1 == opts.length) {
            dataset = "option";
            optname = opts[0];
        }
        final Map<String, String> option = context.getPrivateDataContext().get(dataset);
        if (null != option) {
            return option.get(optname);
        }
        return null;
    }

}
