package oit.is.team2.team2project.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChipMapper {

  @Insert("INSERT INTO chip (playerName, balance) VALUES (#{playerName}, #{balance})")
  int insertChip(Chip chip);

  @Select("SELECT playerName, balance FROM chip WHERE playerName = #{playerName}")
  Chip selectByPlayerName(@Param("playerName") String playerName);

  @Update("UPDATE chip SET balance = #{balance} WHERE playerName = #{playerName}")
  int updateBalance(@Param("playerName") String playerName, @Param("balance") int balance);
}
