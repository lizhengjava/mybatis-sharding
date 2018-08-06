package cc.iliz.mybatis.shading.plugin.domain;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

public interface AppTestMapper {
	Integer insert(AppTestDO domain);
	List<AppTestDO> getList(AppTestDO domain);

	@Insert("insert into app_test (ID,CNT )values (#{id},#{cnt})")
	Integer insert2(AppTestDO domain);
		
	@Insert("insert into app_test (ID,CNT )values (#{id},#{cnt})")
	@SelectKey(statement = "select seq_app_test_id.nextval from dual", 
			keyProperty = "id", before = true, resultType = int.class)
	Integer insertNoShard(AppTestDO domain);
	
	@Select("SELECT * FROM APP_TEST where id = #{id}")
	AppTestDO getOne(AppTestDO domain);
}
