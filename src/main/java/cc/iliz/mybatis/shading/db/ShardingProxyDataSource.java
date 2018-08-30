package cc.iliz.mybatis.shading.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

public class ShardingProxyDataSource implements DataSource,Comparable<ShardingProxyDataSource> {
	private static final Log log = LogFactory.getLog(ShardingProxyDataSource.class);
	private DataSource dataSource;
	private Set<String> tableNames;
	private Integer order = 1;
	
	public static ShardingProxyDataSource instanceBuilder(DataSource dataSource,Integer order){
		ShardingProxyDataSource instance = new ShardingProxyDataSource();
		instance.setDataSource(dataSource);
		instance.setOrder(order);
		return instance;
	}

	public boolean checkDataSourceByTableName(String name){
		return tableNames.contains(name);
	}
	
	private void scanDatasourceScheme(){
			Connection conn = null;
			ResultSet rs = null;
			try {
				conn = dataSource.getConnection();
				DatabaseMetaData meta = conn.getMetaData();
				rs = meta.getTables(null, null, null, new String[] { "TABLE","VIEW" });
				Set<String> sets = new HashSet<String>();
				while (rs.next()) {  
					sets.add(rs.getString(3));
				}
				this.tableNames = sets;
			} catch (SQLException e) {
				log.error("扫描数据源元数据异常");
			}finally{
				try {
					if(rs != null){
						rs.close();
					}
					if(conn != null){
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		dataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		dataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return dataSource.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return dataSource.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return dataSource.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return dataSource.getConnection(username, password);
	}

	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		scanDatasourceScheme();
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Set<String> getTableNames() {
		return tableNames;
	}

	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(ShardingProxyDataSource o) {
		return o.getOrder().compareTo(getOrder());
	}
	
}
