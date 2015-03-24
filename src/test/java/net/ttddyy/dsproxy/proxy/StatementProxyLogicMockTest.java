package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


/**
 * @author Tadaya Tsuyukubo
 */
public class StatementProxyLogicMockTest {

    private static final String DS_NAME = "myDS";

    @Test
    public void testExecuteUpdate() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query)).thenReturn(100);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("executeUpdate", String.class);
        Object result = logic.invoke(method, new Object[]{query});

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verify(stat).executeUpdate(query);
        verifyListener(listener, "executeUpdate", query, query);
    }

    @Test
    public void testExecuteUpdateForException() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("executeUpdate", String.class);
            logic.invoke(method, new Object[]{query});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).executeUpdate(query);
        verifyListenerForException(listener, "executeUpdate", query, query);
    }

    @Test
    public void testExecuteUpdateWithAutoGeneratedKeys() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)).thenReturn(100);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("executeUpdate", String.class, int.class);
        Object result = logic.invoke(method, new Object[]{query, Statement.RETURN_GENERATED_KEYS});

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verify(stat).executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        verifyListener(listener, "executeUpdate", query, query, Statement.RETURN_GENERATED_KEYS);
    }

    @Test
    public void testExecuteUpdateWithAutoGeneratedKeysForException() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("executeUpdate", String.class, int.class);
            logic.invoke(method, new Object[]{query, Statement.RETURN_GENERATED_KEYS});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        verifyListenerForException(listener, "executeUpdate", query, query, Statement.RETURN_GENERATED_KEYS);
    }

    @Test
    public void testExecuteUpdateWithColumnIndexes() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";
        final int[] columnIndexes = {1, 2, 3};

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, columnIndexes)).thenReturn(100);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("executeUpdate", String.class, int[].class);
        Object result = logic.invoke(method, new Object[]{query, columnIndexes});

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verify(stat).executeUpdate(query, columnIndexes);
        verifyListener(listener, "executeUpdate", query, query, columnIndexes);
    }

    @Test
    public void testExecuteUpdateWithColumnIndexesForException() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";
        final int[] columnIndexes = {1, 2, 3};

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, columnIndexes)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("executeUpdate", String.class, int[].class);
            logic.invoke(method, new Object[]{query, columnIndexes});
            fail();
        } catch (SQLException e) {

        }

        verify(stat).executeUpdate(query, columnIndexes);
        verifyListenerForException(listener, "executeUpdate", query, query, columnIndexes);
    }

    @Test
    public void testExecuteUpdateWithColumnNames() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";
        final String[] columnNames = {"foo", "bar", "baz"};

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, columnNames)).thenReturn(100);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("executeUpdate", String.class, String[].class);
        Object result = logic.invoke(method, new Object[]{query, columnNames});

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verify(stat).executeUpdate(query, columnNames);
        verifyListener(listener, "executeUpdate", query, query, columnNames);
    }

    @Test
    public void testExecuteUpdateWithColumnNamesForException() throws Throwable {
        final String query = "insert into emp (id, name) values (1, 'foo')";
        final String[] columnNames = {"foo", "bar", "baz"};

        Statement stat = mock(Statement.class);
        when(stat.executeUpdate(query, columnNames)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("executeUpdate", String.class, String[].class);
            logic.invoke(method, new Object[]{query, columnNames});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).executeUpdate(query, columnNames);
        verifyListenerForException(listener, "executeUpdate", query, query, columnNames);
    }


    @Test
    public void testExecute() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        when(stat.execute(query)).thenReturn(true);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("execute", String.class);
        Object result = logic.invoke(method, new Object[]{query});

        assertThat(result, is(instanceOf(boolean.class)));
        assertTrue((Boolean) result);
        verify(stat).execute(query);
        verifyListener(listener, "execute", query, query);
    }

    @Test
    public void testExecuteForException() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        when(stat.execute(query)).thenThrow(new SQLException());
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("execute", String.class);
            logic.invoke(method, new Object[]{query});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).execute(query);
        verifyListenerForException(listener, "execute", query, query);
    }

    @Test
    public void testExecuteWithAutoGeneratedKeys() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        when(stat.execute(query, Statement.RETURN_GENERATED_KEYS)).thenReturn(true);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("execute", String.class, int.class);
        Object result = logic.invoke(method, new Object[]{query, Statement.RETURN_GENERATED_KEYS});

        assertThat(result, is(instanceOf(boolean.class)));
        assertTrue((Boolean) result);
        verify(stat).execute(query, Statement.RETURN_GENERATED_KEYS);
        verifyListener(listener, "execute", query, query, Statement.RETURN_GENERATED_KEYS);
    }

    @Test
    public void testExecuteWithAutoGeneratedKeysForException() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        when(stat.execute(query, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("execute", String.class, int.class);
            logic.invoke(method, new Object[]{query, Statement.RETURN_GENERATED_KEYS});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).execute(query, Statement.RETURN_GENERATED_KEYS);
        verifyListenerForException(listener, "execute", query, query, Statement.RETURN_GENERATED_KEYS);
    }

    @Test
    public void testExecuteWithColumnIndexes() throws Throwable {
        final String query = "select * from emp";
        final int[] columnIndexes = {1, 2, 3};

        Statement stat = mock(Statement.class);
        when(stat.execute(query, columnIndexes)).thenReturn(true);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("execute", String.class, int[].class);
        Object result = logic.invoke(method, new Object[]{query, columnIndexes});

        assertThat(result, is(instanceOf(boolean.class)));
        assertTrue((Boolean) result);
        verify(stat).execute(query, columnIndexes);
        verifyListener(listener, "execute", query, query, columnIndexes);
    }

    @Test
    public void testExecuteWithColumnIndexesForException() throws Throwable {
        final String query = "select * from emp";
        final int[] columnIndexes = {1, 2, 3};

        Statement stat = mock(Statement.class);
        when(stat.execute(query, columnIndexes)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("execute", String.class, int[].class);
            logic.invoke(method, new Object[]{query, columnIndexes});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).execute(query, columnIndexes);
        verifyListenerForException(listener, "execute", query, query, columnIndexes);
    }

    @Test
    public void testExecuteWithColumnNames() throws Throwable {
        final String query = "select * from emp";
        final String[] columnNames = {"foo", "bar", "baz"};

        Statement stat = mock(Statement.class);
        when(stat.execute(query, columnNames)).thenReturn(true);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("execute", String.class, String[].class);
        Object result = logic.invoke(method, new Object[]{query, columnNames});

        assertThat(result, is(instanceOf(boolean.class)));
        assertTrue((Boolean) result);
        verify(stat).execute(query, columnNames);
        verifyListener(listener, "execute", query, query, columnNames);

    }

    @Test
    public void testExecuteWithColumnNamesForException() throws Throwable {
        final String query = "select * from emp";
        final String[] columnNames = {"foo", "bar", "baz"};

        Statement stat = mock(Statement.class);
        when(stat.execute(query, columnNames)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("execute", String.class, String[].class);
            logic.invoke(method, new Object[]{query, columnNames});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).execute(query, columnNames);
        verifyListenerForException(listener, "execute", query, query, columnNames);

    }

    @Test
    public void testExecuteQuery() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        when(stat.executeQuery(query)).thenReturn(rs);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("executeQuery", String.class);
        Object result = logic.invoke(method, new Object[]{query});

        assertThat(result, is(instanceOf(ResultSet.class)));
        assertThat((ResultSet) result, is(rs));
        verify(stat).executeQuery(query);
        verifyListener(listener, "executeQuery", query, query);
    }

    @Test
    public void testExecuteQueryWithException() throws Throwable {
        final String query = "select * from emp";

        Statement stat = mock(Statement.class);
        when(stat.executeQuery(query)).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        try {
            Method method = Statement.class.getMethod("executeQuery", String.class);
            logic.invoke(method, new Object[]{query});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).executeQuery(query);
        verifyListenerForException(listener, "executeQuery", query, query);
    }

    private StatementProxyLogic getProxyLogic(Statement statement, QueryExecutionListener listener) {
        InterceptorHolder interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        return new StatementProxyLogic(statement, interceptorHolder, DS_NAME, new JdkJdbcProxyFactory());
    }

    @SuppressWarnings("unchecked")
    private void verifyListener(QueryExecutionListener listener, String methodName, String query, Object... methodArgs) {

        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is(methodName));

        assertThat(execInfo.getMethodArgs(), arrayWithSize(methodArgs.length));
        assertThat(execInfo.getMethodArgs(), arrayContaining(methodArgs));
        assertThat(execInfo.getDataSourceName(), is(DS_NAME));
        assertThat(execInfo.getThrowable(), is(nullValue()));
        assertThat(execInfo.isBatch(), is(false));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(1));
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery(), is(equalTo(query)));
    }

    @SuppressWarnings("unchecked")
    private void verifyListenerForException(QueryExecutionListener listener, String methodName,
                                            String query, Object... methodArgs) {

        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is(methodName));

        assertThat(execInfo.getMethodArgs(), arrayWithSize(methodArgs.length));
        assertThat(execInfo.getMethodArgs(), arrayContaining(methodArgs));
        assertThat(execInfo.getDataSourceName(), is(DS_NAME));
        assertThat(execInfo.getThrowable(), is(instanceOf(SQLException.class)));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(1));
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery(), is(equalTo(query)));
    }


    @Test
    public void testAddBatchException() throws Throwable {
        final String queryA = "insert into emp (id, name) values (1, 'foo')";
        final String queryB = "insert into emp (id, name) values (2, 'bar')";

        Statement stat = mock(Statement.class);
        doThrow(new SQLException()).when(stat).addBatch(queryB);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        Method method = Statement.class.getMethod("addBatch", String.class);
        logic.invoke(method, new Object[]{queryA});

        try {
            logic.invoke(method, new Object[]{queryB});
            fail();
        } catch (SQLException e) {
        }

        verify(stat).addBatch(queryA);

    }

    @Test
    public void testExecuteBatch() throws Throwable {
        final String queryA = "insert into emp (id, name) values (1, 'foo')";
        final String queryB = "insert into emp (id, name) values (2, 'bar')";
        final String queryC = "insert into emp (id, name) values (3, 'baz')";

        Statement stat = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        when(stat.executeQuery(queryA)).thenReturn(rs);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        // run
        Method method = Statement.class.getMethod("addBatch", String.class);
        logic.invoke(method, new Object[]{queryA});
        logic.invoke(method, new Object[]{queryB});
        logic.invoke(method, new Object[]{queryC});

        method = Statement.class.getMethod("executeBatch");
        Object result = logic.invoke(method, null);

        assertThat(result, is(nullValue()));
        verify(stat).addBatch(queryA);
        verify(stat).addBatch(queryB);
        verify(stat).addBatch(queryC);
        verify(stat).executeBatch();
        verifyListenerForExecuteBatch(listener, queryA, queryB, queryC);

    }

    @Test
    public void testExecuteBatchForException() throws Throwable {
        final String queryA = "insert into emp (id, name) values (1, 'foo')";
        final String queryB = "insert into emp (id, name) values (2, 'bar')";
        final String queryC = "insert into emp (id, name) values (3, 'baz')";

        Statement stat = mock(Statement.class);
        when(stat.executeBatch()).thenThrow(new SQLException());

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        // run
        Method method = Statement.class.getMethod("addBatch", String.class);
        logic.invoke(method, new Object[]{queryA});
        logic.invoke(method, new Object[]{queryB});
        logic.invoke(method, new Object[]{queryC});

        try {
            method = Statement.class.getMethod("executeBatch");
            logic.invoke(method, null);
            fail();
        } catch (SQLException e) {
        }

        verify(stat).addBatch(queryA);
        verify(stat).addBatch(queryB);
        verify(stat).addBatch(queryC);
        verify(stat).executeBatch();
        verifyListenerForExecuteBatchForException(listener, queryA, queryB, queryC);

    }

    @Test
    public void testExecuteBatchWithClearBatch() throws Throwable {
        final String queryA = "insert into emp (id, name) values (1, 'foo')";
        final String queryB = "insert into emp (id, name) values (2, 'bar')";
        final String queryC = "insert into emp (id, name) values (3, 'baz')";

        Statement stat = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        when(stat.executeQuery(queryA)).thenReturn(rs);

        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        StatementProxyLogic logic = getProxyLogic(stat, listener);

        // run
        Method addBatch = Statement.class.getMethod("addBatch", String.class);
        Method clearBatch = Statement.class.getMethod("clearBatch");
        Method executeBatch = Statement.class.getMethod("executeBatch");

        logic.invoke(addBatch, new Object[]{queryA});
        logic.invoke(clearBatch, null);
        logic.invoke(addBatch, new Object[]{queryB});
        logic.invoke(addBatch, new Object[]{queryC});
        Object result = logic.invoke(executeBatch, null);

        assertThat(result, is(nullValue()));
        verify(stat).addBatch(queryA);
        verify(stat).clearBatch();
        verify(stat).addBatch(queryB);
        verify(stat).addBatch(queryC);
        verify(stat).executeBatch();
        verifyListenerForExecuteBatch(listener, queryB, queryC);

    }

    @SuppressWarnings("unchecked")
    private void verifyListenerForExecuteBatch(QueryExecutionListener listener, String... queries) {
        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is("executeBatch"));
        assertThat(execInfo.getDataSourceName(), is(DS_NAME));
        assertThat(execInfo.getMethodArgs(), is(nullValue()));
        assertThat(execInfo.isBatch(), is(true));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();

        assertThat(queryInfoList, is(notNullValue()));
        assertThat(queryInfoList.size(), is(queries.length));

        for (int i = 0; i < queries.length; i++) {
            String expectedQuery = queries[i];
            QueryInfo queryInfo = queryInfoList.get(i);
            assertThat(queryInfo.getQuery(), is(expectedQuery));
            assertThat(queryInfo.getQueryArgs(), is(notNullValue()));
            assertThat(queryInfo.getQueryArgs().size(), is(0));
        }
    }

    @SuppressWarnings("unchecked")
    private void verifyListenerForExecuteBatchForException(QueryExecutionListener listener, String... queries) {
        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is("executeBatch"));
        assertThat(execInfo.getDataSourceName(), is(DS_NAME));
        assertThat(execInfo.getMethodArgs(), is(nullValue()));
        assertThat(execInfo.getThrowable(), is(instanceOf(SQLException.class)));
        assertThat(execInfo.isBatch(), is(true));


        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();

        assertThat(queryInfoList, is(notNullValue()));
        assertThat(queryInfoList.size(), is(queries.length));

        for (int i = 0; i < queries.length; i++) {
            String expectedQuery = queries[i];
            QueryInfo queryInfo = queryInfoList.get(i);
            assertThat(queryInfo.getQuery(), is(expectedQuery));
            assertThat(queryInfo.getQueryArgs(), is(notNullValue()));
            assertThat(queryInfo.getQueryArgs().size(), is(0));
        }

    }

    @Test
    public void testGetTarget() throws Throwable {
        Statement stmt = mock(Statement.class);
        StatementProxyLogic logic = getProxyLogic(stmt, null);

        Method method = ProxyJdbcObject.class.getMethod("getTarget");
        Object result = logic.invoke(method, null);

        assertThat(result, notNullValue());
        assertThat(result, is(instanceOf(Statement.class)));

        Statement resultStmt = (Statement) result;
        assertThat(resultStmt, is(sameInstance(stmt)));
    }

    @Test
    public void testUnwrap() throws Throwable {
        Statement stmt = mock(Statement.class);
        when(stmt.unwrap(String.class)).thenReturn("called");

        StatementProxyLogic logic = getProxyLogic(stmt, null);

        Method method = Statement.class.getMethod("unwrap", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(stmt).unwrap(String.class);
        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Throwable {
        Statement stmt = mock(Statement.class);
        when(stmt.isWrapperFor(String.class)).thenReturn(true);

        StatementProxyLogic logic = getProxyLogic(stmt, null);

        Method method = Statement.class.getMethod("isWrapperFor", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(stmt).isWrapperFor(String.class);
        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean) result, is(true));
    }

    @Test
    public void testGetConnection() throws Throwable {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);

        when(stmt.getConnection()).thenReturn(conn);
        StatementProxyLogic logic = getProxyLogic(stmt, null);

        Method method = Statement.class.getMethod("getConnection");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(Connection.class)));
        verify(stmt).getConnection();

        assertTrue(Proxy.isProxyClass(result.getClass()));

        InvocationHandler handler = Proxy.getInvocationHandler(result);
        assertThat(handler, is(instanceOf(ConnectionInvocationHandler.class)));

        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
        Object obj = ((ProxyJdbcObject) result).getTarget();

        assertThat(obj, is(instanceOf(Connection.class)));
        Connection resultConn = (Connection) obj;
        assertThat(resultConn, is(sameInstance(conn)));

    }

}
