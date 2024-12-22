import threading
import time
import random
import mysql.connector
from mysql.connector import pooling

# 数据库连接池配置
db_pool_config = {
    'pool_name': 'mypool',
    'pool_size': 10,
    'user': 'gauss_db',
    'password': '----',
    'host': '----',
    'database': 'gauss',
    'raise_on_warnings': True
}

# SQL查询
query = "SELECT * FROM your_table WHERE id = %s"

def execute_query(query, id):
    try:
        connection = db_pool.get_connection()
        cursor = connection.cursor()
        cursor.execute(query, (id,))
        result = cursor.fetchall()
        print(f"Thread {threading.current_thread().name}: Result {result}")
    except mysql.connector.Error as err:
        print(f"Thread {threading.current_thread().name}: Error {err}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

def simulate_concurrent_queries(n_threads):
    threads = []
    for i in range(n_threads):
        t = threading.Thread(target=execute_query, args=(query, random.randint(1, 1000)), name=f"Thread-{i+1}")
        threads.append(t)
        t.start()
    
    # 等待所有线程完成
    for t in threads:
        t.join()

if __name__ == "__main__":
    db_pool = mysql.connector.pooling.MySQLConnectionPool(**db_pool_config)
    start_time = time.time()
    simulate_concurrent_queries(10)  # 模拟10个并发查询
    end_time = time.time()
    print(f"Total time: {end_time - start_time} seconds")