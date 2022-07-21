/*
 *    Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package integration.container.aurora.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import software.aws.rds.jdbc.proxydriver.ds.ProxyDriverDataSource;

public class AuroraPostgresDataSourceTest extends AuroraPostgresBaseTest {
  @Test
  public void testConnectionWithDataSourceClassName() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameAndCredentialProperties() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    targetDataSourceProps.setProperty("user", AURORA_POSTGRES_USERNAME);
    targetDataSourceProps.setProperty("password", AURORA_POSTGRES_PASSWORD);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    Connection conn = ds.getConnection();

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingProtocol() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        SQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingServer() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        SQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingDatabase() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingUser() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection("", AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingPassword() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, ""));
  }

  @Test
  public void testConnectionWithDataSourceClassNameMissingPropertyNames() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("serverName", POSTGRES_CLUSTER_URL);
    targetDataSourceProps.setProperty("databaseName", AURORA_POSTGRES_DB);
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    assertThrows(
        SQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrl() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    ds.setTargetDataSourceProperties(targetDataSourceProps);
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + "/" + AURORA_POSTGRES_DB);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrlWithCredentials() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    ds.setJdbcUrl(
        DB_CONN_STR_PREFIX
            + POSTGRES_CLUSTER_URL
            + ":" + AURORA_POSTGRES_PORT + "/"
            + AURORA_POSTGRES_DB
            + "?user=" + AURORA_POSTGRES_USERNAME
            + "&password=" + AURORA_POSTGRES_PASSWORD);

    Connection conn = ds.getConnection();

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrlWithPort() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/" + AURORA_POSTGRES_DB);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrlAndProperties() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setDatabasePropertyName("databaseName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    Properties targetDataSourceProps = new Properties();
    targetDataSourceProps.setProperty("databaseName", "proxy-driver-test-db");
    ds.setTargetDataSourceProperties(targetDataSourceProps);

    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + "/" + AURORA_POSTGRES_DB);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrlMissingPropertyNames() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + "/" + AURORA_POSTGRES_DB);

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithDataSourceClassNameUsingUrlMissingDatabase() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcProtocol(DB_CONN_STR_PREFIX);
    ds.setServerPropertyName("serverName");
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");

    ds.setTargetDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

    Properties targetDataSourceProps = new Properties();
    ds.setTargetDataSourceProperties(targetDataSourceProps);
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + "/");

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithUrl() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/" + AURORA_POSTGRES_DB);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithUrlWithCredentials() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");
    ds.setJdbcUrl(
        DB_CONN_STR_PREFIX
            + POSTGRES_CLUSTER_URL
            + ":" + AURORA_POSTGRES_PORT + "/"
            + AURORA_POSTGRES_DB
            + "?user=" + AURORA_POSTGRES_USERNAME
            + "&password=" + AURORA_POSTGRES_PASSWORD);

    Connection conn = ds.getConnection();

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithUrlMissingPort() throws SQLException {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + "/" + AURORA_POSTGRES_DB);

    Connection conn = ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD);

    assertTrue(conn.isWrapperFor(org.postgresql.PGConnection.class));
    assertEquals(conn.getCatalog(), AURORA_POSTGRES_DB);

    assertTrue(conn.isValid(10));
    conn.close();
  }

  @Test
  public void testConnectionWithUrlMissingDatabase() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/");

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithUrlMissingUser() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/");

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection("", AURORA_POSTGRES_PASSWORD));
  }

  @Test
  public void testConnectionWithUrlMissingPassword() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setUserPropertyName("user");
    ds.setPasswordPropertyName("password");
    ds.setPortPropertyName("port");
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/");

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, ""));
  }

  @Test
  public void testConnectionWithUrlMissingPropertyNames() {
    ProxyDriverDataSource ds = new ProxyDriverDataSource();
    ds.setJdbcUrl(DB_CONN_STR_PREFIX + POSTGRES_CLUSTER_URL + ":" + AURORA_POSTGRES_PORT + "/" + AURORA_POSTGRES_DB);

    assertThrows(
        PSQLException.class,
        () -> ds.getConnection(AURORA_POSTGRES_USERNAME, AURORA_POSTGRES_PASSWORD));
  }
}