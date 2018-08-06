package cc.iliz.mybatis.shading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
//    	String sql = "select * from test_table1";
    	String sql = "select a.col_1,a.col_2,a.col_3 from test_table1 a where a.id in (select aid from test_table2 where col_1=1 and col_2=?) order by id desc";
    	String reg = "from\\s+(\\w+)";
    	Pattern pattern  = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(sql);
    	Integer count = 11111111;
    	while(matcher.find()){
    		System.out.println(matcher.group(1));
    		System.out.println(matcher.groupCount());
    		
    		
//    		sql = matcher.replaceFirst(count + "");
//    		System.out.println(sql);
//    		matcher.reset(sql);
//    		System.out.println(matcher.matches());
//    		System.out.println(matcher.regionEnd());
    		
    		
    		
    		
//    		System.out.println(sql);
//    		System.out.println(matcher.groupCount());
//    		System.out.println(matcher.group());
//    		String group = matcher.group();
//    		String[] names = matcher.group().split("\\s");
////    		sql = matcher.replaceAll("666666666");
////    		sql = matcher.replaceFirst("666666666");
////    		System.out.println(result);
////    		names[1] = "555555555";
//    		sql = sql.replaceAll(names[1], count+"");
//    		System.out.println(sql);
//    		System.out.println("============================");
    		count += 11111111;
//    		System.out.println(matcher.groupCount());
//    		MatchResult matchResult = matcher.toMatchResult();
    		
//    		System.out.println(sql);
//    		matcher = matcher.appendReplacement(new StringBuffer(matcher.group().split("\\s")[1]), ""+count);
//    		matcher.
//    		System.out.println(matcher.toString());
    	}
    }

   public void testInsert()
   {
//   	String sql = "select * from test_table1";
   	String sql = "INSERT INTO test_table1 VALUES (21, 01, 'Ottoman', ?,?)";
   	String reg = "insert\\s+into\\s+\\w+";
   	Pattern pattern  = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
   	Matcher matcher = pattern.matcher(sql);
   	while(matcher.find()){
   		System.out.println(matcher.group());
   	}
   }

   public void testUpdate()
   {
//   	String sql = "select * from test_table1";
   	String sql = "UPDATE test_table1 set col_1=123 ,col_2=?,col_3=? where col_4=?";
   	String reg = "update\\s+\\w+";
   	Pattern pattern  = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
   	Matcher matcher = pattern.matcher(sql);
   	while(matcher.find()){
   		System.out.println(matcher.group());
   	}
   }

   public void testDelete()
   {
//   	String sql = "select * from test_table1";
   	String sql = "delete from test_table2 where id in (?,?,?,?,?,?) and col_1 is not null";
   	String reg = "from\\s+\\w+";
   	Pattern pattern  = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
   	Matcher matcher = pattern.matcher(sql);
   	while(matcher.find()){
   		System.out.println(matcher.group());
   	}
   }
}
