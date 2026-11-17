package oit.is.team2.team2project.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChipMapper {

  @Insert("INSERT INTO chip (playerName, balance) VALUES (#{playerName}, #{balance})")
  int insertChip(Chip chip);

  @Select("SELECT playerName, balance FROM chip WHERE playerName = #{playerName}")
  Chip selectByPlayerName(@Param("playerName") String playerName);
}
