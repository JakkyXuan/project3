import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PostgreSQLConcurrencyTest {

    // 数据库连接信息
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Wsmm5050544";

    // 并发线程数
    private static final int THREAD_COUNT = 100;

    // 每个线程执行的查询次数
    private static final int QUERY_COUNT_PER_THREAD = 7000;

    // 计数器，用于统计成功的查询次数
    private static final AtomicInteger successCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        // 使用线程池来管理线程
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        // CountDownLatch用于等待所有线程完成
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 创建多个线程来执行查询
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    runQueries();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await();

        long endTime = System.currentTimeMillis();

        // 计算总时间
        long totalTime = endTime - startTime;

        // 打印结果
        System.out.println("Total successful queries: " + successCount.get());
        System.out.println("Total time taken: " + totalTime + " ms");
        System.out.println("Throughput: " + (successCount.get() / (totalTime / 1000.0)) + " queries/sec");

        // 关闭线程池
        executorService.shutdown();
    }

    private static void runQueries() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (int i = 0; i < QUERY_COUNT_PER_THREAD; i++) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT 1")) {
                    statement.executeQuery();
                    successCount.incrementAndGet();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
