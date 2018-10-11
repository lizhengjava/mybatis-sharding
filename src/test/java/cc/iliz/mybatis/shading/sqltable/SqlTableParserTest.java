package cc.iliz.mybatis.shading.sqltable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.iliz.mybatis.shading.db.ShardingEntry;
import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TestTable1TableStrategy;
import cc.iliz.mybatis.shading.strategy.User;

public class SqlTableParserTest {
	private SqlTableParser sqlTableParser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		if(sqlTableParser == null){
			sqlTableParser = SqlTableParserFactory.getInstance().getSqlTableParser();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void SelectSqlTest(){
//		String sql = "select * from test_table1";
		String sql = "select DISTINCT history.member_id, info.sys_id, info.member_name,info.cert_code,info.mobile,info.member_status status, history.current_status,history.org_id,history.merchant_code, history.create_time,history.update_time, history.remark, org.ORG_ZHNAME,merchant.MERCHANT_NAME_ZH FROM member_cancel_history history INNER JOIN (select member_id,member_status,mobile,member_name,cert_type,cert_code,sys_id from member_info union all select member_id,member_status,mobile,member_name,cert_type,cert_code,sys_id from member_info_temp) info ON history.member_id = info.member_id LEFT JOIN mc_organization org ON history.org_id = org.ORG_CODE LEFT JOIN mc_merchant merchant ON history.merchant_code = merchant.MERCHANT_CODE where 1 =1 and history.id in (select max(id)from member_cancel_history group by member_id) ORDER BY history.create_time desc";
//		String sql = "select * from (select * from test_table1)";
//		String sql = "SELECT * FROM Study RIGHT JOIN Student ON Study.Sno=Student.Sno";
//		String sql = "select tmp.uname,tmp.appname as appname from info,( select user.name as uname ,app.name as appname ,info.app_id as app_id from user,info,app where user.uid =info.uid and info.app_id = app.id) as  tmp where tmp.app_id = info.app_id";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User());
		System.out.println(entry.getSql());
	}

	@Test
	public void SelectSqlWithStrategyTest(){
		StrategyRegister.getInstance().register(TestTable1TableStrategy.class);
		String sql = "select a.col_1,a.col_2,a.col_3 from app_test a where a.id in (select aid from app_test where col_1=1 and col_2=?) order by id desc";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}
	

	@Test
	public void InsertSqlTest(){
		String sql = "INSERT INTO test_table1 VALUES (21, 01, 'Ottoman', ?,?)";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}

	@Test
	public void InsertSqlWithStrategyTest(){
		StrategyRegister.getInstance().register(TestTable1TableStrategy.class);
		String sql = "INSERT INTO test_table1 (BUYERID, SELLERID, ITEM) VALUES (01, 21, ?)";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}

	@Test
	public void UpdateSqlTest(){
		String sql = "update test_table1 set col_1=123 ,col_2=?,col_3=? where col_4=?";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}

	@Test
	public void UpdateSqlWithStrategyTest(){
		StrategyRegister.getInstance().register(TestTable1TableStrategy.class);
		String sql = "update test_table1 set col_1=?,col_2=col_2+1 where id in (?,?,?,?)";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}

	@Test
	public void DeleteSqlTest(){
		String sql = "delete from test_table2 where id in (?,?,?,?,?,?) and col_1 is not null";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User());
		System.out.println(entry.getSql());
	}

	@Test
	public void DeleteSqlWithStrategyTest(){
		StrategyRegister.getInstance().register(TestTable1TableStrategy.class);
		String sql = "delete from test_table1 where id in (?,?,?,?,?,?) and col_1 is not null";
		ShardingEntry entry = sqlTableParser.markShardingTable(sql, new User() );
		System.out.println(entry.getSql());
	}
}
