package com.fecred.qzcxpt.mapper;

import com.fecred.qzcxpt.model.MPersonal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface IPersonal {

    @Select(value = "SELECT id, name, tid, sfzhm, ontid, running FROM cx_nmjmzgdaxxb WHERE TID IS NOT NULL AND ONTID IS NULL AND RUNNING = 0 LIMIT 1")
    MPersonal getUnONTIDPerson();

    @Update(value = "UPDATE cx_nmjmzgdaxxb SET ontid = #{ontid}, running = 0 WHERE TID = #{tid}")
    void Update(MPersonal mPersonal);
}
