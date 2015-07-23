package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceMatcher {

    // TODO: create ExecutionTypeMatcher:  executions(0, is(batch())) && executions(0, batch())

    // assertThat(ds, executions(0, batch()));
    // assertThat(ds, executions(0, is(batch())));
    // assertThat(ds, executions(0, statement()));
    // assertThat(ds, executions(0, batchStatement()));
    // assertThat(ds, executions(0, statementOrBatchStatement()));
    // assertThat(ds, executions(0, prepared()));
    // assertThat(ds, executions(0, batchPrepared()));
    // assertThat(ds, executions(0, preparedOrBatchPrepared()));
    // assertThat(ds, executions(0, callable()));
    // assertThat(ds, executions(0, batchCallable()));
    // assertThat(ds, executions(0, preparedOrBatchCallable()));
    // assertThat(ds, executions(0, is(success()))));
    // assertThat(ds, firstStatement(query(...)));
    // assertThat(ds, firstBatchStatement(query(...)));

    public static Matcher<ProxyTestDataSource> firstStatement(Matcher<StatementExecution> statementMatcher) {
        String msg = "first statement";  // TODO: check message
        return new FeatureMatcher<ProxyTestDataSource, StatementExecution>(statementMatcher, msg, msg) {
            @Override
            protected StatementExecution featureValueOf(ProxyTestDataSource actual) {
                List<QueryExecution> queryExecutions = actual.getQueryExecutions();
                QueryExecution queryExecution = findFirstByType(queryExecutions, StatementExecution.class);
                // TODO: if not exist
                return (StatementExecution) queryExecution;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends QueryExecution> T findFirstByType(List<QueryExecution> executions, Class<T> type) {
        for (QueryExecution execution : executions) {
            if (type.isAssignableFrom(execution.getClass())) {
                return (T) execution;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "AA");
        map.put("a", "BB");
        System.out.println(map.get("a"));
    }


    public static Matcher<ProxyTestDataSource> executions(int index, ExecutionType executionType) {
        return executions(index, new ExecutionTypeMatcher(executionType));
    }

    public static Matcher<ProxyTestDataSource> executions(final int index, Matcher<QueryExecution> queryExecutionMatcher) {
        // TODO: check message
        String msg = "queryExecutions[" + index + "]";
        return new FeatureMatcher<ProxyTestDataSource, QueryExecution>(queryExecutionMatcher, msg, msg) {
            @Override
            protected QueryExecution featureValueOf(ProxyTestDataSource actual) {
                List<QueryExecution> queryExecutions = actual.getQueryExecutions();
                // TODO: list size check, and use try-catch
                QueryExecution queryExecution = queryExecutions.get(index);
                return queryExecution;
            }
        };
    }


}