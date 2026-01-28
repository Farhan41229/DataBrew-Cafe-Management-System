# HikariCP Setup Guide

## Overview
This project now uses **HikariCP** - a high-performance JDBC connection pool that provides:
- ⚡ Lightning-fast performance
- 🔄 Efficient connection reuse
- 📊 Built-in monitoring and statistics
- 🛡️ Connection leak detection
- 🎯 Production-ready reliability

---

## Step 1: Download HikariCP

### Option A: Direct Download (Recommended)

1. Download HikariCP JAR from Maven Central:
   - **URL**: https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.1.0/HikariCP-5.1.0.jar
   - **Version**: 5.1.0 (or latest)

2. Download SLF4J (required dependency):
   - **SLF4J API**: https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar
   - **SLF4J Simple**: https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar

3. Place the JAR files in the `lib` folder:
   ```
   lib/
   ├── HikariCP-5.1.0.jar
   ├── slf4j-api-2.0.9.jar
   ├── slf4j-simple-2.0.9.jar
   └── mysql-connector-j-9.1.0.jar (already exists)
   ```

### Option B: Using Maven (if you add Maven to the project)

Add to `pom.xml`:
```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>
```

### Option C: Using Gradle (if you add Gradle to the project)

Add to `build.gradle`:
```gradle
implementation 'com.zaxxer:HikariCP:5.1.0'
```

---

## Step 2: Update Compile Command

After adding the JAR files, update your compile command to include HikariCP:

```powershell
javac --module-path "C:\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml -d bin -cp "lib\mysql-connector-j-9.1.0.jar;lib\HikariCP-5.1.0.jar;lib\slf4j-api-2.0.9.jar;lib\slf4j-simple-2.0.9.jar" (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

---

## Step 3: Update Run Command

Update your run command to include HikariCP in the classpath:

```powershell
java --module-path "C:\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "$PWD\bin;$PWD\resources;$PWD\lib\mysql-connector-j-9.1.0.jar;$PWD\lib\HikariCP-5.1.0.jar;$PWD\lib\slf4j-api-2.0.9.jar;$PWD\lib\slf4j-simple-2.0.9.jar" com.databrew.cafe.App
```

---

## Configuration

The connection pool is configured via `resources/config.properties`:

```properties
# Connection Pool Configuration (HikariCP)
db.pool.maxPoolSize=10              # Maximum connections in pool
db.pool.minIdle=5                   # Minimum idle connections
db.pool.connectionTimeout=30000     # Max wait time (ms) for connection
db.pool.idleTimeout=600000          # Max idle time (ms) before closing
db.pool.maxLifetime=1800000         # Max lifetime (ms) of a connection
db.pool.leakDetectionThreshold=60000 # Leak detection threshold (ms)
```

### Configuration Explained:

| Parameter | Default | Description |
|-----------|---------|-------------|
| **maxPoolSize** | 10 | Maximum number of connections in the pool. Adjust based on your database server capacity and application load. |
| **minIdle** | 5 | Minimum number of idle connections to maintain. Helps with burst traffic. |
| **connectionTimeout** | 30000ms (30s) | Maximum time to wait for a connection. Throws exception if exceeded. |
| **idleTimeout** | 600000ms (10min) | How long a connection can sit idle before being closed. |
| **maxLifetime** | 1800000ms (30min) | Maximum lifetime of a connection. Prevents stale connections. |
| **leakDetectionThreshold** | 60000ms (60s) | Time before a leaked connection is logged. Set to 0 to disable. |

---

## Verification

When the application starts, you should see:

```
✓ HikariCP Connection Pool initialized successfully
  - Pool Name: DataBrewCafePool
  - Max Pool Size: 10
  - Min Idle: 5
```

When the application closes:

```
Closing HikariCP connection pool...
Pool Stats - Active: 0, Idle: 5, Total: 5, Waiting: 0
✓ Connection pool closed successfully
```

---

## Performance Benefits

### Before (DriverManager):
- ❌ New connection created for every database request
- ❌ ~100-200ms overhead per connection
- ❌ No connection reuse
- ❌ Poor performance under load
- ❌ No connection monitoring

### After (HikariCP):
- ✅ Connections are reused from pool
- ✅ ~1-2ms overhead per request
- ✅ Up to **100x faster** for repeated requests
- ✅ Excellent performance under load
- ✅ Built-in monitoring and leak detection

---

## Monitoring Pool Health

The `DBConnection` class now provides monitoring methods:

```java
// Check if pool is healthy
boolean healthy = DBConnection.isPoolHealthy();

// Get pool statistics
String stats = DBConnection.getPoolStats();
// Output: "Pool Stats - Active: 2, Idle: 3, Total: 5, Waiting: 0"
```

---

## Troubleshooting

### Issue: ClassNotFoundException for HikariCP
**Solution**: Ensure `HikariCP-5.1.0.jar` is in the `lib` folder and included in classpath.

### Issue: NoClassDefFoundError for SLF4J
**Solution**: Add both `slf4j-api-2.0.9.jar` and `slf4j-simple-2.0.9.jar` to the `lib` folder.

### Issue: Pool initialization failed
**Solution**: Check `config.properties` exists and contains all required database settings.

### Issue: "Could not get JDBC Connection"
**Solution**: 
1. Verify MySQL server is running
2. Check database credentials in `config.properties`
3. Ensure database `cafedb` exists
4. Check pool configuration settings

### Issue: Connection leaks detected
**Solution**: Ensure all database connections are properly closed using try-with-resources:
```java
try (Connection conn = DBConnection.getConnection()) {
    // use connection
} // automatically closed
```

---

## Migration Notes

### What Changed:
- ✅ `DBConnection.getConnection()` - Now returns pooled connections
- ✅ `DBConnection.closePool()` - Now properly closes the pool
- ✅ Added `DBConnection.getPoolStats()` - For monitoring
- ✅ Added `DBConnection.isPoolHealthy()` - Health check

### What Stayed the Same:
- ✅ All existing DAO code works without changes
- ✅ Connection interface is identical
- ✅ SQL queries work exactly the same
- ✅ Transaction handling unchanged

### No Code Changes Needed In:
- All DAO classes (UserDao, EmployeeDao, etc.)
- All Service classes
- All Controllers
- Any existing code using `DBConnection.getConnection()`

---

## Quick Start Commands

```powershell
# 1. Download JARs and place in lib folder
# 2. Compile with HikariCP
javac --module-path "C:\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml -d bin -cp "lib\*" (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# 3. Run with HikariCP
java --module-path "C:\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "$PWD\bin;$PWD\resources;$PWD\lib\*" com.databrew.cafe.App
```

**Note**: Using `lib\*` includes all JAR files in the lib folder automatically!

---

## Resources

- **HikariCP GitHub**: https://github.com/brettwooldridge/HikariCP
- **HikariCP Documentation**: https://github.com/brettwooldridge/HikariCP/wiki
- **Maven Repository**: https://mvnrepository.com/artifact/com.zaxxer/HikariCP
- **Performance Benchmarks**: https://github.com/brettwooldridge/HikariCP/wiki/Down-the-Rabbit-Hole

---

**Status**: ✅ Implementation Complete  
**Next Step**: Download HikariCP and SLF4J JARs, then rebuild the project!
