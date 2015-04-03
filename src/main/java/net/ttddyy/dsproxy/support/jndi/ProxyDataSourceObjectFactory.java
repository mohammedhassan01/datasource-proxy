package net.ttddyy.dsproxy.support.jndi;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * TODO: documentation
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class ProxyDataSourceObjectFactory implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {

        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference reference = (Reference) obj;

        String dataSourceJndiName = getContentFromReference(reference, "dataSource");
        String proxyDataSourceName = getContentFromReference(reference, "proxyName");
        String listenerNames = getContentFromReference(reference, "listeners");
        String logLevel = getContentFromReference(reference, "logLevel");
        String foramt = getContentFromReference(reference, "format");
        String queryTransformer = getContentFromReference(reference, "queryTransformer");
        String parameterTransformer = getContentFromReference(reference, "parameterTransformer");

        // retrieve datasource from JNDI
        Object dataSourceResource = new InitialContext().lookup(dataSourceJndiName);
        if (dataSourceResource == null) {
            throw new Exception(String.format("%s is not available.", dataSourceJndiName));
        } else if (!(dataSourceResource instanceof DataSource)) {
            throw new Exception(String.format("%s is not DataSource: %s", dataSourceJndiName, dataSourceResource));
        }
        DataSource dataSource = (DataSource) dataSourceResource;

        // builder
        ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create(dataSource);

        if (proxyDataSourceName != null) {
            builder.name(proxyDataSourceName);
        }

        if (listenerNames != null) {
            for (String listenerName : getListenerNames(listenerNames)) {
                if ("commons".equalsIgnoreCase(listenerName)) {
                    if (logLevel != null) {
                        builder.logQueryByCommons(CommonsLogLevel.valueOf(logLevel.toUpperCase()));
                    } else {
                        builder.logQueryByCommons();
                    }
                } else if ("slf4j".equalsIgnoreCase(listenerName)) {
                    if (logLevel != null) {
                        builder.logQueryBySlf4j(SLF4JLogLevel.valueOf(logLevel.toUpperCase()));
                    } else {
                        builder.logQueryBySlf4j();
                    }
                } else if ("sysout".equalsIgnoreCase(listenerName)) {
                    builder.logQueryToSysOut();
                } else if ("count".equalsIgnoreCase(listenerName)) {
                    builder.countQuery();
                } else {
                    QueryExecutionListener listener = createNewInstance(QueryExecutionListener.class, listenerName);
                    builder.listener(listener);
                }
            }

            if (foramt != null && "json".equals(foramt.toLowerCase())) {
                builder.asJson();
            }
        }

        if (queryTransformer != null) {
            QueryTransformer transformer = createNewInstance(QueryTransformer.class, queryTransformer);
            builder.queryTransformer(transformer);
        }
        if (parameterTransformer != null) {
            ParameterTransformer transformer = createNewInstance(ParameterTransformer.class, parameterTransformer);
            builder.parameterTransformer(transformer);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    protected <T> T createNewInstance(Class<T> clazz, String className) throws Exception {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            String msg = String.format("Failed to create %s - %s: %s", clazz.getSimpleName(), e.getClass().getName(), e.getMessage());
            throw new Exception(msg);
        }
    }

    protected String getContentFromReference(Reference reference, String key) {
        RefAddr refAddr = reference.get(key);
        if (refAddr == null) {
            return null;
        }
        return refAddr.getContent().toString();
    }

    protected String[] getListenerNames(String listenerNames) {
        Set<String> listenerNameSet = new HashSet<String>();
        for (String listenerName : listenerNames.split(",")) {
            listenerNameSet.add(trim(listenerName));
        }
        return listenerNameSet.toArray(new String[listenerNameSet.size()]);

    }

    protected String trim(String s) {
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}