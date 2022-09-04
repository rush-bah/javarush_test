package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerCrudRepository extends CrudRepository<Player, Long>, JpaSpecificationExecutor<Player>, PagingAndSortingRepository<Player, Long>{

}


